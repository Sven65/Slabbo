package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;
import xyz.mackan.Slabbo.Slabbo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
	public static void getVersion (final Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(Slabbo.getInstance(), () -> {
			try (
					InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=81368").openStream();
					Scanner scanner = new Scanner(inputStream)
			){
				if (scanner.hasNext()) {
					consumer.accept(scanner.next());
				}
			} catch (IOException exception) {
				Slabbo.log.info("Cannot look for updates: " + exception.getMessage());
			}
		});
	}
}
