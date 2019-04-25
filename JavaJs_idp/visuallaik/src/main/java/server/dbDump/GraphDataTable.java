package server.dbDump;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GraphDataTable {

    private Map<Long, Map<Long, Double[]>> timeNodeSubgraphValues = new ConcurrentHashMap<>();

    private Map<Long, String> nodeIdNamesMap = new HashMap<>();
    private List<String> subgraphNames = new ArrayList<>();

    private Map<Long, Integer> graphSampleIdToSubgraphIndex = new HashMap<>();
    private Map<Long, Long> graphSampleIdToNodeId = new HashMap<>();


    private boolean isEmptyOrNull(String text)
    {
        if (text == null)
            return true;
        if (text.equals(""))
            return true;
        return false;
    }


    private String createSubgraphName(String graphName, String graphSampleName)
    {
        if (!isEmptyOrNull(graphName) || !isEmptyOrNull(graphSampleName))
            return String.format("%s(%s)", graphName, graphSampleName);
        if (isEmptyOrNull(graphName))
            return graphSampleName;
        return graphName;
    }


    private void extractPredata(Map<Long, DbGraphSampleTranscript> sampleTranscriptMap)
    {
        for (Long graphSampleId : sampleTranscriptMap.keySet()) {
            DbGraphSampleTranscript transcript = sampleTranscriptMap.get(graphSampleId);
            if (!nodeIdNamesMap.containsKey(transcript.nodeId))
                nodeIdNamesMap.put(transcript.nodeId, transcript.nodeName);
            graphSampleIdToNodeId.put(graphSampleId, transcript.nodeId);

            String subgraphName = createSubgraphName(transcript.graphName, transcript.graphSampleName);
            if (!subgraphNames.contains(subgraphName)) {
                subgraphNames.add(subgraphName);
            }

            int subgraphNameIndex = subgraphNames.indexOf(subgraphName);
            graphSampleIdToSubgraphIndex.put(graphSampleId, subgraphNameIndex);
        }
    }

    public GraphDataTable(Map<Long, DbGraphSampleTranscript> sampleTranscriptMap) {
        extractPredata(sampleTranscriptMap);
    }

    public void addData(Long graphSampleId, Long time, double value){
        if (!timeNodeSubgraphValues.containsKey(time))
            timeNodeSubgraphValues.put(time, new HashMap<>());
        Long nodeId = graphSampleIdToNodeId.get(graphSampleId);
        if (nodeId == null)
            return;
        Map<Long, Double[]> timeValues = timeNodeSubgraphValues.get(time);
        if (!timeValues.containsKey(nodeId))
            timeValues.put(nodeId, new Double[subgraphNames.size()]);
        Integer subgraphIndex = graphSampleIdToSubgraphIndex.get(graphSampleId);
        if (subgraphIndex == null)
            return;
        timeValues.get(nodeId)[subgraphIndex] = value;
    }

    public Collection<Collection<String>> emptyTable() {
        List<Collection<String>> lines = new ArrayList<>();

        for (Long time : timeNodeSubgraphValues.keySet()) {
            for (Long nodeId : timeNodeSubgraphValues.get(time).keySet()) {
                Double[] timeNodeValues = timeNodeSubgraphValues.get(time).get(nodeId);
                List<String> columnValues = new ArrayList<>();
                columnValues.add(time.toString());
                columnValues.add(nodeIdNamesMap.get(nodeId));
                for (Double value: timeNodeValues) {
                    if (value != null)
                        columnValues.add(String.format(Locale.US, "%.3f", value));
                    else columnValues.add("");
                }
                lines.add(columnValues);
            }
            timeNodeSubgraphValues.get(time).clear();
        }
        timeNodeSubgraphValues.clear();
        return lines;
    }

    public Collection<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        headers.add("Time(s)");
        headers.add("Node");
        headers.addAll(subgraphNames);
        return headers;
    }
}
