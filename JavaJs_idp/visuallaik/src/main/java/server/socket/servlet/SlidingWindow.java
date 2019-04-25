package server.socket.servlet;

/*
Algorithm of sliding window to increase or decrease a value depending on success or fail event. 
Next value increases or decreases proportionally with multiply index.
Value is bounded within range
 */
public class SlidingWindow {
    private long currentValue;
    private long minimumValue;
    private long maximumValue;
    private double multiplyIndex;


    public SlidingWindow(long startValue, long minimumValue, long maximumValue, double multiplyIndex) {
        this.currentValue = startValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.multiplyIndex = multiplyIndex;
    }

    public long increase()
    {
        this.currentValue = Math.min(maximumValue, (long)(this.currentValue*multiplyIndex));
        return this.currentValue;
    }

    public long descrease()
    {
        this.currentValue = Math.max(minimumValue, (long)(this.currentValue*multiplyIndex));
        return this.currentValue;
    }
}
