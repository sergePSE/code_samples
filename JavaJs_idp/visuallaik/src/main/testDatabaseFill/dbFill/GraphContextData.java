package dbFill;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.NativeQuery;
import org.joda.time.Duration;
import server.TimeParameters;
import server.hibernate.HibernateUtil;
import server.hibernate.models.*;

import java.sql.SQLException;
import java.util.*;

public class GraphContextData {

    public void fillContextData(Session session, GraphSample graphSample, TimeParameters timeParameters)
    {
        fillGraphContextData(session, graphSample, timeParameters);
    }

    private final String FILL_GRAPH_DATA_PROCEDURE_CREATE =
        "CREATE PROCEDURE fillGraphSample (fromTime LONG, toTime LONG, stepTime LONG, graphSampleId LONG)\n" +
        "BEGIN\n" +
        "\tWHILE fromTime <= toTime DO\n" +
        "\t\tinsert into GraphSampleData(xValue, yValue, graphSample_id) values (fromTime, RAND()*100, graphSampleId);\n" +
        "\t\tSET fromTime = fromTime + stepTime; \n" +
        "\tEND WHILE;\n" +
        "END; ";
    private final String FILL_GRAPH_DATA_PROCEDURE_DROP = "drop procedure fillGraphSample";
    private void fillGraphContextData(Session session, GraphSample graphSample, TimeParameters timeParameters){
        Transaction transaction = session.beginTransaction();
        try {
            HibernateUtil.executeRawRequest(FILL_GRAPH_DATA_PROCEDURE_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
        fillGraphSampleData(session, graphSample, timeParameters);
        try {
            HibernateUtil.executeRawRequest(FILL_GRAPH_DATA_PROCEDURE_DROP);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
        transaction.commit();
    }

    private void fillGraphSampleData(Session session, GraphSample graphSample, TimeParameters timeParameters)
    {
        NativeQuery procedureCall =
                session.createNativeQuery("call fillGraphSample(:fromTime, :toTime, :stepTime, :graphSampleId)");
        procedureCall.setParameter("fromTime", timeParameters.startDate);
        procedureCall.setParameter("toTime", timeParameters.endDate);
        procedureCall.setParameter("stepTime", timeParameters.durationStep);
        procedureCall.setParameter("graphSampleId", graphSample.getId());
        procedureCall.setTimeout((int)Duration.standardMinutes(20).getStandardSeconds());
        procedureCall.executeUpdate();
    }
}
