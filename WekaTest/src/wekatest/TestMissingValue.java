/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekatest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.TestCase.fail;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.supervised.attribute.TSLagMaker;

/**
 *
 * @author user
 */
public class TestMissingValue {
    
    public Instances getData(String name) throws IOException {
//        CSVLoader csvLoader=new CSVLoader();
//        csvLoader.setSource(new File("F:/My_software_develops/IOTSimulationIBM/SECOM_Dataset_filled_noConstant_numericTime.csv"));
//        Instances data=csvLoader.getDataSet();

//        CSVLoader csvLoader = new CSVLoader();
//        csvLoader.setSource(new File("F:/My_software_develops/IOTSimulationIBM/SmallPredictionModel/WekaTimeseriesForecasting/credit-g.arff"));
//        Instances data = csvLoader.getDataSet();

//        BufferedReader reader =new BufferedReader(new FileReader("F:/My_software_develops/IOTSimulationIBM/SmallPredictionModel/WekaTimeseriesForecasting/cpu.arff"));
//        ArffReader arff = new ArffReader(reader);
//        Instances data = arff.getData();


        BufferedReader reader = new BufferedReader(new FileReader(name));
        ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
        Instances data = arff.getData();

//        System.out.println(data.attribute(0).numValues());
        return data;
    }
    
    TestMissingValue() throws IOException
    {
        boolean success = false;
        Instances dataBefore = getData("F:\\My_software_develops\\IOTSimulationIBM\\SmallPredictionModel\\WekaTimeseriesForecasting\\TestBefore_missingValue.arff");
        
        System.out.println(dataBefore);
        
        Instances dataAfter = getData("F:\\My_software_develops\\IOTSimulationIBM\\SmallPredictionModel\\WekaTimeseriesForecasting\\TestAfter_missingValue.arff");
        
        System.out.println(dataAfter);

        WekaForecaster forecaster = new WekaForecaster();
        TSLagMaker lagMaker = forecaster.getTSLagMaker();

        try {
            ArrayList<String> fieldsToForecast = new ArrayList();

            fieldsToForecast.add("a");
            fieldsToForecast.add("b");
            fieldsToForecast.add("c");
            forecaster.setFieldsToForecast(fieldsToForecast);
            forecaster.setCalculateConfIntervalsForForecasts(12);

            lagMaker.setTimeStampField("Time");

//            lagMaker.setTimeStampField("class");
            lagMaker.setMinLag(1);
            lagMaker.setMaxLag(12);
//            lagMaker.setAddMonthOfYear(true);
//            lagMaker.setAddQuarterOfYear(true);
            lagMaker.setFieldsToLag(fieldsToForecast);
            
            RandomForest model = new RandomForest();

            forecaster.setBaseForecaster(model);
            forecaster.buildForecaster(dataBefore, System.out);
            forecaster.primeForecaster(dataAfter);

            int numStepsToForecast = 1;
            List<List<Prediction>> forecast
                    = forecaster.forecast(numStepsToForecast, System.out);

            String forecastString = predsToString(forecast, numStepsToForecast);
            success = true;
            System.out.println(success);
            System.out.println(forecastString);
            
//            forecaster.updateClassifier(data3);
//            forecaster.primeForecaster(data3);
//            
//            List<List<Prediction>> forecast1
//                    = forecaster.forecast(numStepsToForecast, System.out);
//
//            String forecastString1 = predsToString(forecast1, numStepsToForecast);
//            success = true;
//            System.out.println(success);
//            System.out.println(forecastString1);
            
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
    }
    
    private String predsToString(List<List<Prediction>> preds, int steps) {
        StringBuffer b = new StringBuffer();

        for (int i = 0; i < steps; i++) {
            List<Prediction> predsForTargetsAtStep
                    = preds.get(i);

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
    
    public static void main(String[] args) throws IOException {
        TestMissingValue thisClass = new TestMissingValue();
    }
}
