package server.dbDump;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import server.hibernate.HibernateUtil;
import server.hibernate.models.Context;
import server.hibernate.models.Graph;
import server.hibernate.models.GraphSample;
import server.hibernate.models.Node;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DbDataCollector {

    public HashMap<Long, DbGraphSampleTranscript> getHeaders(StatelessSession session) {
        HashMap<Long, DbGraphSampleTranscript> dbGraphSampleTranscriptHashMap = new HashMap<>();
        Query<GraphSample> graphSamplesQuery = session.createQuery("from GraphSample", GraphSample.class);
        List<GraphSample> graphSamples = graphSamplesQuery.stream().collect(Collectors.toList());
        for (GraphSample graphSample: graphSamples) {
            Context context = graphSample.getContext();
            if (context == null)
                continue;
            Graph graph = context.getGraph();
            if (graph == null)
                continue;
            Node node = context.getNode();
            if (node == null)
                continue;
            DbGraphSampleTranscript transcript = new DbGraphSampleTranscript();
            transcript.graphId = graph.getId();
            transcript.graphName = graph.getName();
            transcript.graphSampleId = graphSample.getId();
            transcript.graphSampleName = graphSample.getName();
            transcript.nodeId = node.getId();
            transcript.nodeName = node.getName();
            dbGraphSampleTranscriptHashMap.put(graphSample.getId(), transcript);
        }
        return dbGraphSampleTranscriptHashMap;
    }
}
