package server.hibernate.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Context", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Context {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private long id;
    @ManyToOne
    private Node node;
    @ManyToOne
    private Graph graph;
    @Column(name="place", nullable=false, unique=false)
    private String place;
    @ManyToOne
    private CustomDataHeader customDataHeader;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public CustomDataHeader getCustomDataHeader() {
        return customDataHeader;
    }

    public void setCustomDataHeader(CustomDataHeader customDataHeader) {
        this.customDataHeader = customDataHeader;
    }
}
