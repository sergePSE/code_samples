package server.servlet.models;

public class ClusterDescription {
    private String clusterName;
    private int numberOfNodes;
    private String connectivity;
    private boolean isHomogeneous;
    private String FQDN;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public String getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }

    public boolean isHomogeneous() {
        return isHomogeneous;
    }

    public void setHomogeneous(boolean homogeneous) {
        isHomogeneous = homogeneous;
    }

    public String getFQDN() {
        return FQDN;
    }

    public void setFQDN(String FQDN) {
        this.FQDN = FQDN;
    }
}
