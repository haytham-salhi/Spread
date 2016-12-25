package com.spread.util;

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
		
		values[0] = Double.parseDouble(stringValues[4]); // The number of them
		values[1] = Double.parseDouble(stringValues[6]); // The percentage of them
		
		return values;
	}

}
