package com.dvasyanin;

import java.sql.*;

public class Database {

    public Connection connectionDb(String url, String user, String password) {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public void queryWrite(Connection connection, String name, Timestamp dataFirst, Timestamp dataLast, int count, String ip, String userAgent) throws SQLException {
        String query = "INSERT INTO logs_slack(user_name, count, date_first, date_last, ip, user_agent) VALUES(?,?,?,?,?,?)";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, name);
        pst.setInt(2, count);
        pst.setTimestamp(3, dataFirst);
        pst.setTimestamp(4, dataLast);
        pst.setString(5, ip);
        pst.setString(6, userAgent);

        pst.executeUpdate();
    }

    public Timestamp queryGetMaxDate(Connection connection) throws SQLException {
        PreparedStatement pst = connection.prepareStatement("SELECT MAX(date_first) FROM logs_slack");
        ResultSet resultSet = pst.executeQuery();
        resultSet.next();
        Timestamp maxDate = resultSet.getTimestamp(1);
        return maxDate;
    }
}
