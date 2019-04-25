package server.servlet.rest.rpi.readers;

import org.hibernate.Session;
import org.hibernate.query.Query;
import server.hibernate.models.Context;
import server.hibernate.models.GraphSample;
import server.hibernate.models.GraphSampleData;
import server.servlet.models.GraphContext;
import server.servlet.models.GraphContextSample;
import server.servlet.models.GraphContextSampleValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChartDataReader {
    public static class FilterParameters{
        public FilterParameters(long contextId, long fromTime, long toTime, int nodesNumber)
        {
            this.contextId = contextId;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.nodesNumber = nodesNumber;
        }

        public long contextId;
        public long fromTime;
        public long toTime;
        public int nodesNumber;

        public long getTimeStep()
        {
            return (toTime - fromTime) / nodesNumber;
        }

    }
    public GraphContext getContext(Session session, FilterParameters filterParameters)
    {
        Context hibernateContext = session.load(Context.class, filterParameters.contextId);
        Set<GraphContext> graphContextSet = new HashSet<GraphContext>();
        if (hibernateContext == null)
            return null;
        Set<Context> context = new HashSet<Context>();
        context.add(hibernateContext);
        NodeGraphDataReader.fillGraphData(session, context, graphContextSet);

        if (graphContextSet.isEmpty())
            return null;
        GraphContext graphContext = graphContextSet.iterator().next();
        Set<GraphContextSample> graphContextSamples = graphContext.getSampleSet();
        for (GraphContextSample graphSample: graphContextSamples) {
            graphSample.setValues(loadDensedGraphSample(session, graphSample.getId(), filterParameters));
        }
        return graphContext;
    }

    private Set<GraphContextSampleValue> loadAvgGraphSample(Session session, long graphSampleId, FilterParameters filterParameters)
    {
        Query<GraphSampleData> sampleDataQuery = session.createQuery(
                "select FLOOR(xValue / :timeStep), yValue from GraphSampleData " +
                        "where graphSample.id = :graphSampleId and xValue between :fromTime and :toTime " +
                        "group by FLOOR(xValue / :timeStep)");

        sampleDataQuery.setParameter("graphSampleId", graphSampleId);
        sampleDataQuery.setParameter("timeStep", filterParameters.getTimeStep());
        sampleDataQuery.setParameter("fromTime", filterParameters.fromTime);
        sampleDataQuery.setParameter("toTime", filterParameters.toTime);
        return queryToSet(sampleDataQuery);
    }

    private Set<GraphContextSampleValue> loadDensedGraphSample(Session session, long graphSampleId, FilterParameters filterParameters)
    {
        if (filterParameters.nodesNumber == 0)
            return new HashSet<GraphContextSampleValue>();
        Query<GraphSampleData> sampleDataQuery = session.createQuery(
                "from GraphSampleData " +
                        "where graphSample.id = :graphSampleId and xValue between :fromTime and :toTime " +
                        "order by (mod(xValue, :timeStep)) ASC ");
        sampleDataQuery.setParameter("graphSampleId", graphSampleId);
        sampleDataQuery.setParameter("fromTime", filterParameters.fromTime);
        sampleDataQuery.setParameter("toTime", filterParameters.toTime);
        sampleDataQuery.setParameter("timeStep", filterParameters.getTimeStep());
        sampleDataQuery.setMaxResults(filterParameters.nodesNumber);
        return queryToSet(sampleDataQuery);

    }

    private Set<GraphContextSampleValue> queryToSet(Query<GraphSampleData> query)
    {
        Set<GraphContextSampleValue> graphContextSampleValues = new HashSet<GraphContextSampleValue>();
        List<GraphSampleData> sampleDataList = query.list();
        for (GraphSampleData sampleData: sampleDataList) {
            GraphContextSampleValue graphContextSampleValue = new GraphContextSampleValue();
            graphContextSampleValue.setTime(sampleData.getxValue());
            graphContextSampleValue.setValue(sampleData.getyValue());
            graphContextSampleValues.add(graphContextSampleValue);
        }
        return graphContextSampleValues;
    }


}
