package com.ef.model;

import java.util.Date;

/**
 * Entity that represents a
 * line in the log file
 */
public class LogEntry {

    private Date date;
    private String ipAddress;
    private String httpRequest;
    private Integer responseCode;
    private String browser;

    public LogEntry(Date date, String ipAddress, String httpRequest, Integer responseCode, String browser) {
        this.date = date;
        this.ipAddress = ipAddress;
        this.httpRequest = httpRequest;
        this.responseCode = responseCode;
        this.browser = browser;
    }

    public Date getDate() {
        return date;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getHttpRequest() {
        return httpRequest;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public String getBrowser() {
        return browser;
    }

    @Override
    public String toString() {
        return "Date: " + date
                + ", ip address: " + ipAddress
                + ", http request: " + httpRequest
                + ", response: " + responseCode
                + ", browser: " + browser;
    }
}
