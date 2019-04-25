package server;

public class ServerSettings {

    public int maxDbSizeMb;
    public int minDbSizeMb;
    public int maxDbLogDays;
    public int minDbLogDays;
    public String dumpPath;

    public int getMaxDbSizeMb() {
        return maxDbSizeMb;
    }

    public void setMaxDbSizeMb(int maxDbSizeMb) {
        this.maxDbSizeMb = maxDbSizeMb;
    }

    public int getMinDbSizeMb() {
        return minDbSizeMb;
    }

    public void setMinDbSizeMb(int minDbSizeMb) {
        this.minDbSizeMb = minDbSizeMb;
    }

    public int getMaxDbLogDays() {
        return maxDbLogDays;
    }

    public void setMaxDbLogDays(int maxDbLogDays) {
        this.maxDbLogDays = maxDbLogDays;
    }

    public int getMinDbLogDays() {
        return minDbLogDays;
    }

    public void setMinDbLogDays(int minDbLogDays) {
        this.minDbLogDays = minDbLogDays;
    }

    public String getDumpPath() {
        return dumpPath;
    }

    public void setDumpPath(String dumpPath) {
        this.dumpPath = dumpPath;
    }
}
