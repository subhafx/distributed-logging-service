package com.subhafx.loggerservice.service;

import com.opencsv.CSVWriter;
import com.subhafx.loggerservice.dto.LogCSVDTO;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Calendar;

/**
 * Singleton Class
 */
public class CSVWriterService {

    private String FILE_NAME =  "./temp_logs_0.csv";
    private CSVWriterService(){
    }

    /**
     * Helps to maintain Singleton instance
     */
    private static final class InstanceHolder {
        private static final CSVWriterService instance = new CSVWriterService();
    }

    public static CSVWriterService getInstance(){
        return InstanceHolder.instance;
    }

    /**
     * Files will be stored under temp folder inside project directory
     */
    private void setFileName(){
        this.FILE_NAME = "./temp/temp_logs_" + Calendar.getInstance().get(Calendar.SECOND) +".csv";
    }

    public void write(LogCSVDTO logCSVDTO) throws Exception {
        this.setFileName();

        File file = Paths.get(FILE_NAME).toFile();
        try(Writer writer = new FileWriter(file, true)) {
            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            csvWriter.writeNext(logCSVDTO.toStringArray());
            csvWriter.close();
        }
    }
}
