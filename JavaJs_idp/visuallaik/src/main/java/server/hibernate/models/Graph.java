package server.hibernate.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Graph",
        uniqueConstraints={@UniqueConstraint(columnNames={"id"})},
        indexes = {@Index(columnList = "name")})
public class Graph {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private long id;
    @Column(name="name", length=100)
    private String name;
    @Column(name="yAxeName", length=100)
    private String yAxeName;
    @Column(name="yAxeUnitName", length=100)
    private String yAxeUnitName;

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

    public String getyAxeName() {
        return yAxeName;
    }

    public void setyAxeName(String yAxeName) {
        this.yAxeName = yAxeName;
    }

    public String getyAxeUnitName() {
        return yAxeUnitName;
    }

    public void setyAxeUnitName(String yAxeUnitName) {
        this.yAxeUnitName = yAxeUnitName;
    }
}
