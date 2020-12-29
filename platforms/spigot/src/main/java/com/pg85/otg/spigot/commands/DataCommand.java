package com.pg85.otg.spigot.commands;

import com.pg85.otg.OTG;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DataCommand
{
	public static final String usage = "Usage: /otg data <type>";
	public static final List<String> dataTypes = new ArrayList<>(Arrays.asList(
			"biome",
			"block",
			"entity",
			"sound",
			"particle"
	));

	public static boolean execute(CommandSender sender, String[] args)
	{
		// /otg data music
		// /otg data sound
		if (args.length != 2)
		{
			sender.sendMessage(usage);
			sender.sendMessage("Data types: "+String.join(", ", dataTypes));
			return true;
		}

		IRegistry<?> registry;

		switch (args[1].toLowerCase())
		{
			case "biome":
				registry = ((CraftServer) Bukkit.getServer()).getServer().customRegistry.b(IRegistry.ay);
				break;
			case "block":
				registry = IRegistry.BLOCK;
				break;
			case "entity":
				registry = IRegistry.ENTITY_TYPE;
				break;
			case "sound":
				registry = IRegistry.SOUND_EVENT;
				break;
			case "particle":
				registry = IRegistry.PARTICLE_TYPE;
				break;
			default:
				sender.sendMessage("Data types: "+String.join(", ", dataTypes));
				return true;
		}
		Set<MinecraftKey> set = registry.keySet();
		new Thread(() -> {
			try
			{
				Path root = OTG.getEngine().getOTGRootFolder();
				String fileName = root.toString() + File.separator + "data-output-" + args[1] + ".txt".toLowerCase();
				File output = new File(fileName);
				FileWriter writer = new FileWriter(output);
				for (MinecraftKey key : set)
				{
					writer.write(key.toString()+"\n");
				}
				writer.close();
				sender.sendMessage("File exported as "+fileName);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}).start();
		return true;
	}

	public static List<String> tabComplete(String[] args)
	{
		return StringUtil.copyPartialMatches(args[1], dataTypes, new ArrayList<>());
	}
}
