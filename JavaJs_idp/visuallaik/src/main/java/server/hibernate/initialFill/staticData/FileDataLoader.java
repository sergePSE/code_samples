package server.hibernate.initialFill.staticData;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class FileDataLoader {
    public NodeStaticDataModel readData() throws IOException {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(classLoader.getResource("nodeStaticData.json"), NodeStaticDataModel.class);
    }

}
