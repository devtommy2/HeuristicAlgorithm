package org.sdutPmLab.utils;

import lombok.Data;

import java.util.List;

/**
 * @author tommy
 * @version 1.0
 * @date 2023/12/3 8:41 PM
 */

@Data
public class Trace {

    private String caseId;

    private List<Event> eventList;

    @Override
    public int hashCode() {

        StringBuilder activityString = new StringBuilder();
        for (Event event : eventList) {
            activityString.append(event.getActivity());
        }

        return activityString.toString().hashCode();
    }
}
