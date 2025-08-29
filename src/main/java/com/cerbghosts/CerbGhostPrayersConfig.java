package com.cerbghosts;

import net.runelite.client.config.*;

@ConfigGroup("cerbghostprayers")
public interface CerbGhostPrayersConfig extends Config
{
    @ConfigItem(
        keyName = "onlyInLair",
        name = "Only in Cerberus lair",
        description = "Hide overlay outside Cerberus rooms")
    default boolean onlyInLair() { return true; }

    @Range(min = 12, max = 48)
    @ConfigItem(
        keyName = "iconSize",
        name = "Icon size",
        description = "Prayer icon pixel size")
    default int iconSize() { return 28; }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "iconYOffset",
        name = "Icon Y offset",
        description = "Vertical offset from ghost head (pixels)")
    default int iconYOffset() { return 32; }

    @ConfigItem(
        keyName = "showOrder",
        name = "Show 1‑2‑3 order",
        description = "Display west→east attack order numbers")
    default boolean showOrder() { return true; }

    // Advanced: if NPC id mapping ever changes, you can paste IDs here.
    // Comma‑separated NPC IDs for each ghost color/type.
    @ConfigItem(
        keyName = "magicNpcIds",
        name = "Magic ghost NPC IDs",
        description = "Comma‑separated IDs for blue/staff ghost (optional)")
    default String magicNpcIds() { return ""; }

    @ConfigItem(
        keyName = "rangedNpcIds",
        name = "Ranged ghost NPC IDs",
        description = "Comma‑separated IDs for green/bow ghost (optional)")
    default String rangedNpcIds() { return ""; }

    @ConfigItem(
        keyName = "meleeNpcIds",
        name = "Melee ghost NPC IDs",
        description = "Comma‑separated IDs for red/sword ghost (optional)")
    default String meleeNpcIds() { return ""; }
}
