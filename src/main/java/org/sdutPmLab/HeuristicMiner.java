package org.sdutPmLab;

import org.sdutPmLab.utils.LogReader;

import java.util.*;

/**
 * @author tommy
 */
public class HeuristicMiner {

    /**
     * result Format:
     * BD: 0.9090909090909091
     * ED: 0.9090909090909091
     * AB: 0.9090909090909091
     * CD: 0.9
     * AC: 0.9
     * AE: 0.9090909090909091
     */
    public static HashMap<String, Double> getAllActivitiesConnectedHeuristicMatrix(String logPath,
                                                                                   Double dependencyThreshold,
                                                                                   Integer positiveObservationThreshold,
                                                                                   Double relativeToBestThreshold) {
        // {AED=9, ABCED=1, AD=1, AECBD=1, ACBD=1, ABCD=9}
        HashMap<String, Integer> traceAndFrequencyMap = LogReader.getTraceAndFrequencyMap(logPath);

        // activity pair's structor of HashMap<String, Integer>(key: AE, value: 1)
        // get activity-activity pair's frequency in hashmap to calc dependency value
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

        // calc
        Set<String> allActivitySet = LogReader.getAllActivityInLog(logPath);
        HashMap<String, Double> activityPairDependencyValueMap = new HashMap<>();
        for (String activityI : allActivitySet) {
            for (String activityJ : allActivitySet) {
                // calc activityI-activityJ's Dependency Value using formula was given
                calcDependencyRelationValue(activityI, activityJ, activityPairDependencyValueMap, tupleAndFrequencyMap);
            }
        }

        // calc single loop situation
        Set<String> singleLoopActivitySet = new HashSet<>();
        for (String activity : allActivitySet) {
            calcSingleLoopDependencyValue(activity, activityPairDependencyValueMap, tupleAndFrequencyMap, singleLoopActivitySet, dependencyThreshold);
        }

        // calc double loop situation
        for (Map.Entry<String, Integer> stringIntegerEntry : traceAndFrequencyMap.entrySet()) {
            Integer traceFrequency = stringIntegerEntry.getValue();
            String trace = stringIntegerEntry.getKey();

//             保证满足双循环的情况下不是单循环！
//            allActivitySet.stream().collect()
            List<String> allActivityList = new ArrayList<>(allActivitySet);
            for (int i = 0; i < allActivityList.size(); i ++ ) {
                for (int j = 0; j < allActivityList.size(); j ++ ) {
                    if (i != j && !singleLoopActivitySet.contains(allActivityList.get(i)) && !singleLoopActivitySet.contains(allActivityList.get(j))) {
                        // todo
                        String activityActivity = allActivityList.get(i) + allActivityList.get(j) + "2";
                        calcDoubleLoopDependencyValue(activityActivity, trace, activityPairDependencyValueMap, tupleAndFrequencyMap);
                    }
                }
            }
        }

        // filter less than dependency threshold
        filterLessThanDependencyThreshold(activityPairDependencyValueMap, dependencyThreshold);

        // filter tupleAndFrequencyMap by positiveObservationThreshold
        filterLessThanPositiveObservationThreshold(activityPairDependencyValueMap, tupleAndFrequencyMap, positiveObservationThreshold);

        // filter by relativeToBestThreshold
        filterByRelativeToBestThreshold(activityPairDependencyValueMap, relativeToBestThreshold);


        return activityPairDependencyValueMap;
    }


    public static void calcSingleLoopDependencyValue(String activity,
                                                    HashMap<String, Double> activityPairDependencyValueMap,
                                                    HashMap<String, Integer> tupleAndFrequencyMap,
                                                     Set<String> singleLoopActivitySet,
                                                     Double dependencyThreshold) {

        Integer activityActivity = 0;
        if (tupleAndFrequencyMap.containsKey(activity + activity)) {
            activityActivity = tupleAndFrequencyMap.get(activity + activity);
        }
        // calc
        Double calcActivityActivity = (activityActivity + 0.0) / (activityActivity + 1.0);
        activityPairDependencyValueMap.put(activity + activity, calcActivityActivity);

        if (calcActivityActivity > dependencyThreshold) {
            singleLoopActivitySet.add(activity);
        }
    }

    // calc the double loop num on every trace
    // activity: AB2, CD2
    public static void calcDoubleLoopDependencyValue(String activityActivity, String trace,
                                                     HashMap<String, Double> activityPairDependencyValueMap,
                                                     HashMap<String, Integer> tupleAndFrequencyMap) {
        // pre data
        Integer doubleLoopOccurNum = doubleLoopOccurNumOnTrace(activityActivity.replace("2", ""), trace); // |a>>b|
        StringBuilder stringBuilder = new StringBuilder(activityActivity.replace("2", ""));
        Integer doubleLoopOccurTraverseNum = doubleLoopOccurNumOnTrace(stringBuilder.reverse().toString(), trace); // |b>>a|

        // calc
        Double doubleLoopDependency = (doubleLoopOccurNum + doubleLoopOccurTraverseNum + 0.0) / (doubleLoopOccurNum + doubleLoopOccurTraverseNum + 1.0);
        activityPairDependencyValueMap.put(activityActivity, doubleLoopDependency);
    }

    public static Integer doubleLoopOccurNumOnTrace(String activityActivity, String trace) {
        return 0;
    }

    /**
     * filter the dependency relations by
     */
    public static void filterByRelativeToBestThreshold(HashMap<String, Double> activityPairDependencyValueMap, Double relativeToBestThreshold) {

        /*
          1. find the best Dependency Value
          2. calc the diff between the best Dependency Value and Dependency Value
          3. filter which diff value less than Threshold
         */
        // choose the best Dependency Value
        Double bestDependencyValue = 0D;
        for (Map.Entry<String, Double> stringDoubleEntry : activityPairDependencyValueMap.entrySet()) {
            Double value = stringDoubleEntry.getValue();
            if (value > bestDependencyValue) {
                bestDependencyValue = value;
            }
        }

        // calc the diff value and record remove item
        Set<String> shouldRemoveTuple = new HashSet<>();
        for (Map.Entry<String, Double> stringDoubleEntry : activityPairDependencyValueMap.entrySet()) {
            String key = stringDoubleEntry.getKey();
            double diff = bestDependencyValue - stringDoubleEntry.getValue();
            if (diff > relativeToBestThreshold) {
                shouldRemoveTuple.add(key);
            }
        }

        // remove(filter)
        for (String tuple : shouldRemoveTuple) {
            activityPairDependencyValueMap.remove(tuple);
        }
    }

    /**
     * remove AB: Dependency Which Frequency Less Than Threshold Was Given
     */
    public static void filterLessThanPositiveObservationThreshold(HashMap<String, Double> activityPairDependencyValueMap, HashMap<String, Integer> tupleAndFrequencyMap, Integer positiveObservationThreshold) {
        Set<String> removeTuple = new HashSet<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : tupleAndFrequencyMap.entrySet()) {
            String key = stringIntegerEntry.getKey();
            Integer value = stringIntegerEntry.getValue();
            if (value < positiveObservationThreshold) {
                removeTuple.add(key);
            }
        }

        for (String tuple : removeTuple) {
            activityPairDependencyValueMap.remove(tuple);
        }
    }

    public static void filterLessThanDependencyThreshold(HashMap<String, Double> activityPairDependencyValueMap,
                                                         Double dependencyThreshold) {
        Set<String> removeActivitySet = new HashSet<>();
        for (Map.Entry<String, Double> stringDoubleEntry : activityPairDependencyValueMap.entrySet()) {
            String key = stringDoubleEntry.getKey();
            Double value = stringDoubleEntry.getValue();

            if (value < dependencyThreshold) {
                removeActivitySet.add(key);
            }
        }

        for (String activity : removeActivitySet) {
            activityPairDependencyValueMap.remove(activity);
        }
    }

    /**
     * to print the object like HashMap or List
     * just Using Test Program
     * @param iterable
     */
    public static void print(Object iterable) {
        System.out.println("********************************************************");
        if (iterable instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) iterable).entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } else if (iterable instanceof List) {
            ((List<?>) iterable).forEach(System.out::println);
        }
        System.out.println("********************************************************");
    }

    public static void calcDependencyRelationValue(String activityI,
                                                   String activityJ,
                                                   HashMap<String, Double> activityPairDependencyValueMap,
                                                   HashMap<String, Integer> tupleAndFrequencyMap) {
        // formula
        Integer ABFreq = 0;
        Integer BAFreq = 0;
        if (tupleAndFrequencyMap.containsKey(activityI + activityJ)) {
            ABFreq = tupleAndFrequencyMap.get(activityI + activityJ);
        }

        if (tupleAndFrequencyMap.containsKey(activityJ + activityI)) {
            BAFreq = tupleAndFrequencyMap.get(activityJ + activityI);
        }

        Double aArrowB = (ABFreq - BAFreq + 0.0d) / (ABFreq + BAFreq + 1.0d);

        activityPairDependencyValueMap.put(activityI + activityJ, aArrowB);
    }
}
