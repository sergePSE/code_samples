package server.servlet.models;

public class Node {
    private long id;
    private String name;
    private String ip;
    private boolean isOnline;
    private int row;
    private int column;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getRow() { return row; }

    public void setRow(int row) { this.row = row; }

    public int getColumn() { return column; }

    public void setColumn(int column) { this.column = column; }
}
