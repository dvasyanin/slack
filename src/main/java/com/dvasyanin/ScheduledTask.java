package com.dvasyanin;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.TimerTask;

import static com.dvasyanin.Utils.getDateMinusMonth;
import static com.dvasyanin.Utils.unixToTimestamp;

public class ScheduledTask extends TimerTask {
    Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().load();

    final String URL = dotenv.get("URL");
    final String USER = dotenv.get("USER");
    final String PASSWORD = dotenv.get("PASSWORD");

    final String SLACK_TOKEN = dotenv.get("SLACK_TOKEN");
    final int SLACK_COUNT_TOTAL = Integer.parseInt(dotenv.get("SLACK_COUNT_TOTAL"));
    final int SLACK_PAGES_TOTAL = Integer.parseInt(dotenv.get("SLACK_PAGES_TOTAL"));

    @Override
    public void run() {
        String name, userAgent, ip;
        Timestamp dateFirst, dateLast;
        int count;
        boolean checkLoop = false;
        JSONObject statUser;

        Slack slack = new Slack();
        Database database = new Database();
        Connection connection = database.connectionDb(URL, USER, PASSWORD);

        //получаю максимальную дату из БД, если в БД данных еще нет, тогда
        //дату ставлю за месяц назад
        Timestamp maxDateInDatabase = null;
        try {
            maxDateInDatabase = database.queryGetMaxDate(connection);
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
                        checkLoop = true;
                        break;
                    }
                }
                if (checkLoop == true) {
                    break;
                }
            }
            connection.close();
            System.out.println("База обновлена");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
