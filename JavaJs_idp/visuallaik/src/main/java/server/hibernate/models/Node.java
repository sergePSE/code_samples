package server.hibernate.models;

import javax.persistence.*;

@Entity
@Table(name="Node",
        uniqueConstraints = {
            @UniqueConstraint(columnNames={"id"}),
            @UniqueConstraint(columnNames={"columnNumber", "rowNumber"})
        },
        indexes = {@Index(columnList = "name")}
)
public class Node {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private long id;
    @Column(name="name", nullable=false, unique = true)
    private String name;
    @Column(name="ip")
    private String ip;
    @Column(name="isOnline", nullable=false)
    private int isOnline;
    @Column(name="columnNumber", nullable=false)
    private int columnNumber;
    @Column(name="rowNumber", nullable=false)
    private int rowNumber;

    @ManyToOne
    private ClusterDescription clusterDescription;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public ClusterDescription getClusterDescription() {
        return clusterDescription;
    }

    public void setClusterDescription(ClusterDescription clusterDescription) {
        this.clusterDescription = clusterDescription;
    }
    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public int getRowNumber() { return rowNumber; }

    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
}
