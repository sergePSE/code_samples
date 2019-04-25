package server.socket.servlet.parse.netdev;

public class DirectionalInterfaceStatistics{
    public DirectionalInterfaceStatistics(long bytesCount, long packetsCount) {
        this.bytesCount = bytesCount;
        this.packetsCount = packetsCount;
    }

    public DirectionalInterfaceStatistics(String bytesCount, String packetsCount) {
        this.bytesCount = Long.parseLong(bytesCount);
        this.packetsCount = Long.parseLong(packetsCount);
    }

    public long bytesCount;
    public long packetsCount;
}
