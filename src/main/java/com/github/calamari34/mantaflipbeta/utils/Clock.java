package com.github.calamari34.mantaflipbeta.utils;

public class Clock {
    private long endTime;

    public void schedule(long milliseconds) {
        this.endTime = System.currentTimeMillis() + milliseconds;
    }

    public long getRemainingTime() {
        return endTime - System.currentTimeMillis();
    }


    public boolean passed() {
        return System.currentTimeMillis() >= endTime;
    }
}