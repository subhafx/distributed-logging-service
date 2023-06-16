package com.subhafx.loggerservice.service;

import com.subhafx.loggerservice.dto.LogCSVDTO;
import com.subhafx.loggerservice.dto.LogDTO;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    /**
     * @param logDTO Received in request
     * @throws Exception handles job exceptions if any
     */
    @Job(name = "LogCSVWriterJob")
    public void writeLogsToCSV(LogDTO logDTO) throws Exception {
        CSVWriterService
                .getInstance()
                .write(LogCSVDTO.mapTo(logDTO));
    }
}
