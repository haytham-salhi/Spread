package com.spread.experiment.evaluation;

import java.util.HashMap;
import java.util.Map;

import weka.classifiers.evaluation.AbstractEvaluationMetric;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.InformationRetrievalEvaluationMetric;
import weka.classifiers.evaluation.InformationTheoreticEvaluationMetric;
import weka.classifiers.evaluation.StandardEvaluationMetric;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class OtherWayForMyMetric extends Evaluation {
	
	private static final long serialVersionUID = 5162566515285027655L;
	
	private Map<Integer, Integer> classIndexToUnclassifiedCountMap = new HashMap<Integer, Integer>();

	public OtherWayForMyMetric(Instances data) throws Exception {
		super(data);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public double truePositiveRate(int classIndex) {
		double correct = 0, total = 0;
	    for (int j = 0; j < m_NumClasses; j++) {
	      if (j == classIndex) {
	        correct += m_ConfusionMatrix[classIndex][j];
	      }
	      total += m_ConfusionMatrix[classIndex][j];
	    }
	    
	    // Begin: Haytham Logic
	    Integer UnclassifiedCountForClassIndex = classIndexToUnclassifiedCountMap.get(classIndex);
	    if(UnclassifiedCountForClassIndex != null && UnclassifiedCountForClassIndex > 0) {
	    	total += UnclassifiedCountForClassIndex;
	    }
	    // End: Haytham Logic
	    
	    if (total == 0) {
	      return 0;
	    }
	    return correct / total;
	}
	
	@Override
	protected void updateStatsForClassifier(double[] predictedDistribution,
			Instance instance) throws Exception {
		// TODO Auto-generated method stub
		int actualClass = (int) instance.classValue();

	    if (!instance.classIsMissing()) {
	      updateMargins(predictedDistribution, actualClass, instance.weight());

	      // Determine the predicted class (doesn't detect multiple
	      // classifications)
	      int predictedClass = -1;
	      double bestProb = 0.0;
	      for (int i = 0; i < m_NumClasses; i++) {
	        if (predictedDistribution[i] > bestProb) {
	          predictedClass = i;
	          bestProb = predictedDistribution[i];
	        }
	      }

	      m_WithClass += instance.weight();

	      // Determine misclassification cost
	      if (m_CostMatrix != null) {
	        if (predictedClass < 0) {
	          // For missing predictions, we assume the worst possible cost.
	          // This is pretty harsh.
	          // Perhaps we could take the negative of the cost of a correct
	          // prediction (-m_CostMatrix.getElement(actualClass,actualClass)),
	          // although often this will be zero
	          m_TotalCost +=
	            instance.weight() * m_CostMatrix.getMaxCost(actualClass, instance);
	        } else {
	          m_TotalCost +=
	            instance.weight()
	              * m_CostMatrix.getElement(actualClass, predictedClass, instance);
	        }
	      }

	      // Update counts when no class was predicted
	      if (predictedClass < 0) {
	        m_Unclassified += instance.weight();
	        
	        // Begin: Haytham's Logic
	        Integer UnclassfiedCountForClass = classIndexToUnclassifiedCountMap.get((int) instance.classValue());
	        if(UnclassfiedCountForClass == null) {
	        	// It is a new
	        	classIndexToUnclassifiedCountMap.put((int) instance.classValue(), (int) instance.weight());
	        } else {
	        	classIndexToUnclassifiedCountMap.put((int) instance.classValue(), (int) (UnclassfiedCountForClass + instance.weight()));
	        }
	        // End: Haytham's Logic 
	        
	        return;
	      }

	      double predictedProb =
	        Math.max(MIN_SF_PROB, predictedDistribution[actualClass]);
	      double priorProb =
	        Math.max(MIN_SF_PROB, m_ClassPriors[actualClass] / m_ClassPriorsSum);
	      if (predictedProb >= priorProb) {
	        m_SumKBInfo +=
	          (Utils.log2(predictedProb) - Utils.log2(priorProb))
	            * instance.weight();
	      } else {
	        m_SumKBInfo -=
	          (Utils.log2(1.0 - predictedProb) - Utils.log2(1.0 - priorProb))
	            * instance.weight();
	      }

	      m_SumSchemeEntropy -= Utils.log2(predictedProb) * instance.weight();
	      m_SumPriorEntropy -= Utils.log2(priorProb) * instance.weight();

	      updateNumericScores(predictedDistribution,
	        makeDistribution(instance.classValue()), instance.weight());

	      // Update coverage stats
	      int[] indices = Utils.stableSort(predictedDistribution);
	      double sum = 0, sizeOfRegions = 0;
	      for (int i = predictedDistribution.length - 1; i >= 0; i--) {
	        if (sum >= m_ConfLevel) {
	          break;
	        }
	        sum += predictedDistribution[indices[i]];
	        sizeOfRegions++;
	        if (actualClass == indices[i]) {
	          m_TotalCoverage += instance.weight();
	        }
	      }
	      m_TotalSizeOfRegions += instance.weight() * sizeOfRegions / (m_MaxTarget - m_MinTarget);

	      // Update other stats
	      m_ConfusionMatrix[actualClass][predictedClass] += instance.weight();
	      if (predictedClass != actualClass) {
	        m_Incorrect += instance.weight();
	      } else {
	        m_Correct += instance.weight();
	      }
	    } else {
	      m_MissingClass += instance.weight();
	    }

	    if (m_pluginMetrics != null) {
	      for (AbstractEvaluationMetric m : m_pluginMetrics) {
	        if (m instanceof StandardEvaluationMetric) {
	          ((StandardEvaluationMetric) m).updateStatsForClassifier(
	            predictedDistribution, instance);
	        } else if (m instanceof InformationRetrievalEvaluationMetric) {
	          ((InformationRetrievalEvaluationMetric) m).updateStatsForClassifier(
	            predictedDistribution, instance);
	        } else if (m instanceof InformationTheoreticEvaluationMetric) {
	          ((InformationTheoreticEvaluationMetric) m).updateStatsForClassifier(
	            predictedDistribution, instance);
	        }
	      }
	    }
	}
	
	public Map<Integer, Integer> getClassIndexToUnclassifiedCountMap() {
		return classIndexToUnclassifiedCountMap;
	}
}
