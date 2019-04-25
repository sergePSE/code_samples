package server.servlet.rest.rpi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import server.hibernate.SessionFactoryInstance;
import server.servlet.models.GraphContext;
import server.servlet.rest.rpi.readers.ChartDataReader;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ChartDataController  extends HttpServlet {
    private static int MAX_POINTS = 1000;
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Session session = SessionFactoryInstance.getSession(request);
        long contextId;
        long fromDate;
        long toDate;
        int nodesNumber;
        try {
            contextId = Long.parseLong(request.getParameter("id"));
            fromDate = Long.parseLong(request.getParameter("from"));
            toDate = Long.parseLong(request.getParameter("to"));
            nodesNumber = Integer.parseInt(request.getParameter("nodesNumber"));
            if (nodesNumber > MAX_POINTS)
                throw new NumberFormatException("number of requested nodes is exceeded");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        GraphContext context = (new ChartDataReader()).getContext(session,
                new ChartDataReader.FilterParameters(contextId, fromDate, toDate, nodesNumber));
        session.close();
        response.getWriter().print(new ObjectMapper().writeValueAsString(context));
    }
}