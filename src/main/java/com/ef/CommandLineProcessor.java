package com.ef;

import org.apache.commons.cli.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandLineProcessor {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
    private String[] args;
    private String accessLog;
    private Date startDate;
    private String duration;
    private Integer threshold;

    public CommandLineProcessor(final String[] args) throws ParseException {
        this.args = args;
        parse();
    }

    protected void parse() throws ParseException {
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption( "a", "accesslog", true, "access log");
        options.addOption( "s", "startDate", true, "start date");
        options.addOption( "d", "duration", true, "duration");
        options.addOption( "t", "threshold", true, "threshold");

        // parse the command line arguments
        CommandLine line = parser.parse(options, args);

        if (!line.hasOption("accesslog")) {
            throw new ParseException("Parameter \"accesslog\" is missing");
        }

        this.accessLog = line.getOptionValue("accesslog");

        if (!line.hasOption("startDate")) {
            throw new ParseException("Parameter \"startDate\" is missing");
        }

        try {
            this.startDate = simpleDateFormat.parse(line.getOptionValue("startDate"));
        } catch (java.text.ParseException pe) {
            throw new ParseException(pe.getMessage());
        }

        if (!line.hasOption("duration")) {
            throw new ParseException("Parameter \"duration\" is missing");
        }

        this.duration = line.getOptionValue("duration");

        if (!duration.equals("hourly") && !duration.equals("daily")) {
            throw new ParseException("Parameter \"duration\" is incorrect: must be \"hourly\" or \"daily\"");
        }

        if (!line.hasOption("threshold")) {
            throw new ParseException("Parameter \"threshold\" is missing");
        }

        try {
            this.threshold = Integer.parseInt(line.getOptionValue("threshold"));

            if (this.threshold < 0) {
                throw new ParseException("Parameter \"threshold\" cannot be negative");
            }
        } catch (NumberFormatException nfe) {
            throw new ParseException("Parameter \"threshold\" is not a number");
        }
    }

    public String getAccessLog() {
        return accessLog;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getDuration() {
        return duration;
    }

    public Integer getThreshold() {
        return threshold;
    }
}
