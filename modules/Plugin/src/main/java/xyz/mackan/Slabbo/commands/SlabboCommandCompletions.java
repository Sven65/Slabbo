package xyz.mackan.Slabbo.commands;

import xyz.mackan.Slabbo.Slabbo;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SlabboCommandCompletions {

	public static List<String> getImportFiles () {
		File folder = new File(Slabbo.getDataPath());

		return Arrays.stream(folder.listFiles())
				.filter(file -> file.isFile() && !file.getName().equalsIgnoreCase("shops.yml") && file.getName().endsWith(".yml"))
				.map(file -> file.getName())
				.collect(Collectors.toList());
	}
}
