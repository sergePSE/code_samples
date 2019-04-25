package server.hibernate.models;

import javax.persistence.*;

@Entity
@Table(name="Task",
        uniqueConstraints={@UniqueConstraint(columnNames={"id"})},
        indexes = {@Index(columnList = "isEnded")})
public class Task {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private long id;
    @Column(name="name", nullable=false, unique=false)
    private String name;
    @Column(name="isExecutable", nullable=true, unique=false)
    private int isExecutable;
    @Column(name="args", nullable=false, unique=false)
    private String args;
    @Column(name="jobStartTime", nullable=false, unique=false)
    private long jobStartTime;
    @Column(name="ranks", nullable=true, unique=false)
    private String ranks;
    @Column(name="isEnded", nullable=false, unique=false)
    private int isEnded;
    @ManyToOne
    private Node node;

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

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public long getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(long jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public String getRanks() {
        return ranks;
    }

    public void setRanks(String ranks) {
        this.ranks = ranks;
    }

    public int getIsExecutable() {
        return isExecutable;
    }

    public void setIsExecutable(int isExecutable) {
        this.isExecutable = isExecutable;
    }

    public int getIsEnded() {
        return isEnded;
    }

    public void setIsEnded(int isEnded) {
        this.isEnded = isEnded;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
