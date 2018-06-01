/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ervits.csvdatagenerator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aervits
 */
public class CSVWriter {

    private static final Logger LOG = Logger.getLogger(CSVWriter.class.getName());
    private static final String SAMPLE_CSV_FILE = "./sample.csv";
    private static final int MAX = 1000000;
    public static void main(String[] args) throws IOException {
        
        LOG.info("Generating CSV file with ".concat(Integer.toString(MAX)).concat(" records"));
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("U_ID", "TIME_IN_ISO", "VAL", "BATCH_ID", "JOB_ID", "POS_ID"));) {
            for (int i = 0; i < MAX; i++) {
                LocalDate date = LocalDate.now();
                String dtText = date.format(BASIC_ISO_DATE);

                csvPrinter.printRecord(Arrays.asList(
                        UUID.randomUUID(),
                        dtText,
                        "val".concat(dtText),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID()));
            }

            csvPrinter.flush();
        }
        LOG.info("All Done.");
    }
}
