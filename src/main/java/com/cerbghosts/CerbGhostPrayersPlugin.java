package com.cerbghosts;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.*;
import javax.inject.Inject;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "Cerberus Ghost Prayers",
    description = "Shows the correct protection prayer above Cerberus' Summoned Souls",
    tags = {"cerberus","prayer","ghosts","pvm"},
    enabledByDefault = false
)
public class CerbGhostPrayersPlugin extends Plugin
{
    // If you want to hardcode NPC IDs, add them here once discovered via Dev Tools.
    private Set<Integer> MAGIC_IDS = Collections.emptySet();
    private Set<Integer> RANGED_IDS = Collections.emptySet();
    private Set<Integer> MELEE_IDS = Collections.emptySet();

    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private CerbGhostPrayersOverlay overlay;
    @Inject private CerbGhostPrayersConfig config;

    private final Map<NPC, Ghost> ghosts = new HashMap<>();

    // Known Cerberus lair map regions (all three rooms share regions 4883/5140/5397 depending on instance).
    // We keep this permissive by just requiring Cerberus to be present if "only in lair" is on.
    private static final Set<String> SUMMONED_SOUL_NAMES = ImmutableSet.of("Summoned soul", "Summoned Soul");

    @Provides
    CerbGhostPrayersConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CerbGhostPrayersConfig.class);
    }

    @Override
    protected void startUp()
    {
        loadIdConfig();
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        ghosts.clear();
    }

    Map<NPC, Ghost> getGhosts()
    {
        return ghosts;
    }

    boolean shouldRender()
    {
        if (!config.onlyInLair())
        {
            return true;
        }
        // Render only if Cerberus or Summoned Souls are present in the scene (covers instances).
        for (NPC n : client.getNpcs())
        {
            final String name = n.getName();
            if (name == null) continue;
            if (name.equalsIgnoreCase("Cerberus") || SUMMONED_SOUL_NAMES.contains(name))
            {
                return true;
            }
        }
        return false;
    }

    private void loadIdConfig()
    {
        MAGIC_IDS = parseIdCsv(config.magicNpcIds());
        RANGED_IDS = parseIdCsv(config.rangedNpcIds());
        MELEE_IDS = parseIdCsv(config.meleeNpcIds());
    }

    private static Set<Integer> parseIdCsv(String csv)
    {
        if (csv == null || csv.trim().isEmpty())
        {
            return Collections.emptySet();
        }
        Set<Integer> out = new HashSet<>();
        for (String tok : csv.split(","))
        {
            try { out.add(Integer.parseInt(tok.trim())); } catch (NumberFormatException ignore) { }
        }
        return out;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned e)
    {
        NPC npc = e.getNpc();
        final String name = npc.getName();
        if (name != null && SUMMONED_SOUL_NAMES.contains(name))
        {
            Ghost g = new Ghost(npc);
            g.style = guessStyleFromNpcId(npc.getId());
            ghosts.put(npc, g);
            updateOrderIndices();
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned e)
    {
        if (ghosts.remove(e.getNpc()) != null)
        {
            updateOrderIndices();
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if (ghosts.isEmpty()) return;
        updateOrderIndices(); // keep order stable as they shuffle
    }

    private void updateOrderIndices()
    {
        List<Ghost> list = new ArrayList<>(ghosts.values());
        // West -> East order (smaller world X is further west)
        list.sort(Comparator.comparingInt(g -> g.npc.getWorldLocation().getX()));
        for (int i = 0; i < list.size(); i++)
        {
            list.get(i).orderIndex = i + 1;
        }
    }

    private GhostStyle guessStyleFromNpcId(int id)
    {
        if (MAGIC_IDS.contains(id)) return GhostStyle.MAGIC;
        if (RANGED_IDS.contains(id)) return GhostStyle.RANGED;
        if (MELEE_IDS.contains(id)) return GhostStyle.MELEE;
        return GhostStyle.UNKNOWN; // overlay will still place a number; you can fill IDs from Dev Tools.
    }
}
