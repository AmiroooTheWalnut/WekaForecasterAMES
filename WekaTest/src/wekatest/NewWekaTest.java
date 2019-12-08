/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekatest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.TestCase.fail;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SGD;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.CSVLoader;
import weka.filters.supervised.attribute.TSLagMaker;
import weka.filters.unsupervised.attribute.Add;

/**
 *
 * @author user
 */
public class NewWekaTest {

    public Instances getData() throws IOException {
//        CSVLoader csvLoader=new CSVLoader();
//        csvLoader.setSource(new File("F:/My_software_develops/IOTSimulationIBM/SECOM_Dataset_filled_noConstant_numericTime.csv"));
//        Instances data=csvLoader.getDataSet();

//        CSVLoader csvLoader = new CSVLoader();
//        csvLoader.setSource(new File("F:/My_software_develops/IOTSimulationIBM/SmallPredictionModel/WekaTimeseriesForecasting/credit-g.arff"));
//        Instances data = csvLoader.getDataSet();

//        BufferedReader reader =new BufferedReader(new FileReader("F:/My_software_develops/IOTSimulationIBM/SmallPredictionModel/WekaTimeseriesForecasting/cpu.arff"));
//        ArffReader arff = new ArffReader(reader);
//        Instances data = arff.getData();


        BufferedReader reader = new BufferedReader(new FileReader("F:/My_software_develops/IOTSimulationIBM/SmallPredictionModel/WekaTimeseriesForecasting/credit-g.arff"));
        ArffReader arff = new ArffReader(reader);
        Instances data = arff.getData();

//        System.out.println(data.attribute(0).numValues());
        return data;
    }

    public NewWekaTest() throws IOException {
        boolean success = false;
        Instances data = getData();
        
        Instances data1 = new Instances(data,0,40);
        System.out.println(data1);
        Instances data2 = new Instances(data,40,80);
        System.out.println(data2);
        
        Instances data3 = new Instances(data,80,120);
//        System.out.println(data3);
        Instances data4 = new Instances(data,120,160);
//        System.out.println(data2);
        
//    weka.test.Regression reg = new weka.test.Regression(this.getClass());

        WekaForecaster forecaster = new WekaForecaster();
        TSLagMaker lagMaker = forecaster.getTSLagMaker();

        try {
            ArrayList<String> fieldsToForecast = new ArrayList();

//            fieldsToForecast.add("Sensor1");
//            fieldsToForecast.add("Sensor2");
//            fieldsToForecast.add("Sensor3");
//            fieldsToForecast.add("Target");
//            fieldsToForecast.add("MYCT");
//            fieldsToForecast.add("MMIN");
//            fieldsToForecast.add("MMAX");
//            fieldsToForecast.add("CACH");
//            fieldsToForecast.add("CHMIN");
//            fieldsToForecast.add("CHMAX");

//            fieldsToForecast.add("employment");
//            fieldsToForecast.add("installment_commitment");
//            fieldsToForecast.add("personal_status");
//            fieldsToForecast.add("other_parties");
            fieldsToForecast.add("checking_status");
            fieldsToForecast.add("duration");
            fieldsToForecast.add("credit_history");
            fieldsToForecast.add("purpose");
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
//            UpdatableMultilayerPerceptron model = new UpdatableMultilayerPerceptron();//***
//            model.setTrainingTime(10);//***
//            KStar model = new KStar();
            
//            data1.setClassIndex(0);
//            
//            model.buildClassifier(data1);
//            
//            Evaluation eval = new Evaluation(data1);
//            
//            double evals[]=eval.evaluateModel(model, data1);
//            
//            for(int i=0;i<evals.length;i++)
//            {
//                System.out.println(evals[i]);
//            }
//            
//            System.out.println("Going to update");
//            
//            data2.setClassIndex(0);
//            
//            model.updateClassifier(data2.get(0));
//            
//            evals=eval.evaluateModel(model, data1);
//            
//            for(int i=0;i<evals.length;i++)
//            {
//                System.out.println(evals[i]);
//            }
            
            

            forecaster.setBaseForecaster(model);
            forecaster.buildForecaster(data2, System.out);
            forecaster.primeForecaster(data2);

            int numStepsToForecast = 12;
            List<List<Prediction>> forecast
                    = forecaster.forecast(numStepsToForecast, System.out);

            String forecastString = predsToString(forecast, numStepsToForecast);
            success = true;
            System.out.println(success);
            System.out.println(forecastString);
            
            forecaster.updateClassifier(data3);
            forecaster.primeForecaster(data3);
            
            List<List<Prediction>> forecast1
                    = forecaster.forecast(numStepsToForecast, System.out);

            String forecastString1 = predsToString(forecast1, numStepsToForecast);
            success = true;
            System.out.println(success);
            System.out.println(forecastString1);
            
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
        NewWekaTest thisClass = new NewWekaTest();
    }
}
