package com.ef;

import com.ef.model.LogEntry;
import com.ef.services.LogDao;
import com.ef.services.LogReader;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Parser {
    public static void main(String[] args) {

        try {
            CommandLineProcessor commandLineProcessor = new CommandLineProcessor(args);

            LogDao logDao = new LogDao();
            logDao.deleteAll();
            LogReader logReader = new LogReader(commandLineProcessor.getAccessLog());
            List<LogEntry> entries = new LinkedList<>();
            LogEntry entry;

            while ((entry = logReader.getNextEntry()) != null) {
                entries.add(entry);

                if (entries.size() % 1000 == 0) {
                    logDao.addEntriesBulk(entries);
                    entries = new LinkedList<>();
                }
            }

            logDao.addEntriesBulk(entries);

            System.out.println(logDao.getIpExceedingThreshold(
                                commandLineProcessor.getStartDate(),
                                (commandLineProcessor.getDuration().equals("hourly")),
                                commandLineProcessor.getThreshold()));
        } catch( ParseException pe ) {
            System.out.println( "CLI exception: " + pe.getMessage());
        } catch (SQLException se) {
            System.out.println( "SQL exception: " + se.getMessage());
        } catch (IOException ioe) {
            System.out.println( "IO exception: " + ioe.getMessage());
        } catch (Exception e) {
            System.out.println( "Unexpected exception: " + e.getMessage());
        }

    }
}
