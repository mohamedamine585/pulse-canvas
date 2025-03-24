package com.pulse.canvas.Dtoes;

public class PixelUpdate {
   public  Long updateId;
   public int start, end, timestamp;

    public PixelUpdate(int start, int end, int timestamp) {
        this.start = start;
        this.end = end;
        this.timestamp = timestamp;
    }
    public PixelUpdate(Long updateId,int start, int end, int timestamp) {
        this.updateId = updateId;
        this.start = start;
        this.end = end;
        this.timestamp = timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Long getUpdateId() {
        return updateId;
    }

    @Override
    public String toString() {
        return "[" + start + " → " + end + " @ " + timestamp + "]";
    }
}
