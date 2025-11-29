package com.generatik.adbooking.bookingrequest.repositories;

import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import com.generatik.adbooking.bookingrequest.dto.enums.BookingStatus;
import com.generatik.adbooking.bookingrequest.entities.BookingRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookingRequestRepository extends JpaRepository<BookingRequestEntity, Integer> {

    Optional<BookingRequestEntity> findByUuid(UUID uuid);

    @Query("""
            SELECT COUNT(bookingRequestEntity) > 0 FROM BookingRequestEntity bookingRequestEntity
                WHERE bookingRequestEntity.adSpace.uuid = :adSpaceUuid
                AND bookingRequestEntity.status = :bookingRequestStatus
                AND (bookingRequestEntity.startDate BETWEEN :startDate AND :endDate
                OR bookingRequestEntity.endDate BETWEEN :startDate AND :endDate
                OR :startDate BETWEEN bookingRequestEntity.startDate AND bookingRequestEntity.endDate
                OR :endDate BETWEEN bookingRequestEntity.startDate AND bookingRequestEntity.endDate)
            """)
    boolean existsAtLeastOneOverlappingDateRange(@Param("adSpaceUuid") UUID adSpaceUuid, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("bookingRequestStatus") BookingStatus bookingRequestStatus);


}
