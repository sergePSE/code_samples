package server.socket.servlet.parse.general;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralParser {
    public Pattern pattern;
    public Pattern patternTime;
    public Pattern tempPattern;

    public GeneralParser() {
        this.pattern = Pattern.compile("ENCODE_HOST:\\s+(\\S+), .*" +
                "CONTENT:\\s+b'(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\/(\\d+)\\s+(\\d+)\\n");
        this.patternTime = Pattern.compile("ENCODE_TIME:\\s+(\\d+)");
        this.tempPattern = Pattern.compile("(\\d+)\\n?'?$");
    }

    private static Logger logger = Logger.getLogger("GeneralParser");

    private String parseAndSubstringHeader(String data, GeneralInfo fillingInfo) throws IllegalArgumentException
    {
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find()) {
            logger.log(Level.INFO, "message not parsed:" + data);
            throw new IllegalArgumentException();
        }

        fillingInfo.host =  matcher.group(1);
        fillingInfo.avg1Load = Double.parseDouble(matcher.group(2));
        fillingInfo.avg5Load = Double.parseDouble(matcher.group(3));
        fillingInfo.avg15Load = Double.parseDouble(matcher.group(4));
        fillingInfo.currentProcesses = Integer.parseInt(matcher.group(5));
        fillingInfo.totalProcesses = Integer.parseInt(matcher.group(6));
        fillingInfo.lastProcessId = Integer.parseInt(matcher.group(7));

        Matcher matcherTime = patternTime.matcher(data);
        if (!matcherTime.matches())
            fillingInfo.encodeTimeS = System.currentTimeMillis() / 1000;
        else
            fillingInfo.encodeTimeS = Integer.parseInt(matcher.group(1));
        return data.substring(matcher.end(0));
    }

    private String parseTempAndSubstringHeader(String data, GeneralInfo fillingInfo) throws IllegalArgumentException
    {
        Matcher matcher = tempPattern.matcher(data);
        if (!matcher.find()) {
            logger.log(Level.INFO, "message not parsed for temperature:" + data);
            throw new IllegalArgumentException();
        }

        fillingInfo.temperatureuC = Integer.parseInt(matcher.group(1));
        return data.substring(0, matcher.start(0));
    }

    public GeneralParseResult parseGeneralInfo(String data) throws IllegalArgumentException
    {
        GeneralInfo generalInfo = new GeneralInfo();
        data = parseAndSubstringHeader(data, generalInfo);
        data = parseTempAndSubstringHeader(data, generalInfo);
        return new GeneralParseResult(data, generalInfo);
    }
}
