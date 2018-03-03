package com.ef.services;

import com.ef.model.LogEntry;

import java.sql.*;
import java.util.List;

public class LogDao {

    private Connection connection;

    public LogDao() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/wallethub", "root", "starlight");
    }

    public void addSingleEntry(LogEntry entry) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "insert into log_entries(start_date, ip_address, http_request, response_code, browser) values(?, ?, ?, ?, ?)");
        ps.setDate(1, new java.sql.Date(entry.getDate().getTime()));
        ps.setString(2, entry.getIpAddress());
        ps.setString(3, entry.getHttpRequest());
        ps.setInt(4, entry.getResponseCode());
        ps.setString(5, entry.getBrowser());
        ps.executeUpdate();
    }

    public void addEntriesBulk(List<LogEntry> entries) throws SQLException {
        System.out.println("Number of bulk records: " + entries.size());
        PreparedStatement ps = connection.prepareStatement(
                "insert into log_entries(start_date, ip_address, http_request, response_code, browser) values(?, ?, ?, ?, ?)");
        connection.setAutoCommit(false);

        for (LogEntry entry : entries) {
            ps.setTimestamp(1, new java.sql.Timestamp(entry.getDate().getTime()));
            ps.setString(2, entry.getIpAddress());
            ps.setString(3, entry.getHttpRequest());
            ps.setInt(4, entry.getResponseCode());
            ps.setString(5, entry.getBrowser());
            ps.addBatch();
        }

        ps.executeBatch();
        connection.commit();
    }

    public void deleteAll() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("delete from log_entries");
    }

    public String getIpExceedingThreshold(java.util.Date startDate, boolean hourly, int threshold) {
//        select count(ip_address), ip_address from log_entries where start_date between '2017-01-01 10:00:00' and '2017-01-01 12:00:00' group by ip_address having count(ip_address) > 200;
        return "";
    }
}
