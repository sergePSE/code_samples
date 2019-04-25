package dbFill;

import org.hibernate.Session;
import org.hibernate.Transaction;
import server.TimeParameters;
import server.hibernate.models.*;
import dbFill.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class ContextFill {

    private final static double CONTEXT_CHANCE = 0.7;
    private final static int MAX_CUSTOM_VALUE_LEN = 20;
    public Context fillCustomHeaderContext(Session session, CustomDataHeader dataHeader, Node node)
    {
        if (Math.random() > CONTEXT_CHANCE)
            return null;
        Transaction transaction = session.beginTransaction();
        Context context = new Context();
        context.setPlace(getRandomPlace());
        context.setNode(node);
        context.setCustomDataHeader(dataHeader);
        session.save(context);
        transaction.commit();

        (new CustomDataValueFill()).fillCustomContextData(session, context, System.currentTimeMillis(),
                StringUtils.generateRandomString((int)(MAX_CUSTOM_VALUE_LEN * Math.random())));
        return context;
    }


    public class PLACE_NAMES
    {
        public static final String OVERVIEW_PLACE = "overview";
        public static final String PERFORMANCE_PLACE = "performance";
        public static final String TASK_PLACE = "task";
    }

    public String getRandomPlace()
    {
        String[] places = {PLACE_NAMES.TASK_PLACE, PLACE_NAMES.OVERVIEW_PLACE, PLACE_NAMES.PERFORMANCE_PLACE};
        return places[(int)(Math.random()*places.length)];
    }

    public Context fillGraphContext(Session session, Graph graph, Node node, TimeParameters timeParameters)
    {
        if (Math.random() > CONTEXT_CHANCE)
            return null;
        Transaction transaction = session.beginTransaction();
        Context context = new Context();
        context.setPlace(Math.random() > 0.5? PLACE_NAMES.OVERVIEW_PLACE : PLACE_NAMES.PERFORMANCE_PLACE);
        context.setGraph(graph);
        context.setNode(node);
        session.save(context);
        transaction.commit();

        fillGraphSamples(session, context, timeParameters);
        return context;
    }

    private final int MAX_GRAPH_SAMPLE_NUMBER = 4;
    private final int MAX_GRAPH_SAMPLE_NAME_LEN = 15;
    private Set<GraphSample> fillGraphSamples(Session session, Context graphContext,
                                              TimeParameters timeParameters)
    {
        GraphContextData graphContextData = new GraphContextData();
        Set<GraphSample> graphSamples = new HashSet<GraphSample>();
        int graph_sample_number = (int)(Math.random() * MAX_GRAPH_SAMPLE_NUMBER);
        for (int i = 0; i < graph_sample_number; i++) {
            Transaction transaction = session.beginTransaction();

            GraphSample graphSample = new GraphSample();
            graphSample.setName(StringUtils.generateRandomString(MAX_GRAPH_SAMPLE_NAME_LEN));
            graphSample.setContext(graphContext);

            session.save(graphSample);
            transaction.commit();
            graphContextData.fillContextData(session, graphSample, timeParameters);
            graphSamples.add(graphSample);
        }
        return graphSamples;
    }
}
