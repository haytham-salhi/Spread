package com.spread.charts;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.spread.config.RootConfig;
import com.spread.experiment.RawSearchResult;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.SearchEngine;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;

public class ChartTest {
	
	
	@Before
	public void setUp() throws Exception {
	}
	
	
	@Test
	public void barChartsTest() throws Exception {
		
		JFreeChart barChart = ChartFactory.createBarChart("هيثم صالحي", "Data Size", "Percentage of Correctly Clustered Instances (%)", createDataset(), PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot plot = (CategoryPlot)barChart.getPlot();
		plot.getRangeAxis().setRange(0, 100);
		plot.setBackgroundPaint(Color.white);
		
		final CategoryItemRenderer renderer = plot.getRenderer();

		((BarRenderer)renderer).setBarPainter(new StandardBarPainter());
		
		((BarRenderer)renderer).setShadowVisible(true);
		// To color the bars
		renderer.setSeriesPaint(0, new Color(76,174,227));
		renderer.setSeriesPaint(1, new Color(243,164,71));
		
		// To color the dash line
		plot.setDomainGridlinePaint(Color.blue);
		plot.setRangeMinorGridlinePaint(Color.blue);
		plot.setRangeGridlinePaint(Color.blue);

		
		int width = 800;//640; /* Width of the image */
	    int height = 500;//480; /* Height of the image */ 
	    File BarChart = new File( "BarChart.png" );
	      
	      ChartUtilities.saveChartAsPNG( BarChart , barChart , width , height );
	}


	private CategoryDataset createDataset() {
		final String dataSize1 = "10";
		  final String dataSize2 = "100";
		  final String title = "Title";
		  final String snippet = "Snippet";
		  final String title_snippet = "Title_Snippet";
		
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		  dataset.addValue( 30 , new Integer(10) , title );
		  dataset.addValue( 40 , new Integer(10) , snippet );
		  dataset.addValue( 50 , new Integer(10) , title_snippet );
		
		  dataset.addValue(10 , new Integer(20) , title );
		  dataset.addValue(12 , new Integer(20) , snippet );
		  dataset.addValue( 11 , new Integer(20) , title_snippet );
		  
		  dataset.addValue(10 , new Integer(30) , title );
		  dataset.addValue(12 , new Integer(30) , snippet );
		  dataset.addValue( 11 , new Integer(30) , title_snippet );
		  
		  dataset.addValue(10 , new Integer(40) , title );
		  dataset.addValue(12 , new Integer(40) , snippet );
		  dataset.addValue( 11 , new Integer(40) , title_snippet );
		  
		  dataset.addValue(10 , new Integer(50) , title );
		  dataset.addValue(12 , new Integer(50) , snippet );
		  dataset.addValue( 11 , new Integer(50) , title_snippet );
		  
		  dataset.addValue(10 , new Integer(60) , title );
		  dataset.addValue(12 , new Integer(60) , snippet );
		  dataset.addValue( 11 , new Integer(60) , title_snippet );
		  
		  dataset.addValue(10 , new Integer(70) , title );
		  dataset.addValue(12 , new Integer(70) , snippet );
		  dataset.addValue( 11 , new Integer(70) , title_snippet );
		  
		  dataset.addValue(10 , new Integer(80) , title );
		  dataset.addValue(12 , new Integer(80) , snippet );
		  dataset.addValue( 11 , new Integer(80) , title_snippet );
		  
		  dataset.addValue(10 , new Integer(90) , title );
		  dataset.addValue(12 , new Integer(90) , snippet );
		  dataset.addValue( 11 , new Integer(90) , title_snippet );
		  
		  
		  dataset.addValue(10 , new Integer(100) , title );
		  dataset.addValue(12 , new Integer(100) , snippet );
		  dataset.addValue( 11 , new Integer(100) , title_snippet );
		  
		  
		  return dataset;
	}
	
	@Test
	public void pieChartsTest() throws Exception {
		DefaultPieDataset dataset = new DefaultPieDataset( );
	      dataset.setValue( "IPhone 5s" , new Double( 20 ) );  
	      dataset.setValue( "SamSung Grand" , new Double( 20 ) );   
	      dataset.setValue( "MotoG" , new Double( 40 ) );    
	      dataset.setValue( "Nokia Lumia" , new Double( 10 ) );  
	      
		JFreeChart pieChart = ChartFactory.createPieChart("Cluster Analysis", dataset, true, true, false);
		
		PiePlot plot = (PiePlot) pieChart.getPlot();
		plot.setBackgroundPaint(Color.white);
		
		
		int width = 640; /* Width of the image */
	      int height = 480; /* Height of the image */ 
	      File pieChartFile = new File( "PieChart.jpeg" ); 
	      ChartUtilities.saveChartAsPNG( pieChartFile , pieChart , width , height );
	}
		
}
