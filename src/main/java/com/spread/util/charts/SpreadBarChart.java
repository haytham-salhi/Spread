package com.spread.util.charts;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;

/**
 * A custom barchart for Spread
 * 
 * @author Haytham Salhi
 *
 */
public class SpreadBarChart {
	
	private JFreeChart barChart;
	
	public SpreadBarChart(double lower, double upper, String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, PlotOrientation orientation,
            boolean legend, boolean tooltips, boolean urls) {
		barChart = ChartFactory.createBarChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		CategoryPlot plot = (CategoryPlot)barChart.getPlot();
		plot.getRangeAxis().setRange(lower, upper);
		plot.setBackgroundPaint(Color.white);
		
		final CategoryItemRenderer renderer = plot.getRenderer();
		
		// Set standard printer for bars
		((BarRenderer)renderer).setBarPainter(new StandardBarPainter());
		
		((BarRenderer)renderer).setShadowVisible(true);
		// To color the bars
		renderer.setSeriesPaint(0, new Color(76,174,227));
		renderer.setSeriesPaint(1, new Color(243,164,71));
		
		// To color the dash line
		plot.setDomainGridlinePaint(Color.blue);
		plot.setRangeMinorGridlinePaint(Color.blue);
		plot.setRangeGridlinePaint(Color.blue);
	}
	
	public JFreeChart getBarChart() {
		return barChart;
	}
}
