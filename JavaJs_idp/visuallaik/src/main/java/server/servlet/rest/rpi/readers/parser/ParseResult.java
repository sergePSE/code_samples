package server.servlet.rest.rpi.readers.parser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ParseResult {

    public ParseResult(long id) {
        this.id = id;
        this.hasError = false;
    }

    public ParseResult(boolean hasError) {
        this.hasError = hasError;
    }

    public long id;
    public boolean hasError;

    public static ParseResult parseId(HttpServletRequest request, HttpServletResponse response)
    {
        String rpiNodeIdStr = request.getParameter("id");
        long rpiNodeId;
        try {
            rpiNodeId = Long.parseLong(rpiNodeIdStr);
        } catch (NumberFormatException e) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return new ParseResult(true);
        }
        return new ParseResult(rpiNodeId);
    }
}
