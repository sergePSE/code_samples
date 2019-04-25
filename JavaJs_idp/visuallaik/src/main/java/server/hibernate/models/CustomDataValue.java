package server.hibernate.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="CustomDataValue",
        uniqueConstraints={@UniqueConstraint(columnNames={"id"})},
        indexes = {
                @Index(columnList = "time", name = "custom_data_value_time_index")
        })
public class CustomDataValue {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private long id;

    @Column(name="time", nullable=true, unique=false)
    private long time;

    @Column(name="value", nullable=true, unique=false)
    private String value;

    @ManyToOne
    private Context context;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
