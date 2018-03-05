package com.ef.services;

import com.ef.model.IpStatistics;
import com.ef.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides DB operations with
 * IpStatistics entities.
 */
public class IpStatisticsDao {
    private static Log LOG = LogFactory.getLog(IpStatisticsDao.class);

    private Connection connection;

    public IpStatisticsDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Count ip statistics with a
     * given ip address
     * @param ipStatistics
     * @return
     * @throws SQLException
     */
    public int getCountWithIpAddress(IpStatistics ipStatistics) throws  SQLException {
        int result = 0;
        PreparedStatement ps = connection.prepareStatement(Constants.GET_COUNT_STATISTICS_WITH_IP_QUERY);
        ps.setString(1, ipStatistics.getIpAddress());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            result = rs.getInt(1);
        }

        return result;
    }

    /**
     * Add a single IpStatistics
     * entity to the database
     * @param ipStatistics
     * @throws SQLException
     */
    public void addIpStatistics(IpStatistics ipStatistics) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(Constants.INSERT_IP_STATISTICS_QUERY);
        ps.setString(1, ipStatistics.getIpAddress());
        ps.setInt(2, ipStatistics.getCount());
        ps.executeUpdate();
    }

    /**
     * Update existing IPStatistics
     * entrity with a new count value
     * @param ipStatistics
     * @throws SQLException
     */
    public void updateIpStatistics(IpStatistics ipStatistics) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(Constants.UPDATE_IP_STATISTICS_QUERY);
        ps.setString(2, ipStatistics.getIpAddress());
        ps.setInt(1, ipStatistics.getCount());
        ps.executeUpdate();
    }

}
