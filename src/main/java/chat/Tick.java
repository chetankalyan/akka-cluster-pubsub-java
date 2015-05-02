package chat;

/**
 * Created by chetan.k on 5/1/15.
 */
public class Tick {
    private long timestamp;
    public Tick() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tick)) {
            return false;
        }

        Tick tick = (Tick) o;

        return timestamp == tick.timestamp;

    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("timestamp", timestamp)
                .toString();
    }
}
