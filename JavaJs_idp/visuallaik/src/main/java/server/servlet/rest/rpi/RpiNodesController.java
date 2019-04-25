package server.servlet.rest.rpi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import server.hibernate.SessionFactoryInstance;
import server.servlet.models.ClusterData;
import server.servlet.rest.rpi.readers.ClusterReader;
import server.servlet.rest.rpi.readers.parser.ParseResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static server.servlet.rest.rpi.readers.parser.ParseResult.parseId;

public class RpiNodesController extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        Session session = SessionFactoryInstance.getSession(request);
        ClusterData clusterData = (new ClusterReader()).readClusterDescription(session);
        session.close();
        response.getWriter().print(new ObjectMapper().writeValueAsString(clusterData));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        ParseResult idParseResult = parseId(request, response);
        String commandString = request.getParameter("command");
        if (idParseResult.hasError)
            return;
        System.out.println(String.format("node command received: %d %s", idParseResult.id, commandString));
    }
}
