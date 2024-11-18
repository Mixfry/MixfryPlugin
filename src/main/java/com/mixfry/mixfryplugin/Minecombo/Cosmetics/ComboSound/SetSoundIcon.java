package com.mixfry.mixfryplugin.Minecombo.Cosmetics.ComboSound;

import org.bukkit.Material;
import org.bukkit.Sound;

public class SetSoundIcon {

    public static Sound ComboSoundIcon(Material material) {
        switch (material) {
            case BARRIER:
                return null;
            case ICE:
                return Sound.BLOCK_NOTE_BLOCK_CHIME;
            case SOUL_SAND:
                return Sound.BLOCK_NOTE_BLOCK_COW_BELL;
            case BONE_BLOCK:
                return Sound.BLOCK_NOTE_BLOCK_XYLOPHONE;
            case CLAY:
                return Sound.BLOCK_NOTE_BLOCK_FLUTE;
            case EMERALD_BLOCK:
                return Sound.BLOCK_NOTE_BLOCK_BIT;
            case IRON_BLOCK:
                return Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE;
            case GOLD_BLOCK:
                return Sound.BLOCK_NOTE_BLOCK_BELL;
            case STONE:
                return Sound.BLOCK_NOTE_BLOCK_BASEDRUM;
            default:
                return null;
        }
    }
}