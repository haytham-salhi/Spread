package com.spread.util;

import java.util.Arrays;

/**
 * 
 * @author Haytham Salhi
 *
 */
public class WekaHelper {
	
	/**
	 * [0] --> number
	 * [1] --> percentage
	 * 
	 * @param evaluationString
	 * @return
	 */
	public static double[] getIncorrectlyClassifiedInstances(String evaluationString) {
		double[] values = new double[2];
		
		String[] stringValues = evaluationString.substring(evaluationString.indexOf("Incorrectly"), evaluationString.length() - 1).split("[ \r\n\t]");
		// Fix: remove the empty strings!
		String[] newStringValues = Arrays.stream(stringValues).filter(n -> !n.isEmpty()).toArray(String[]::new);
		
		values[0] = Double.parseDouble(newStringValues[4]); // The number of them
		values[1] = Double.parseDouble(newStringValues[5]); // The percentage of them
		
		return values;
	}

}
