/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekatest;

import weka.classifiers.UpdateableClassifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author user
 */
public class UpdatableMultilayerPerceptron extends CustomMultilayerPerceptron implements UpdateableClassifier {

    public void updateClassifier(Instances instncs) throws Exception {
        m_accepted = false;
        m_epoch = 0;
        m_error = 0;
        m_stopIt = true;
        m_stopped = true;
        m_currentInstance = null;
        m_instances = new Instances(instncs);
        m_instances.randomize(m_random);

        // For the given number of iterations
        while (next()) {
        }

        // Clean up
        done();
    }
    
    @Override
    public void updateClassifier(Instance instnc) throws Exception {
        m_accepted = false;
        m_epoch = 0;
        m_error = 0;
        m_stopIt = true;
        m_stopped = true;
        m_currentInstance = null;
        m_instances.add(instnc);// = new Instances(instnc);
        m_instances.randomize(m_random);

        // For the given number of iterations
        while (next()) {
        }

        // Clean up
        done();
    }

}
