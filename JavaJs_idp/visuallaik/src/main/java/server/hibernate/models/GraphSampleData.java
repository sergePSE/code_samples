package server.hibernate.models;

import javax.persistence.*;

@Entity
@Table(name="GraphSampleData",
        uniqueConstraints={@UniqueConstraint(columnNames={"id"})},
        indexes = {@Index(columnList = "xValue", name = "graph_sample_time_index")})
public class GraphSampleData {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private long id;
    @Column(name="xValue", nullable=false, unique=false)
    private long xValue;
    @Column(name="yValue", nullable=false, unique=false)
    private double yValue;

    @ManyToOne(fetch = FetchType.LAZY)
    public GraphSample graphSample;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public GraphSample getGraphSample() {
        return graphSample;
    }

    public void setGraphSample(GraphSample graphSample) {
        this.graphSample = graphSample;
    }
}
