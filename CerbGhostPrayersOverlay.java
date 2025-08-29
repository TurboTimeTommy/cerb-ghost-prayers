package com.cerbghosts;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.*;

import net.runelite.client.game.SpriteManager;

public class CerbGhostPrayersOverlay extends Overlay
{
    private final Client client;
    private final CerbGhostPrayersPlugin plugin;
    private final CerbGhostPrayersConfig config;
    private final SpriteManager spriteManager;

    private BufferedImage mageIcon, rangeIcon, meleeIcon;

    @Inject
    public CerbGhostPrayersOverlay(Client client,
                                   CerbGhostPrayersPlugin plugin,
                                   CerbGhostPrayersConfig config,
                                   SpriteManager spriteManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        if (!plugin.shouldRender())
        {
            return null;
        }

        ensureIconsLoaded();

        for (Ghost ghost : plugin.getGhosts().values())
        {
            final NPC npc = ghost.npc;
            if (npc.isDead() || npc.isHidden()) continue;

            final BufferedImage icon = switch (ghost.style)
            {
                case MAGIC -> mageIcon;
                case RANGED -> rangeIcon;
                case MELEE -> meleeIcon;
                default -> null;
            };

            final LocalPoint lp = npc.getLocalLocation();
            if (lp == null) continue;

            if (icon != null)
            {
                drawImageOverActor(g, lp, icon, config.iconYOffset(), config.iconSize());
            }

            if (config.showOrder() && ghost.orderIndex > 0)
            {
                drawOrderText(g, lp, ghost.orderIndex, icon == null ? 0 : (config.iconYOffset() + config.iconSize()/2 + 12));
            }
        }
        return null;
    }

    private void ensureIconsLoaded()
    {
        if (mageIcon == null)  mageIcon  = spriteManager.getSprite(GhostStyle.MAGIC.getSpriteId(), 0);
        if (rangeIcon == null) rangeIcon = spriteManager.getSprite(GhostStyle.RANGED.getSpriteId(), 0);
        if (meleeIcon == null) meleeIcon = spriteManager.getSprite(GhostStyle.MELEE.getSpriteId(), 0);
    }

    private void drawImageOverActor(Graphics2D g, LocalPoint lp, BufferedImage img, int yOffset, int size)
    {
        if (img == null) return;
        final Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        final Point p = Perspective.getCanvasImageLocation(client, lp, img, yOffset);
        if (p != null)
        {
            final int x = p.getX() - size / 2;
            final int y = p.getY() - size / 2;
            g.drawImage(scaled, x, y, null);
        }
    }

    private void drawOrderText(Graphics2D g, LocalPoint lp, int number, int extraYOffset)
    {
        final String text = Integer.toString(number);
        final Point p = Perspective.getCanvasTextLocation(client, lp, text, extraYOffset);
        if (p == null) return;

        final Font old = g.getFont();
        g.setFont(old.deriveFont(Font.BOLD, 14f));

        final int w = g.getFontMetrics().stringWidth(text);
        final int h = g.getFontMetrics().getAscent();

        // simple outline for readability
        g.setColor(Color.BLACK);
        g.drawString(text, p.getX() - w/2 + 1, p.getY() + 1);
        g.drawString(text, p.getX() - w/2 - 1, p.getY() - 1);
        g.setColor(Color.WHITE);
        g.drawString(text, p.getX() - w/2, p.getY());

        g.setFont(old);
    }
}
