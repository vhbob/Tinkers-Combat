package com.vhbob.tinkerscombat.util;

import org.bukkit.ChatColor;

public class ChatUtils {

	public static String parseColor(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

}
