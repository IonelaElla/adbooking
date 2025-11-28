package com.generatik.adbooking.bookingrequest.entities;


import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import com.generatik.adbooking.bookingrequest.dto.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking_request")
public class BookingRequestEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "booking_id_gen"
    )
    @SequenceGenerator(
            name = "booking_id_gen",
            sequenceName = "booking_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Integer id;

    @Setter(AccessLevel.NONE)
    @Column(name = "uuid")
    private UUID uuid = UUID.randomUUID();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_space_id", nullable = false)
    private AdSpaceEntity adSpace;

    @NotBlank
    @Column(name = "advertiser_name", nullable = false)
    private String advertiserName;

    @NotBlank
    @Email
    @Column(name = "advertiser_email", nullable = false)
    private String advertiserEmail;

    @NotNull
    @Future
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 255)
    private BookingStatus status;

    @NotNull
    @Column(name = "total_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

}
