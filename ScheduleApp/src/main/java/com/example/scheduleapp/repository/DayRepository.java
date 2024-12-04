package com.example.scheduleapp.repository;

import com.example.scheduleapp.model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DayRepository extends JpaRepository<Day, Long> {

    @Query("SELECT d FROM Day d JOIN FETCH d.dayRecordLinks drl JOIN FETCH drl.record")
    List<Day> findAllWithRecords();
}