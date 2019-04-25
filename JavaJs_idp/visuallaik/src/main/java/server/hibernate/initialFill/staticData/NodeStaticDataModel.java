package server.hibernate.initialFill.staticData;

import java.util.List;

public class NodeStaticDataModel {
    private ClusterDescription clusterDescription;
    private List<NodeModel> nodes;

    public ClusterDescription getClusterDescription() {
        return clusterDescription;
    }

    public void setClusterDescription(ClusterDescription clusterDescription) {
        this.clusterDescription = clusterDescription;
    }

    public List<NodeModel> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeModel> nodes) {
        this.nodes = nodes;
    }
}
