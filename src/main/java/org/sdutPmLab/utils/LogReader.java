package org.sdutPmLab.utils;

/**
 * @author tommy
 * @version 1.0
 * @date 2023/12/3 8:42 PM
 */


import au.com.bytecode.opencsv.CSVReader;
import com.sun.media.jfxmedia.logging.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 此时默认这里的事件日志是简单事件日志
 * 此时默认日志文件中trace中事件的出现顺序就是执行顺序，为了实现简单就不考虑时间戳的排序了。
 */
public class LogReader {

//    List<Trace> eventLog = new ArrayList<>();

    public static List<Event> getEventList(String csvFilePath) {

        List<Event> eventList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] line;
            line = reader.readNext();
            while ((line = reader.readNext()) != null) {
                // 处理每一行的数据
                Event event = new Event();
                event.setCaseId(line[0]);
                event.setEventId(UUID.randomUUID().toString());
                event.setActivity(line[1]);
                eventList.add(event);
            }
            return eventList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, Integer> getTraceAndFrequencyMap(String csvFilePath) {
//        List<Event> eventList = CsvReader.getEventList("src/main/java/org/example/data/log.csv");
        List<Event> eventList = LogReader.getEventList(csvFilePath);

        assert eventList != null;
        // 拿到所有的caseId
        List<String> caseIdList = new ArrayList<>();
        for (Event event : eventList) {
            if (!caseIdList.contains(event.getCaseId())) {
                caseIdList.add(event.getCaseId());
            }
        }

        HashMap<String, Integer> traceIntegerMap = new HashMap<>();
        for (String caseId : caseIdList) {
            StringBuilder traceString = new StringBuilder();
            for (Event event : eventList) {
                if (event.getCaseId().equals(caseId)) {
                    traceString.append(event.getActivity());
                }
            }

            if (!traceIntegerMap.containsKey(traceString.toString())) {
                traceIntegerMap.put(traceString.toString(), 1);
            } else {
                traceIntegerMap.put(traceString.toString(), traceIntegerMap.get(traceString.toString()) + 1);
            }
        }


        return traceIntegerMap;
    }

    public static void main(String[] args) {
        HashMap<String, Integer> traceList = LogReader.getTraceAndFrequencyMap("src/main/java/org/sdutPmLab/data/running-example2.csv");

        for (Map.Entry<String, Integer> traceIntegerEntry : traceList.entrySet()) {
            System.out.println("key: " + traceIntegerEntry.getKey() + " value: " + traceIntegerEntry.getValue());
        }
    }
}
