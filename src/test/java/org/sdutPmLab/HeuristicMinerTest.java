package org.sdutPmLab;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.sdutPmLab.utils.LogReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author tommy
 * @version 1.0
 * @date 2023/12/4 2:43 PM
 */
public class HeuristicMinerTest extends TestCase {

    public String filePath = "src/main/java/org/sdutPmLab/data/running-example2.csv";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public HeuristicMinerTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testGetAllActivitiesConnectedHeuristicMatrix() {
        HashMap<String, Double> allActivitiesConnectedHeuristicMatrix = HeuristicMiner.getAllActivitiesConnectedHeuristicMatrix(filePath, 0.1, 3, 0.2);

//        for (Map.Entry<String, Double> stringDoubleEntry : allActivitiesConnectedHeuristicMatrix.entrySet()) {
//            String key = stringDoubleEntry.getKey();
//            Double value = stringDoubleEntry.getValue();
//
//            System.out.println(key + ": " + value);
//        }
    }

    public void testtraceAndFrequencyMap() {
        HashMap<String, Integer> traceAndFrequencyMap = LogReader.getTraceAndFrequencyMap(filePath);
        System.out.println(traceAndFrequencyMap);
    }

    public void testStringContains() {
        String a = "asdasdasd";

//        System.out.println(a.contains("a"));
    }


}
