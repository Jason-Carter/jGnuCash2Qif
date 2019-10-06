package com.maxtech.gnucash.sqlqif;

import com.maxtech.gnucash.sqlqif.library.Extractor;
import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {

        Options options = new Options();

        Option dataSource = new Option("d", "datasource", true, "Full path to the Sqlite file");
        dataSource.setRequired(true);
        options.addOption(dataSource);

        Option output = new Option("o", "output", true, "Output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String dataSourceFilePath = cmd.getOptionValue("datasource");
        String outputFilePath = cmd.getOptionValue("output");

        Extractor runExtract = new Extractor();
        runExtract.ExtractData(dataSourceFilePath, outputFilePath);

        System.out.println("INFO: jGnuCashSql2Qif successfully completed.");
    }
}
