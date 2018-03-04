package com.ef.services;

import com.ef.model.IpStatistics;
import com.ef.model.LogEntry;
import com.ef.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDao {
    private static Log LOG = LogFactory.getLog(LogDao.class);

    private Connection connection;

    public LogDao() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(Constants.DB_CONNECTION_URL, Constants.DB_USER, Constants.DB_PWD);
    }

    public void addSingleEntry(LogEntry entry) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(Constants.INSERT_LOG_ENTRY_QUERY);
        ps.setTimestamp(1, new java.sql.Timestamp(entry.getDate().getTime()));
        ps.setString(2, entry.getIpAddress());
        ps.setString(3, entry.getHttpRequest());
        ps.setInt(4, entry.getResponseCode());
        ps.setString(5, entry.getBrowser());
        ps.executeUpdate();
    }

    public void addEntriesBulk(List<LogEntry> entries) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(Constants.INSERT_LOG_ENTRY_QUERY);
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

    public List<IpStatistics> getIpExceedingThreshold(java.util.Date startDate, boolean hourly, int threshold) throws SQLException {
        List<IpStatistics> ipStatistics = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(Constants.GET_IP_ADDRESSES_QUERY);
        ps.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
        ps.setTimestamp(2, new java.sql.Timestamp(getEndDate(startDate, hourly).getTime()));
        ps.setInt(3, threshold);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ipStatistics.add(new IpStatistics(rs.getString(1), rs.getInt(2)));
        }

        return ipStatistics;
    }

    protected java.util.Date getEndDate(final java.util.Date startDate, boolean hourly) {
        if (hourly) {
            return new java.util.Date(startDate.getTime() + 1000*(60*60 - 1));
        } else {
            return new java.util.Date(startDate.getTime() + 1000*(60*60*24 - 1));
        }
    }
}
