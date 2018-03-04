package com.ef.model;

/**
 * Entity that represents statistics
 * of requests from an IP address
 */
public class IpStatistics {

    private String ipAddress;
    private Integer count;

    public IpStatistics(String ipAddress, Integer count) {
        this.ipAddress = ipAddress;
        this.count = count;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public String toString() {
        return ipAddress + ", count: " + count;
    }
}
