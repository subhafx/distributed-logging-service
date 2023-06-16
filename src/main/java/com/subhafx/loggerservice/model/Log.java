package com.subhafx.loggerservice.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "logs")
public class Log {
    @Id
    private Long id;
    private Long unix_ts;
    private Long user_id;
    private String event_name;
}
