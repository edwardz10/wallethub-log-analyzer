package com.ef.services;

import com.ef.model.LogEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Reads an access log line by line
 */
public class LogReader {
    private static Log LOG = LogFactory.getLog(LogReader.class);

    private String path;
    private BufferedReader bufferedReader;
    private SimpleDateFormat simpleDateFormat;

    public LogReader(final String path) throws FileNotFoundException {
        this.path = path;
        bufferedReader = new BufferedReader(new FileReader(path));
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
    }

    /**
     * Returns the next LogEntry object
     * from the access log
     * @return
     */
    public LogEntry getNextEntry() {
        LogEntry logEntry = null;

        try {
            String nextLine = bufferedReader.readLine();
            String[] tokens = nextLine.split("\\|");
            logEntry = new LogEntry(simpleDateFormat.parse(tokens[0]),
                                    tokens[1],
                                    tokens[2],
                                    Integer.valueOf(tokens[3]),
                                    tokens[4]);
        } catch (IOException ioe) {
            try {
                bufferedReader.close();
            } catch (IOException ioe2) {
                LOG.error("Failed to close " + path + ": " + ioe2.getMessage());
            }
        } finally {
            return logEntry;
        }
    }
}
