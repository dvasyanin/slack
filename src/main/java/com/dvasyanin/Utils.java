package com.dvasyanin;

import java.sql.Timestamp;
import java.util.Calendar;

public class Utils {

    //метод для получения даты с начала текущего дня (00:00:00 часов) в Unix формате
    public long getYesterdayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -1);

        return (calendar.getTimeInMillis() / 1000);
    }

    // откатываю дату на месяц назад
    public long getDateMinusMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -30);

        return (calendar.getTimeInMillis());
    }

    //метод перевода времени из Unix time в человекочитабельный 1999-30-08 04:05:06
    public Timestamp unixToTimestamp(long date) {
        Timestamp timestamp = new Timestamp(date * 1000);
        return timestamp;
    }
}
