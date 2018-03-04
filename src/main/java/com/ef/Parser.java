package com.ef;

import com.ef.model.IpStatistics;
import com.ef.model.LogEntry;
import com.ef.services.ApplicationProperties;
import com.ef.services.CommandLineProcessor;
import com.ef.services.LogDao;
import com.ef.services.LogReader;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Main application class.
 */
public class Parser {
    private static Log LOG = LogFactory.getLog(Parser.class);

    public static void main(String[] args) {

        try {
            LOG.info("Load application properties...");
            ApplicationProperties applicationProperties = new ApplicationProperties();
            LOG.info("Parse command line parameters...");
            CommandLineProcessor commandLineProcessor = new CommandLineProcessor(args);

            LOG.info("Initialize DB connection...");
            LogDao logDao = new LogDao(
                    applicationProperties.getProperty("mysql.jdbc.driver"),
                    applicationProperties.getProperty("mysql.connection.url"),
                    applicationProperties.getProperty("mysql.user"),
                    applicationProperties.getProperty("mysql.password"));

            LOG.info("Clear 'log_entities' table...");
            logDao.deleteAll();

            LogReader logReader = new LogReader(commandLineProcessor.getAccessLog());
            List<LogEntry> entries = new LinkedList<>();
            LogEntry entry;

            LOG.info("Load data into the database...");
            while ((entry = logReader.getNextEntry()) != null) {
                entries.add(entry);

                if (entries.size() % 1000 == 0) {
                    logDao.addEntriesBulk(entries);
                    entries = new LinkedList<>();
                }
            }

            logDao.addEntriesBulk(entries);

            LOG.info("Calculate IP address statistics: from: "
                    + commandLineProcessor.getStartDate()
                    + ", duration: " + commandLineProcessor.getDuration()
                    + ", threshold: " + commandLineProcessor.getThreshold());

            List<IpStatistics> ipStatistics = logDao.getIpExceedingThreshold(
                    commandLineProcessor.getStartDate(),
                    (commandLineProcessor.getDuration().equals("hourly")),
                    commandLineProcessor.getThreshold());

            for (IpStatistics ipStat : ipStatistics) {
                System.out.println(ipStat);
            }

        } catch( ParseException pe ) {
            LOG.error( "CLI exception: " + pe.getMessage());
        } catch (SQLException se) {
            LOG.error( "SQL exception: " + se.getMessage());
        } catch (IOException ioe) {
            LOG.error( "IO exception: " + ioe.getMessage());
        } catch (Exception e) {
            LOG.error( "Unexpected exception: " + e.getMessage());
        }

    }
}
