package com.spread.util.charts;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.PieDataset;

/**
 * A custom pie chart for Spread
 * 
 * @author Haytham Salhi
 *
 */
public class SpreadPieChart {
	
	private JFreeChart pieChart;
	
	public SpreadPieChart(String title,
            PieDataset dataset,
            boolean legend, boolean tooltips, boolean urls) {
		pieChart = ChartFactory.createPieChart(title, dataset, legend, tooltips, urls);
		
		// Change the background
		PiePlot plot = (PiePlot) pieChart.getPlot();
		plot.setBackgroundPaint(Color.white);
	}
	
	public JFreeChart getPieChart() {
		return pieChart;
	}
}
