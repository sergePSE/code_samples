package server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ServerSettingsReader {
    public ServerSettings readSettings()
    {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(classLoader.getResource("serverSettings.json"), ServerSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("no serverSettings.json found\n");
            System.exit(-1);
        }
        return null;
    }
}
