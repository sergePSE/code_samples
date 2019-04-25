package server.websocket.models;

import java.util.List;

public class ContextData {
    private long contextId;
    private List<GraphSampleData> graphSampleData;

    public long getContextId() {
        return contextId;
    }

    public void setContextId(long contextId) {
        this.contextId = contextId;
    }

    public List<GraphSampleData> getGraphSampleData() {
        return graphSampleData;
    }

    public void setGraphSampleData(List<GraphSampleData> graphSampleData) {
        this.graphSampleData = graphSampleData;
    }
}
