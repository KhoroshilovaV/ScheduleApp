package com.example.scheduleapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "day_record_links")
public class DayRecordLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "day_id", nullable = false)
    private Day day;

    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;
}