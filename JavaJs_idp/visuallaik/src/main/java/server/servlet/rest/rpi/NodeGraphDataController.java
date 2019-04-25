package server.servlet.rest.rpi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import server.hibernate.SessionFactoryInstance;
import server.servlet.models.NodeData;
import server.servlet.rest.rpi.readers.NodeGraphDataReader;
import server.servlet.rest.rpi.readers.parser.ParseResult;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static server.servlet.rest.rpi.readers.parser.ParseResult.parseId;

public class
NodeGraphDataController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        Session session = SessionFactoryInstance.getSession(request);
        ParseResult idParseResult = parseId(request, response);
        if (idParseResult.hasError)
            return;
        NodeData node = (new NodeGraphDataReader()).getContext(session, idParseResult.id);
        ObjectMapper mapper = new ObjectMapper();

        String contextStr = mapper.writeValueAsString(node);
        session.close();
        response.getWriter().print(contextStr);
    }
}