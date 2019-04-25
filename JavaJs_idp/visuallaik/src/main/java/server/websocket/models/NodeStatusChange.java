package server.websocket.models;

public class NodeStatusChange {
    public NodeStatusChange() {
    }

    public NodeStatusChange(long nodeId, boolean nodeStatus) {
        this.nodeId = nodeId;
        this.nodeStatus = nodeStatus;
    }

    private long nodeId;
    private boolean nodeStatus;

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public boolean isNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(boolean nodeStatus) {
        this.nodeStatus = nodeStatus;
    }
}
