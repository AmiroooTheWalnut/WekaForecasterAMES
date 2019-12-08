/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekatest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;
import junit.framework.TestCase;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.eval.TSEvaluation;
import weka.core.Instances;
import weka.filters.supervised.attribute.TSLagMaker;

/**
 *
 * @author user
 */
public class EvalTest extends TestCase {
  
  public EvalTest(String name) {
    super(name);
  }

  private String predsToString(List<List<Prediction>> preds, int steps) {
    StringBuffer b = new StringBuffer();

    for (int i = 0; i < steps; i++) {
      List<Prediction> predsForTargetsAtStep = 
        preds.get(i);
      
      for (int j = 0; j < predsForTargetsAtStep.size(); j++) {
        Prediction p = predsForTargetsAtStep.get(j);
        double[][] limits=null;
        if(p instanceof NumericPrediction)
        {
            limits = ((NumericPrediction)p).predictionIntervals();
        }
        
        b.append(p.predicted() + " ");
        if (limits != null && limits.length > 0) {
          b.append(limits[0][0] + " " + limits[0][1] + " ");
        }
      }
      b.append("\n");
    }

    return b.toString();
  }

  public Instances getData(String name) {
    Instances data = null;
    try {
      data =
        new Instances(new BufferedReader(new InputStreamReader(
          ClassLoader.getSystemResourceAsStream("weka/classifiers/timeseries/data/" + name))));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return data;
  }

  public void testRegressionForecastTwoTargetsConfidenceIntervals() throws Exception {

    boolean success = false;
    Instances wine = getData("wine_date.arff");
//    weka.test.Regression reg = new weka.test.Regression(this.getClass());

    WekaForecaster forecaster = new WekaForecaster();
    TSLagMaker lagMaker = forecaster.getTSLagMaker();

    try {
      forecaster.setFieldsToForecast("Fortified,Dry-white");
      forecaster.setCalculateConfIntervalsForForecasts(12);
      lagMaker.setTimeStampField("Date");
      lagMaker.setMinLag(1);
      lagMaker.setMaxLag(12);
      lagMaker.setAddMonthOfYear(true);
      lagMaker.setAddQuarterOfYear(true);
      forecaster.buildForecaster(wine, System.out);
      forecaster.primeForecaster(wine);

      int numStepsToForecast = 12;
      List<List<Prediction>> forecast = 
        forecaster.forecast(numStepsToForecast, System.out);
    
      String forecastString = predsToString(forecast, numStepsToForecast);
      success = true;
//      reg.println(forecastString);
    } catch (Exception ex) {
      ex.printStackTrace();
      String msg = ex.getMessage().toLowerCase();
      if (msg.indexOf("not in classpath") > -1) {
        return;
      }
    }
    
    if (!success) {
      fail("Problem during regression testing: no successful predictions generated");
    }

//    try {
//      String diff = reg.diff();
//      if (diff == null) {
//        System.err.println("Warning: No reference available, creating.");
//      } else if (!diff.equals("")) {
//        fail("Regression test failed. Difference:\n" + diff);
//      }
//    } catch (IOException ex) {
//      fail("Problem during regression testing.\n" + ex);
//    }
  }

//  public static Test suite() {
//    return new TestSuite(weka.classifiers.timeseries.WekaForecasterTest.class);
//  }

//  public static void main(String[] args) {
//    junit.textui.TestRunner.run(suite());
//  }
}