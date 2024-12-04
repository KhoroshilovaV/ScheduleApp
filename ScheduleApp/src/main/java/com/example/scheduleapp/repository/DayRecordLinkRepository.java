package com.example.scheduleapp.repository;

import com.example.scheduleapp.model.DayRecordLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayRecordLinkRepository extends JpaRepository<DayRecordLink, Long> {
}