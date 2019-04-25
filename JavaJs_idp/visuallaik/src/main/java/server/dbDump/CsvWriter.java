package server.dbDump;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.stream.Collectors;

public class CsvWriter {
    private File file;
    private String generateFileName(boolean isSizeLimit, Calendar minDate, Calendar maxDate) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd_MM_yyyy");
        return String.format("%s_%s%s", dateFormatter.format(minDate.getTime()),
                dateFormatter.format(maxDate.getTime()), isSizeLimit? "_size" : "");

    }

    private File getUniqueFileNameWithSuffix(String path, String suggestedName){
        int suffix = 1;
        File file = Paths.get(path, suggestedName + ".csv").toFile();
        while(file.exists()) {
            file = Paths.get(path, suggestedName + suffix + ".csv").toFile();
            suffix++;
        }
        return file;
    }

    public CsvWriter(String path, boolean isSizeLimit, Calendar minDate, Calendar maxDate) throws IOException {

        String filename = generateFileName(isSizeLimit, minDate, maxDate);
        Path folderPath = Paths.get(path).toAbsolutePath();
        if (!Files.exists(folderPath))
            if (!folderPath.toFile().mkdir()) {
                throw new AccessDeniedException(String.format("%s forbidden to create a folder", folderPath.toString()));
            }
        this.file = getUniqueFileNameWithSuffix(folderPath.toString(), filename);
    }

    private void writeLineToFile(Collection<String> lineData, boolean isEscapeRequired, FileWriter writer)
            throws IOException {
        if (isEscapeRequired) {
            lineData = lineData.stream().map(strValue -> '"' + strValue + '"').collect(Collectors.toList());
        }
        String line = String.join(",", lineData);
        writer.write(line + "\n");
    }

    public void writeLineToFile(Collection<String> lineData, boolean isEscapeRequired) throws IOException {
        FileWriter writer = new FileWriter(file, true);
        writeLineToFile(lineData, isEscapeRequired, writer);
        writer.close();
    }

    public void writeLinesToFile(Collection<Collection<String>> linesData, boolean isEscapeRequired) throws IOException {
        FileWriter writer = new FileWriter(file, true);
        for (Collection<String> lineData: linesData) {
            writeLineToFile(lineData, isEscapeRequired, writer);
        }
        writer.close();
    }

    public void writeDoublesToFile(Collection<Double> lineData) throws IOException {
        FileWriter writer = new FileWriter(file, true);
        String line = String.join(",",
                (String[])lineData.stream().map(doubleValue -> doubleValue.toString()).toArray());
        writer.write(line + "\n");
    }

    public void destroy(){

    }
}
