package com.dvasyanin;

import java.util.Timer;


public class App {
    public static void main(String[] args) {
        Timer timer = new Timer();
        ScheduledTask scheduledTask = new ScheduledTask();
        long delay = 1000L;
        long period = 1000L * 60L * 60L * 24L;
        timer.scheduleAtFixedRate(scheduledTask, delay, period);
    }
}