package server.servlet.models;

import java.util.HashSet;
import java.util.Set;

public class NodeData {
    private long id;
    private Set<GraphContext> graphContexts = new HashSet<GraphContext>();
    private Set<CustomDataContext> customDataContexts = new HashSet<CustomDataContext>();
    private Set<Task> activeTasks = new HashSet<Task>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<GraphContext> getGraphContexts() {
        return graphContexts;
    }

    public void setGraphContexts(Set<GraphContext> graphContexts) {
        this.graphContexts = graphContexts;
    }

    public Set<CustomDataContext> getCustomDataContexts() {
        return customDataContexts;
    }

    public void setCustomDataContexts(Set<CustomDataContext> customDataContexts) {
        this.customDataContexts = customDataContexts;
    }

    public Set<Task> getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(Set<Task> activeTasks) {
        this.activeTasks = activeTasks;
    }
}
