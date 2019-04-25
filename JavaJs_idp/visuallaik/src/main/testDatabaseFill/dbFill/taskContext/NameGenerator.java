package dbFill.taskContext;

public class NameGenerator implements ITaskValueGenerator {
    public String getStringValue() {
        return String.format("Job #%d", (int)(Math.random() * 10000));
    }
}
