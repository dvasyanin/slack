package com.dvasyanin;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.dvasyanin.Utils.getDateMinusMonth;
import static com.dvasyanin.Utils.unixToTimestamp;


public class App {
    public static void main(String[] args) throws SQLException {

//        final String URL = "jdbc:postgresql://127.0.0.1:5434/slack";
        final String URL = "jdbc:postgresql://host.docker.internal:5434/slack";
        final String USER = "postgres";
        final String PASSWORD = "postgres";

        final String SLACK_TOKEN = "xoxp-121046473458-473715617431-709809922311-33be156b88996700cbe89ae8159cc2b0";
        final int SLACK_COUNT_TOTAL = 1000;
        final int SLACK_PAGES_TOTAL = 20;

        String name, userAgent, ip;
        Timestamp dateFirst, dateLast;
        int count;
        JSONObject statUser;

        Slack slack = new Slack();
        Database database = new Database();
        Connection connection = database.connectionDb(URL, USER, PASSWORD);

        //получаю максимальную дату из БД, если в БД данных еще нет, тогда
        //дату ставлю за месяц назад
        Timestamp maxDateInDatabase = database.queryGetMaxDate(connection);
        if (maxDateInDatabase == null) {
            maxDateInDatabase = new Timestamp(getDateMinusMonth());
        }

        for (int i = 1; i <= SLACK_PAGES_TOTAL; i++) {
            for (Object s : slack.getStatistics(SLACK_TOKEN, SLACK_COUNT_TOTAL, i)) {
                statUser = (JSONObject) s;
                name = statUser.getString("username");
                count = statUser.getInt("count");
                dateFirst = unixToTimestamp(statUser.getLong("date_first"));
                dateLast = unixToTimestamp(statUser.getLong("date_last"));
                ip = statUser.getString("ip");
                userAgent = statUser.getString("user_agent");
                // проверяю текущая дата раньше чем максимальная дата из базы?
                if (dateFirst.after(maxDateInDatabase)) {
                    database.writeUserLog(connection, name, dateFirst, dateLast, count, ip, userAgent);
                } else {
                    connection.close();
                    System.out.println("База обновлена");
                    System.exit(0);
                }
            }
        }
        connection.close();
        System.out.println("База обновлена");

    }
}
