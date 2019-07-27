package com.dvasyanin;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;


public class App {
    public static void main(String[] args) throws SQLException {
        long startTime = System.currentTimeMillis();

        final String URL = "jdbc:postgresql://127.0.0.1:5432/slack";
        final String USER = "postgres";
        final String PASSWORD = "postgres";

        final String SLACK_TOKEN = "xoxp-121046473458-473715617431-682182922855-5887d33a728ce2c7044a9cc8baa16a21";
        final int SLACK_COUNT_TOTAL = 1000;
        final int SLACK_PAGES_TOTAL = 20;

        String name, userAgent, ip;
        Timestamp dateFirst, dateLast;
        int count;
        JSONObject statUser;

        Slack slack = new Slack();
        Database database = new Database();
        Connection connection = database.connectionDb(URL, USER, PASSWORD);
        Utils utils = new Utils();

        //получаю максимальную дату из БД, если в БД данных еще нет, тогда
        //дату ставлю за месяц назад
        Timestamp maxDateInDatabase = database.queryGetMaxDate(connection);
        if (maxDateInDatabase == null) {
            maxDateInDatabase = new Timestamp(utils.getDateMinusMonth());
        }

        for (int i = 1; i <= SLACK_PAGES_TOTAL; i++) {
            for (Object s : slack.slackGetStatistics(SLACK_TOKEN, SLACK_COUNT_TOTAL, i)) {
                statUser = (JSONObject) s;
                name = statUser.getString("username");
                count = statUser.getInt("count");
                dateFirst = utils.unixToTimestamp(statUser.getLong("date_first"));
                dateLast = utils.unixToTimestamp(statUser.getLong("date_last"));
                ip = statUser.getString("ip");
                userAgent = statUser.getString("user_agent");
                // проверяю текущая дата раньше чем максимальная дата из базы?
                if (dateFirst.after(maxDateInDatabase)) { //
                    database.queryWrite(connection, name, dateFirst, dateLast, count, ip, userAgent);
                } else {
                    long finishTime = System.currentTimeMillis();
                    System.out.println((finishTime - startTime) / 1000 + " s");
                    System.out.println("База обновлена");
                    connection.close();
                    System.exit(0);
                }
            }
        }
        connection.close();
        long finishTime = System.currentTimeMillis();
        System.out.println((finishTime - startTime) / 1000 + " s");
    }
}
