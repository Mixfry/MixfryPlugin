package com.mixfry.mixfryplugin.Minecombo;

import org.bukkit.ChatColor;

public class ComboColor {

    public static String getGradientText(String text, int combo) {
        ChatColor[] colors;
        if (combo >= 15000) {
            colors = new ChatColor[]{
                    ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN,
                    ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_PURPLE
            };
        } else if (combo >= 10000) {
            colors = new ChatColor[]{
                    ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.GREEN,
                    ChatColor.AQUA, ChatColor.BLUE, ChatColor.WHITE
            };
        } else if (combo < 2000) {
            colors = new ChatColor[]{ChatColor.WHITE, ChatColor.GRAY, ChatColor.DARK_GRAY};
        } else if (combo < 2500) {
            colors = new ChatColor[]{ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.BLUE};
        } else if (combo < 3000) {
            colors = new ChatColor[]{ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW};
        } else if (combo < 3500) {
            colors = new ChatColor[]{ChatColor.RED, ChatColor.DARK_RED, ChatColor.GOLD};
        } else if (combo < 4000) {
            colors = new ChatColor[]{ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.BLUE};
        } else if (combo < 4500) {
            colors = new ChatColor[]{ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED};
        } else if (combo < 5000) {
            colors = new ChatColor[]{ChatColor.DARK_BLUE, ChatColor.BLUE, ChatColor.AQUA};
        } else if (combo < 5500) {
            colors = new ChatColor[]{ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.YELLOW};
        } else if (combo < 6000) {
            colors = new ChatColor[]{ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD};
        } else if (combo < 6500) {
            colors = new ChatColor[]{ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, ChatColor.BLUE};
        } else if (combo < 7000) {
            colors = new ChatColor[]{ChatColor.GOLD, ChatColor.YELLOW, ChatColor.RED};
        } else if (combo < 7500) {
            colors = new ChatColor[]{ChatColor.BLUE, ChatColor.AQUA, ChatColor.DARK_BLUE};
        } else if (combo < 8000) {
            colors = new ChatColor[]{ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW};
        } else if (combo < 8500) {
            colors = new ChatColor[]{ChatColor.RED, ChatColor.DARK_RED, ChatColor.GOLD};
        } else if (combo < 9000) {
            colors = new ChatColor[]{ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.BLUE};
        } else {
            colors = new ChatColor[]{ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED};
        }
        StringBuilder gradientText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            gradientText.append(colors[i % colors.length]).append(text.charAt(i));
        }
        return gradientText.toString();
    }

    public static String getComboName(int combo) {
        if (combo >= 1500) {
            return getGradientText(combo + " Combo!", combo);
        } else {
            ChatColor color;
            switch ((combo / 100) % 15) {
                case 1: color = ChatColor.DARK_GRAY; break;
                case 2: color = ChatColor.GRAY; break;
                case 3: color = ChatColor.WHITE; break;
                case 4: color = ChatColor.AQUA; break;
                case 5: color = ChatColor.DARK_AQUA; break;
                case 6: color = ChatColor.BLUE; break;
                case 7: color = ChatColor.DARK_BLUE; break;
                case 8: color = ChatColor.DARK_GREEN; break;
                case 9: color = ChatColor.GREEN; break;
                case 10: color = ChatColor.YELLOW; break;
                case 11: color = ChatColor.GOLD; break;
                case 12: color = ChatColor.RED; break;
                case 13: color = ChatColor.DARK_RED; break;
                case 14: color = ChatColor.LIGHT_PURPLE; break;
                case 15: color = ChatColor.DARK_PURPLE; break;
                default: color = ChatColor.WHITE; break;
            }
            return color.toString() + combo + " Combo!";
        }
    }

    public static ChatColor getLevelColor(int level) {
        if (level >= 2560) {
            return ChatColor.MAGIC; // 虹色
        }
        int cycle = (level / 10) % 16;
        switch (cycle) {
            case 0: return ChatColor.DARK_GRAY;
            case 1: return ChatColor.GRAY;
            case 2: return ChatColor.WHITE;
            case 3: return ChatColor.AQUA;
            case 4: return ChatColor.DARK_AQUA;
            case 5: return ChatColor.BLUE;
            case 6: return ChatColor.DARK_BLUE;
            case 7: return ChatColor.DARK_GREEN;
            case 8: return ChatColor.GREEN;
            case 9: return ChatColor.YELLOW;
            case 10: return ChatColor.GOLD;
            case 11: return ChatColor.RED;
            case 12: return ChatColor.DARK_RED;
            case 13: return ChatColor.LIGHT_PURPLE;
            case 14: return ChatColor.DARK_PURPLE;
            case 15: return ChatColor.BLACK;
            default: return ChatColor.DARK_PURPLE;
        }
    }

    public static ChatColor getBorderColor(int level) {
        if (level >= 2560) {
            return ChatColor.MAGIC; // 虹色
        }
        int cycle = level / 160;
        switch (cycle) {
            case 0: return ChatColor.DARK_GRAY;
            case 1: return ChatColor.GRAY;
            case 2: return ChatColor.WHITE;
            case 3: return ChatColor.AQUA;
            case 4: return ChatColor.DARK_AQUA;
            case 5: return ChatColor.BLUE;
            case 6: return ChatColor.DARK_BLUE;
            case 7: return ChatColor.DARK_GREEN;
            case 8: return ChatColor.GREEN;
            case 9: return ChatColor.YELLOW;
            case 10: return ChatColor.GOLD;
            case 11: return ChatColor.RED;
            case 12: return ChatColor.DARK_RED;
            case 13: return ChatColor.LIGHT_PURPLE;
            case 14: return ChatColor.DARK_PURPLE;
            case 15: return ChatColor.BLACK;
            default: return ChatColor.DARK_PURPLE;
        }
    }

    public static String getRainbowText(String text) {
        ChatColor[] colors = {
                ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN,
                ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_PURPLE
        };
        StringBuilder rainbowText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            rainbowText.append(colors[i % colors.length]).append(text.charAt(i));
        }
        rainbowText.append(ChatColor.RESET);
        return rainbowText.toString();
    }
}