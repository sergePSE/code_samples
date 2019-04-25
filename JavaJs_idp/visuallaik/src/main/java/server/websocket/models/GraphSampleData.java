package server.websocket.models;

public class GraphSampleData {
    public GraphSampleData() {
    }

    public GraphSampleData(long xValue, double yValue, long graphSampleId) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.graphSampleId = graphSampleId;
    }

    private long xValue;
    private double yValue;
    public long graphSampleId;

    public long getxValue() {
        return xValue;
    }

    public void setxValue(long xValue) {
        this.xValue = xValue;
    }

    public double getyValue() {
        return yValue;
    }

    public void setyValue(double yValue) {
        this.yValue = yValue;
    }

    public long getGraphSampleId() {
        return graphSampleId;
    }

    public void setGraphSampleId(long graphSampleId) {
        this.graphSampleId = graphSampleId;
    }
}
