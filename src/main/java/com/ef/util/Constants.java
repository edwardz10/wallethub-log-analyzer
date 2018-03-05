package com.ef.util;

/**
 * Prepared statements for SQL queries.
 */
public interface Constants {
    static String INSERT_LOG_ENTRY_QUERY =
            "insert into log_entries(start_date, ip_address, http_request, response_code, browser) values(?, ?, ?, ?, ?)";

    static String GET_IP_ADDRESSES_QUERY =
            "select ip_address, count(ip_address) from log_entries " +
                    "where start_date between ? and ? group by ip_address having count(ip_address) > ? order by 2 desc";

    static String GET_COUNT_STATISTICS_WITH_IP_QUERY =
            "select count(*) from ip_statistics where ip_address = ?";

    static String INSERT_IP_STATISTICS_QUERY =
            "insert into ip_statistics(ip_address, cnt) values(?, ?)";

    static String UPDATE_IP_STATISTICS_QUERY =
            "update ip_statistics set cnt = ? where ip_address = ?";
}
