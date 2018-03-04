package com.ef.util;

public interface Constants {
    static String INSERT_LOG_ENTRY_QUERY =
            "insert into log_entries(start_date, ip_address, http_request, response_code, browser) values(?, ?, ?, ?, ?)";

    static String GET_IP_ADDRESSES_QUERY =
            "select ip_address, count(ip_address) from log_entries " +
                    "where start_date between ? and ? group by ip_address having count(ip_address) > ? order by 2 desc";

}
