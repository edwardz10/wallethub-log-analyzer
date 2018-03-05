package com.ef;

import com.ef.model.IpStatistics;
import com.ef.model.LogEntry;
import com.ef.services.*;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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
            Class.forName(applicationProperties.getProperty("mysql.jdbc.driver"));
            Connection connection = DriverManager.getConnection(
                    applicationProperties.getProperty("mysql.connection.url"),
                    applicationProperties.getProperty("mysql.user"),
                    applicationProperties.getProperty("mysql.password"));

            LogEntryDao logDao = new LogEntryDao(connection);
            IpStatisticsDao ipStatisticsDao = new IpStatisticsDao(connection);

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

            LOG.info("Save IP address statistics into the database...");
            for (IpStatistics ipStat : ipStatistics) {
                System.out.println(ipStat);

                /**
                 * For each IpStatistics entity:
                 * 1. Check, whether a record with
                 * the same IP address exists.
                 * 2. If not, then insert a new IpStatistics entity.
                 * 3. If yes, then update the existing entity
                 * with a new 'count' field.
                 */
                int countStatistics = ipStatisticsDao.getCountWithIpAddress(ipStat);

                if (countStatistics == 0) {
                    ipStatisticsDao.addIpStatistics(ipStat);
                } else {
                    ipStatisticsDao.updateIpStatistics(ipStat);
                }
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
