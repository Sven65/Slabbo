package xyz.mackan.Slabbo.utils.locale;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;
import xyz.mackan.Slabbo.Slabbo;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocaleManager {
	public HashMap<String, String> translationMap = new HashMap<String, String>();

	public LocaleManager () {
		File languageFile = new File(Slabbo.getDataPath(), "lang.yml");
		InputStream defaultFile = Slabbo.getInstance().getResource("lang.yml");
		InputStreamReader reader = new InputStreamReader(defaultFile);

		YamlConfiguration defaultConf = YamlConfiguration.loadConfiguration(reader);
		YamlConfiguration languageConf = YamlConfiguration.loadConfiguration(languageFile);

		Set<String> keys = defaultConf.getKeys(true);

		for (String key : keys) {
			Object valueObj = defaultConf.get(key);

			if (valueObj instanceof MemorySection) continue;

			String value = (String) valueObj;
			String userValue = languageConf.getString(key);

			if(userValue == null || userValue.equals("")) {
				userValue = value;
			}

			translationMap.put(key, userValue);
		}
	}

	/**
	 * Gets a string from the translation map by key
	 * @param key The key to get
	 * @return String
	 */
	public String getString (String key) {
		String value = translationMap.get(key);

		if (value == null || value.equals("")) return "Translation key "+key+" not found!";

		return value;
	}

	/**
	 * Replaces a string from translation key and hashmap
	 * @param tlKey The key to use for getting the replacement string
	 * @param replacementMap The map to use for replacements
	 * @return String
	 */
	public String replaceKey (String tlKey, HashMap<String, Object> replacementMap) {
		String replaceString = getString(tlKey);

		for (Map.Entry<String, Object> replacement : replacementMap.entrySet()) {
			String key = replacement.getKey();
			String value = replacement.getValue().toString();

			replaceString = replaceString.replaceAll("\\{" + key + "\\}", Matcher.quoteReplacement(value));
		}

		return replaceString;
	}

	/**
	 * Replaces a string from hashmap
	 * @param replaceString The string to replace
	 * @param replacementMap The map to use for replacements
	 * @return String
	 */
	public static String replaceString (String replaceString, HashMap<String, Object> replacementMap) {
		for (Map.Entry<String, Object> replacement : replacementMap.entrySet()) {
			String key = replacement.getKey();
			String value = replacement.getValue().toString();

			replaceString = replaceString.replaceAll("\\{"+key+"\\}", value);
		}

		return replaceString;
	}

	/**
	 * Replaces a single key in a string from tlKey
	 * @param tlKey The key to use for getting the replacement string
	 * @param replaceKey The key to replace
	 * @param replaceValue The value to replace with
	 * @return String
	 */
	public String replaceSingleKey (String tlKey, String replaceKey, Object replaceValue) {
		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put(replaceKey, replaceValue);

		return replaceKey(tlKey, replacementMap);
	}

	/**
	 * Gets the currency string with the amount
	 * @param amount The amount of currency
	 * @return String
	 */
	public String getCurrencyString (Object amount) {
		return replaceSingleKey("general.currency-format", "amount", amount);
	}
}
