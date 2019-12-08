/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekatest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.gui.ForecastingPanel;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.supervised.attribute.TSLagMaker;

/**
 *
 * @author user
 */
public class WekaTest {
    
    public WekaForecaster m_forecaster = new WekaForecaster();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        // TODO code application logic here
        
        CSVLoader csvLoader=new CSVLoader();
        csvLoader.setSource(new File("F:/My_software_develops/IOTSimulationIBM/SECOM_Dataset_filled_noConstant_numericTime.csv"));
        
        Instances data=csvLoader.getDataSet();
        
        // new forecaster
      WekaForecaster forecaster = new WekaForecaster();
      TSLagMaker lagMaker = forecaster.getTSLagMaker();
      
      lagMaker.setTimeStampField("");
 
      List<String> targets=new ArrayList<String>();
      for(int i=0;i<data.numAttributes();i++)
      {
          targets.add(data.attribute(i).name());
      }
      
      System.out.println(lagMaker.getTimeStampField());
      
      lagMaker.setTimeStampField("Time");
      
//      lagMaker.setArtificialTimeStartValue(1);
      
      // set the targets we want to forecast. This method calls
      // setFieldsToLag() on the lag maker object for us
      forecaster.setFieldsToForecast("Sensor1,Sensor2,Sensor3");
 
      // default underlying classifier is SMOreg (SVM) - we'll use
      // gaussian processes for regression instead
      forecaster.setBaseForecaster(new GaussianProcesses());
      
 
//      forecaster.getTSLagMaker().setTimeStampField("Date"); // date time stamp
//      forecaster.getTSLagMaker().setMinLag(1);
//      forecaster.getTSLagMaker().setMaxLag(12); // monthly data
// 
//      // add a month of the year indicator field
//      forecaster.getTSLagMaker().setAddMonthOfYear(true);
// 
//      // add a quarter of the year indicator field
//      forecaster.getTSLagMaker().setAddQuarterOfYear(true);
 
      // build the model
      forecaster.buildForecaster(data);
 
      // prime the forecaster with enough recent historical data
      // to cover up to the maximum lag. In our case, we could just supply
      // the 12 most recent historical instances, as this covers our maximum
      // lag period
      forecaster.primeForecaster(data);
 
      // forecast for 12 units (months) beyond the end of the
      // training data
      List<List<Prediction>> forecast = forecaster.forecast(12, System.out);
 
      // output the predictions. Outer list is over the steps; inner list is over
      // the targets
      for (int i = 0; i < 12; i++) {
        List<Prediction> predsAtStep = forecast.get(i);
        for (int j = 0; j < 2; j++) {
          Prediction predForTarget = predsAtStep.get(j);
          System.out.print("" + predForTarget.predicted() + " ");
        }
        System.out.println();
      }
 
      // we can continue to use the trained forecaster for further forecasting
      // by priming with the most recent historical data (as it becomes available).
      // At some stage it becomes prudent to re-build the model using current
      // historical data.
        
    }
    
}
