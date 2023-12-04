package org.sdutPmLab;

import org.sdutPmLab.utils.LogReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class HeuristicMiner {

    public static void main(String[] args) {
//        String filePath = "";
//        HashMap<String, Integer> traceAndFrequencyMap = LogReader.getTraceAndFrequencyMap(filePath);
        String filePath = "src/main/java/org/sdutPmLab/data/running-example2.csv";
        getAllActivitiesConnectedHeuristicMatrix(filePath);
    }

    public static void getAllActivitiesConnectedHeuristicMatrix(String logPath) {
        HashMap<String, Integer> traceAndFrequencyMap = LogReader.getTraceAndFrequencyMap(logPath);

        // HashMap<String, Integer>(key: AE, value: 1)
        HashMap<String, Integer> tupleAndFrequencyMap = new HashMap<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : traceAndFrequencyMap.entrySet()) {
            String key = stringIntegerEntry.getKey();
            Integer value = stringIntegerEntry.getValue();
            for (int i = 0, j = i + 1; i < key.length() - 1; i ++, j ++ ) {
                String tmpTuple = key.charAt(i) + String.valueOf(key.charAt(j));
                if (!tupleAndFrequencyMap.containsKey(tmpTuple)) {
                    tupleAndFrequencyMap.put(tmpTuple, value);
                } else {
                    tupleAndFrequencyMap.put(tmpTuple, tupleAndFrequencyMap.get(tmpTuple) + value);
                }
            }
        }

//        for (Map.Entry<String, Integer> stringIntegerEntry : tupleAndFrequencyMap.entrySet()) {
//            System.out.println(stringIntegerEntry.getKey() + ": " + stringIntegerEntry.getValue());
//        }

        // calc
        for (Map.Entry<String, Integer> stringIntegerEntry : tupleAndFrequencyMap.entrySet()) {
            String key = stringIntegerEntry.getKey();
            Integer value = stringIntegerEntry.getValue();


        }
    }

    public static void calcDependencyRelationValue() {
    }
}
