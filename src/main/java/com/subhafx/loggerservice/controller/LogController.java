package com.subhafx.loggerservice.controller;

import com.subhafx.loggerservice.dto.LogDTO;
import com.subhafx.loggerservice.service.LogService;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/log")
public class LogController {
    private LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * @param logDTO Request Payload in Request Body
     * @apiNote POST /api/log
     *  Request Body
     *  {
     *   "id": 1234,
     *   "unix_ts": 1684129671,
     *   "user_id": 123456,
     *   "event_name": "login"
     *   }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void writeLog(@RequestBody LogDTO logDTO){
        BackgroundJob.enqueue(() -> logService.writeLogsToCSV(logDTO));
    }

}
