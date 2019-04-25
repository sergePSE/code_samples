package server.socket.servlet.parse.general;

public class GeneralParseResult{
    public GeneralParseResult(String restString, GeneralInfo generalInfo) {
        this.restString = restString;
        this.generalInfo = generalInfo;
    }

    public String restString;
    public GeneralInfo generalInfo;
}