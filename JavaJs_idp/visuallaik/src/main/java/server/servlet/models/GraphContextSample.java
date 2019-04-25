package server.servlet.models;

import java.util.HashSet;
import java.util.Set;

public class GraphContextSample {
    private long id;
    private String name;

    private Set<GraphContextSampleValue> values = new HashSet<GraphContextSampleValue>();

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

    public Set<GraphContextSampleValue> getValues() {
        return values;
    }

    public void setValues(Set<GraphContextSampleValue> values) {
        this.values = values;
    }
}
