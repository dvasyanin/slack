package com.dvasyanin;

import java.util.Timer;


public class App {
    public static void main(String[] args) {
        Timer timer = new Timer();
        ScheduledTask scheduledTask = new ScheduledTask();
        timer.schedule(scheduledTask, 0, 60000);
    }
}
