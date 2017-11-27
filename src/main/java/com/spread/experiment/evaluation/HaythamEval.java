package com.spread.experiment.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.evaluation.AbstractEvaluationMetric;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.StandardEvaluationMetric;
import weka.core.Instance;
import weka.core.Utils;

public class HaythamEval extends AbstractEvaluationMetric implements StandardEvaluationMetric {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8922037544308608897L;

	@Override
	public boolean appliesToNominalClass() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean appliesToNumericClass() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getMetricName() {
		// TODO Auto-generated method stub
		return "SobheMetric";
	}

	@Override
	public String getMetricDescription() {
		// TODO Auto-generated method stub
		return "Description of SobheMetric";
	}

	@Override
	public List<String> getStatisticNames() {
		// TODO Auto-generated method stub
		return Arrays.asList(new String[] {"haystat1", "haystat2"});
	}

	@Override
	public double getStatistic(String statName) {
		if(statName.equals("haystat1")) {
			
			ArrayList<Prediction> pred= this.m_baseEvaluation.predictions();
		    double [] predictedVals=new double[pred.size()];
		    double [] actualVals=new double[pred.size()];
		    
		    for(int i=0;i<pred.size();i++){
		    		actualVals[i]=pred.get(i).actual();
		    		predictedVals[i]=pred.get(i).predicted();
		    }
		    
		    return 1 + 1;
		} else {
			return 2 + 1;
		}
	}

	@Override
	public String toSummaryString() {
		 return       "Kendall's tau                      " + Utils.doubleToString(getStatistic("haystat1"), 12, 4) + "\n";
	}

	@Override
	public void updateStatsForClassifier(double[] predictedDistribution,
			Instance instance) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatsForPredictor(double predictedValue, Instance instance)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
