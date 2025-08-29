package com.cerbghosts;

import net.runelite.api.Prayer;
import net.runelite.api.SpriteID;

public enum GhostStyle
{
    MAGIC(Prayer.PROTECT_FROM_MAGIC, SpriteID.Prayeron.PROTECT_FROM_MAGIC),
    RANGED(Prayer.PROTECT_FROM_MISSILES, SpriteID.Prayeron.PROTECT_FROM_MISSILES),
    MELEE(Prayer.PROTECT_FROM_MELEE, SpriteID.Prayeron.PROTECT_FROM_MELEE),
    UNKNOWN(null, -1);

    private final Prayer prayer;
    private final int spriteId;

    GhostStyle(Prayer prayer, int spriteId)
    {
        this.prayer = prayer;
        this.spriteId = spriteId;
    }

    public Prayer getPrayer() { return prayer; }
    public int getSpriteId() { return spriteId; }
}
