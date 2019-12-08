/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekatest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

/**
 *
 * @author user
 */
public class SimpleClassifyOnly {

    public static void main(String[] args) throws IOException, Exception {
        BufferedReader reader = new BufferedReader(new FileReader("C:/Users/user/Desktop/credit-g.arff"));
        ArffReader arff = new ArffReader(reader);
        Instances data = arff.getData();
        data.setClass(data.attribute("class"));
        RandomForest rf=new RandomForest();
        rf.setComputeAttributeImportance(true);
        rf.buildClassifier(data);
        CorrelationAttributeEval ce=new CorrelationAttributeEval();
        ce.buildEvaluator(data);
        for(int i=0;i<data.numAttributes();i++)
        {
            System.out.println(ce.evaluateAttribute(i));
        }
        data.setClassIndex(0);
        ce.buildEvaluator(data);
        for(int i=0;i<data.numAttributes();i++)
        {
            System.out.println(ce.evaluateAttribute(i));
        }
    }
}
