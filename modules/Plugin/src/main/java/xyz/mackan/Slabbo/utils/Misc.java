package xyz.mackan.Slabbo.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Misc {
	public static String countStacks (int items) {
		double stacks = items / 64;

		NumberFormat formatter = new DecimalFormat("#0.00");

		return formatter.format(stacks);
	}
}
