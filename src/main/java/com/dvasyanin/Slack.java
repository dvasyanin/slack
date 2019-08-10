package com.dvasyanin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.dvasyanin.Utils.getYesterdayDate;

public class Slack {

    //метод получения статистики из Slack
    public JSONArray getStatistics(String token, int count, int pages) {
        String stat = getRequest(token, count, pages);
        JSONObject obj = new JSONObject(stat);
        JSONArray arr = obj.getJSONArray("logins");
        return arr;
    }

    // запрос в API Slack
    private String getRequest(String token, int count, int pages) {
        String str = new String();
        String slackUrl = "https://slack.com/api/team.accessLogs?token=" + token + "&count="
                + count + "&page=" + pages ; //+ "&before=" + getYesterdayDate()
        HttpURLConnection slackConnection = null;

        try {
            slackConnection = (HttpURLConnection) new URL(slackUrl).openConnection();
            slackConnection.setRequestMethod("GET");
            slackConnection.connect();

            if (HttpURLConnection.HTTP_OK == slackConnection.getResponseCode()) {
                Scanner scanner = new Scanner(slackConnection.getInputStream());
                while (scanner.hasNext())
                    str += scanner.nextLine();
                scanner.close();
            } else {
                System.out.println("fail: " + slackConnection.getResponseCode() + " " + slackConnection.getResponseMessage());
            }

        } catch (Throwable cause) {
            cause.printStackTrace();
        } finally {
            if (slackConnection != null) {
                slackConnection.disconnect();
            }
        }
        return str;
    }
}