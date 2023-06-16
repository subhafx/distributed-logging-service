package com.subhafx.loggerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object for CSV Operations
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class LogCSVDTO {
    private Long id;
    private Long unix_ts;
    private Long user_id;
    private String event_name;

    public static LogCSVDTO mapTo(LogDTO logDTO){
        return new LogCSVDTO(logDTO.getId(), logDTO.getUnix_ts(), logDTO.getUser_id(), logDTO.getEvent_name());
    }

    public String[] toStringArray(){
        return new String[] {String.valueOf(this.id), String.valueOf(this.unix_ts), String.valueOf(this.user_id), this.event_name};
    }

    public String[] getCSVHeaders(){
        return new String[]{"id", "unix_ts", "user_id", "event_name"};
    }
}
