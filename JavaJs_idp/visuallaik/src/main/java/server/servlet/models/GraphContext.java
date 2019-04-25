package server.servlet.models;

import java.util.HashSet;
import java.util.Set;

public class GraphContext implements IPlacedContext {
    private long id;
    private String name;
    private String yaxisName;
    private String yaxisUnitName;
    private String placement;

    private Set<GraphContextSample> sampleSet = new HashSet<GraphContextSample>();

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

    public String getYaxisName() {
        return yaxisName;
    }

    public void setYaxisName(String yaxisName) {
        this.yaxisName = yaxisName;
    }

    public String getYaxisUnitName() {
        return yaxisUnitName;
    }

    public void setYaxisUnitName(String yaxisUnitName) {
        this.yaxisUnitName = yaxisUnitName;
    }

    public Set<GraphContextSample> getSampleSet() {
        return sampleSet;
    }

    public void setSampleSet(Set<GraphContextSample> sampleSet) {
        this.sampleSet = sampleSet;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }
}
