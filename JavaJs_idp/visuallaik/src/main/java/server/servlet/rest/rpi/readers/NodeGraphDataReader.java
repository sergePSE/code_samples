package server.servlet.rest.rpi.readers;

import org.hibernate.Session;
import org.hibernate.query.Query;
import server.hibernate.models.Context;
import server.hibernate.models.CustomDataHeader;
import server.hibernate.models.Graph;
import server.hibernate.models.GraphSample;
import server.servlet.models.*;

import java.util.*;

public class NodeGraphDataReader {
    public NodeData getContext(Session session, long nodeId)
    {
        server.hibernate.models.Node node = session.load(server.hibernate.models.Node.class, nodeId);
        if (node == null)
            return null;
        NodeData nodeData = new NodeData();
        nodeData.setId(nodeId);
        Query<Context> contextQuery = session.createQuery("from Context where node.id = :nodeId", Context.class);
        contextQuery.setParameter("nodeId", node.getId());
        List<Context> contextList = contextQuery.list();
        fillTaskData(session, nodeData, nodeId);
        fillCustomDataContext(session, contextList, nodeData);
        fillGraphData(session, contextList, nodeData.getGraphContexts());
        return nodeData;
    }

    private void fillTaskData(Session session, NodeData outNodeData, long nodeId)
    {
        Query<server.hibernate.models.Task> taskQuery =
                session.createQuery("from Task where node.id = :nodeId and isEnded = 0");
        taskQuery.setParameter("nodeId", nodeId);
        List<server.hibernate.models.Task> hibernateActiveTasks = taskQuery.list();
        for (server.hibernate.models.Task hibernateActiveTask : hibernateActiveTasks) {
            outNodeData.getActiveTasks().add(hibernateToModelTask(hibernateActiveTask));
        }
    }

    private Task hibernateToModelTask(server.hibernate.models.Task hibernateTask)
    {
        Task task = new Task();
        task.setId(hibernateTask.getId());
        task.setArgs(hibernateTask.getArgs());
        task.setExecutable(hibernateTask.getIsExecutable() == 1);
        task.setJobStartTime(hibernateTask.getJobStartTime());
        task.setName(hibernateTask.getName());
        task.setRanks(hibernateTask.getRanks());
        task.setPlacement("task");
        return task;
    }

    private void fillCustomDataContext(Session session, List<Context> contextSet, NodeData outNodeData)
    {
        for (Context context : contextSet) {

            CustomDataHeader hibernateDataHeader = context.getCustomDataHeader();
            if (hibernateDataHeader == null)
                continue;

            CustomDataContext dataContext = new CustomDataContext();
            CustomDataValue customDataValue = loadLastCustomDataValue(session, context.getId());

            dataContext.setId(context.getId());
            dataContext.setName(hibernateDataHeader.getValue());
            dataContext.setDataValue(customDataValue);
            dataContext.setPlacement(context.getPlace());

            outNodeData.getCustomDataContexts().add(dataContext);
        }
    }

    private server.hibernate.models.CustomDataValue fromObject(Object[] requestResultObj)
    {
        server.hibernate.models.CustomDataValue customDataValue = new server.hibernate.models.CustomDataValue();
        customDataValue.setId((Long)requestResultObj[0]);
        customDataValue.setTime((Long)requestResultObj[1]);
        customDataValue.setValue((String)requestResultObj[2]);
        customDataValue.setContext((Context)requestResultObj[3]);
        return customDataValue;
    }

    private CustomDataValue loadLastCustomDataValue(Session session, long contextId)
    {
        CustomDataValue customDataValue = new CustomDataValue();
        Query customDataValueRequest =
            session.createQuery(
                    "select cdv.id, max(cdv.time), cdv.value, cdv.context " +
                            "from CustomDataValue as cdv where cdv.context.id = :contextId"
            );
        customDataValueRequest.setParameter("contextId", contextId);
        Object result = customDataValueRequest.getSingleResult();
        server.hibernate.models.CustomDataValue hibernateDataValue = fromObject((Object[])result);
        if (hibernateDataValue == null)
            return null;

        customDataValue.setTime(hibernateDataValue.getTime());
        customDataValue.setValue(hibernateDataValue.getValue());
        return customDataValue;
    }

    // hibernate has a lazy load, extra load data required
    public static void fillGraphData(Session session, Collection<Context> contextSet, Collection<GraphContext> outContextSet)
    {
        for (Context context : contextSet) {
            Graph hibernateGraph = context.getGraph();
            if (hibernateGraph == null)
                continue;
            GraphContext graphContext = new GraphContext();
            // set context id to find current instance
            graphContext.setId(context.getId());
            graphContext.setName(hibernateGraph.getName());
            graphContext.setYaxisName(hibernateGraph.getyAxeName());
            graphContext.setYaxisUnitName(hibernateGraph.getyAxeUnitName());
            graphContext.setPlacement(context.getPlace());

            fillGraphSampleSets(session, context, graphContext);

            outContextSet.add(graphContext);
        }
    }

    public static void fillGraphSampleSets(Session session, Context hibernateContext, GraphContext outGraphContext)
    {
        Query<GraphSample> graphSampleQuery =
                session.createQuery("from GraphSample where context.id = :contextId", GraphSample.class);
        graphSampleQuery.setParameter("contextId", hibernateContext.getId());
        for (GraphSample graphSample: graphSampleQuery.list()) {
            GraphContextSample graphContextSample = new GraphContextSample();
            graphContextSample.setId(graphSample.getId());
            graphContextSample.setName(graphSample.getName());
            outGraphContext.getSampleSet().add(graphContextSample);
        }
    }
}
