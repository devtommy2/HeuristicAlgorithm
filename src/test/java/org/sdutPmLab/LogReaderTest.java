package org.sdutPmLab;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.sdutPmLab.utils.LogReader;

import java.util.Set;

/**
 * @author tommy
 * @version 1.0
 * @date 2023/12/4 2:16 PM
 */
public class LogReaderTest extends TestCase {

    public String filePath = "src/main/java/org/sdutPmLab/data/running-example2.csv";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LogReaderTest( String testName )
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

    public void testgetAllActivityInLog() {
        Set<String> allActivityInLog = LogReader.getAllActivityInLog(filePath);
        System.out.println(allActivityInLog);
//        System.out.println(1);
    }

    public void testCalcIntegerToDouble() {
        int a = 1;
        int b = 2;

        Double c = (a + 0.0) / b;

        System.out.println(c);
    }

}

