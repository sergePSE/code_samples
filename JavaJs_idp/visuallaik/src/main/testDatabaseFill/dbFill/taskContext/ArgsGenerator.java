package dbFill.taskContext;


import dbFill.utils.StringUtils;

public class ArgsGenerator implements ITaskValueGenerator {
    private final int MAX_ARGS_LEN = 50;

    public String getStringValue() {
        return StringUtils.generateRandomString(MAX_ARGS_LEN);
    }
}
