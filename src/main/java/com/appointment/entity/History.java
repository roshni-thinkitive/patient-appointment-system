package com.appointment.entity;

import com.appointment.enums.HistoryType;
import com.appointment.enums.Relationship;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(unique = true)
    private UUID uuid = UUID.randomUUID();

    private String name;

    private LocalDate date;

    private String note;

    private LocalDate recordedDate;

    private Integer onSetAge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryType historyType;

    @Enumerated(EnumType.STRING)
    private Relationship relation;

    @Column(nullable = false)
    private String condition;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
