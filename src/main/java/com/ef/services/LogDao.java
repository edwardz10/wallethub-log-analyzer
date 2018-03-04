package com.ef.services;

import com.ef.model.IpStatistics;
import com.ef.model.LogEntry;
import com.ef.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides DB operations with
 * LogEntry entities.
 */
public class LogDao {
    private static Log LOG = LogFactory.getLog(LogDao.class);

    private Connection connection;

    public LogDao(String jdbcDriver, String connectionUrl, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName(jdbcDriver);
        connection = DriverManager.getConnection(connectionUrl, user, password);
    }

    /**
     * Adds a single LogEntry entity
     * to the database.
     * @param entry
     * @throws SQLException
     */
    public void addSingleEntry(LogEntry entry) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(Constants.INSERT_LOG_ENTRY_QUERY);
        ps.setTimestamp(1, new java.sql.Timestamp(entry.getDate().getTime()));
        ps.setString(2, entry.getIpAddress());
        ps.setString(3, entry.getHttpRequest());
        ps.setInt(4, entry.getResponseCode());
        ps.setString(5, entry.getBrowser());
        ps.executeUpdate();
    }

    /**
     * Addd a bulk of LogEntry entities to
     * the DB to speed up the performance.
     * @param entries
     * @throws SQLException
     */
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

    /**
     * Clears up the 'log_entities' table
     * @throws SQLException
     */
    public void deleteAll() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("delete from log_entries");
    }

    /**
     * Executes the SQL query that returns
     * IP address statistics from the DB table
     * @param startDate
     * @param hourly
     * @param threshold
     * @return list of LogEntry objects
     * @throws SQLException
     */
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

    /**
     * Calculates end date by
     * start date and duration
     * @param startDate
     * @param hourly
     * @return
     */
    protected java.util.Date getEndDate(final java.util.Date startDate, boolean hourly) {
        if (hourly) {
            return new java.util.Date(startDate.getTime() + 1000*(60*60 - 1));
        } else {
            return new java.util.Date(startDate.getTime() + 1000*(60*60*24 - 1));
        }
    }
}
