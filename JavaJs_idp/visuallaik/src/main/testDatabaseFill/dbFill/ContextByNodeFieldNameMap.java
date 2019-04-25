package dbFill;

import server.hibernate.models.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// purporsed for ordering data by nodeId and custom data header name
public class ContextByNodeFieldNameMap {
    private Map<Long, Map<String, Context>> map = new HashMap<Long, Map<String, Context>>();

    public Map<String, Context> getNodeContext(Long nodeId)
    {
        return map.get(nodeId);
    }

    public void putNodeContext(Long nodeId, String headerName, Context context)
    {
        if (!map.containsKey(nodeId))
            map.put(nodeId, new HashMap<String, Context>());
        map.get(nodeId).put(headerName, context);
    }

    public Set<Long> getNodeIds()
    {
        return map.keySet();
    }
}
