package dbFill.utils;

public class StringUtils {
    public static String generateRandomString(int length)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append((char)('a' +  Math.random()*('z' - 'a')));
        }
        return stringBuilder.toString();
    }
}
