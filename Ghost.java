package com.cerbghosts;

import net.runelite.api.NPC;

final class Ghost
{
    final NPC npc;
    GhostStyle style = GhostStyle.UNKNOWN;
    int orderIndex = -1;

    Ghost(NPC npc)
    {
        this.npc = npc;
    }
}
