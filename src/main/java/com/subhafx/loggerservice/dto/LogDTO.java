package com.subhafx.loggerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object for Http Requests
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class LogDTO {
    private Long id;
    private Long unix_ts;
    private Long user_id;
    private String event_name;
}
