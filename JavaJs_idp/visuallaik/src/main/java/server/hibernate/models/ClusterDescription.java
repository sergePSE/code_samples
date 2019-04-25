package server.hibernate.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="ClusterDescription",
        uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class ClusterDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "connectivity", nullable = true, unique = false)
    private String connectivity;
    @Column(name = "isHomogeneous", nullable = true, unique = false)
    private int isHomogeneous;
    @Column(name = "fqdn", nullable = true, unique = false)
    private String fqdn;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }

    public int getIsHomogeneous() {
        return isHomogeneous;
    }

    public void setIsHomogeneous(int isHomogeneous) {
        this.isHomogeneous = isHomogeneous;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }
}
