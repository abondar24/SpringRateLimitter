package org.abondar.spring.ratelimitter;


public class RateObj {
    private final int limit;
    private final long period;

    public RateObj(int limit, long period) {
        this.limit = limit;
        this.period = period;
    }

    public int getLimit() {
        return limit;
    }

    public long getPeriod() {
        return period;
    }

}
