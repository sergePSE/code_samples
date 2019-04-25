package server.hibernate.initialFill.staticData;

public class ClusterDescription {
    private String connectivity;
    private String fqdn;
    private int isHomogeneous;
    private String name;

    public String getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public int getIsHomogeneous() {
        return isHomogeneous;
    }

    public void setIsHomogeneous(int isHomogeneous) {
        this.isHomogeneous = isHomogeneous;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
