package server.servlet.models;

import java.util.ArrayList;
import java.util.List;

public class ClusterData {
    private Long currentTime;
    private ClusterDescription clusterDescription;
    private List<Node> nodes = new ArrayList<Node>();

    public ClusterDescription getClusterDescription() {
        return clusterDescription;
    }

    public void setClusterDescription(ClusterDescription clusterDescription) {
        this.clusterDescription = clusterDescription;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }
}


