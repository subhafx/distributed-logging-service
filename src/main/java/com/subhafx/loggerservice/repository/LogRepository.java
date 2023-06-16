package com.subhafx.loggerservice.repository;

import com.subhafx.loggerservice.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Required for JPA Migration
 * Also, Can be reused in DB operations if needed
 */
public interface LogRepository extends JpaRepository<Log,Long> {
}
