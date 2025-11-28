package com.generatik.adbooking.bookingrequest.services;

import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import com.generatik.adbooking.adspace.exceptions.InvalidEntityException;
import com.generatik.adbooking.adspace.exceptions.ResourceNotFoundException;
import com.generatik.adbooking.adspace.exceptions.ResourcesConflictException;
import com.generatik.adbooking.adspace.exceptions.UnavailableResourceException;
import com.generatik.adbooking.adspace.repositories.AdSpaceRepository;
import com.generatik.adbooking.bookingrequest.dto.BookingRequestCreateDto;
import com.generatik.adbooking.bookingrequest.dto.BookingRequestResponseDto;
import com.generatik.adbooking.bookingrequest.dto.enums.BookingStatus;
import com.generatik.adbooking.bookingrequest.entities.BookingRequestEntity;
import com.generatik.adbooking.bookingrequest.mappers.BookingRequestMapper;
import com.generatik.adbooking.bookingrequest.repositories.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;


@Service
@RequiredArgsConstructor
public class BookingRequestService {
    private final BookingRequestRepository bookingRequestRepository;
    private final AdSpaceRepository adSpaceRepository;

    @Transactional
    public BookingRequestResponseDto createBookingRequest(BookingRequestCreateDto bookingRequestCreateDto) {
        validateBookingRequestDates(bookingRequestCreateDto);

        AdSpaceEntity adSpaceEntity = this.adSpaceRepository.findByUuid(bookingRequestCreateDto.getAdSpaceUuid()).orElseThrow(() -> new ResourceNotFoundException(String.format("Ad space with uuid %s not found", bookingRequestCreateDto.getAdSpaceUuid())));
        BookingRequestEntity entity = BookingRequestMapper.toEntity(bookingRequestCreateDto, adSpaceEntity);

        validateBookingRequest(entity, adSpaceEntity);

        entity.setTotalCost(computeTotalCost(bookingRequestCreateDto.getStartDate(), bookingRequestCreateDto.getEndDate(), adSpaceEntity.getPricePerDay()));

        BookingRequestEntity savedEntity = this.bookingRequestRepository.save(entity);
        return BookingRequestMapper.toDto(savedEntity);
    }

    private BigDecimal computeTotalCost(LocalDate startDate, LocalDate endDate, BigDecimal pricePerDay) {
        return BigDecimal.valueOf(Period.between(startDate, endDate).getDays()).multiply(pricePerDay);

    }

    private void validateBookingRequestDates(BookingRequestCreateDto bookingRequestCreateDto) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        LocalDate startDate = bookingRequestCreateDto.getStartDate();
        LocalDate endDate = bookingRequestCreateDto.getEndDate();
        if (bookingRequestCreateDto.getStartDate().isBefore(tomorrow)) {
            throw new InvalidEntityException("Start date must be in the future");
        }

        if (Period.between(startDate, endDate).getDays() < 7) {
            throw new InvalidEntityException("End date must be after start date. Minimum booking duration: 7 days");
        }
    }

    private void validateBookingRequest(BookingRequestEntity bookingRequestEntity, AdSpaceEntity adSpaceEntity) {
        if (adSpaceEntity.getAvailabilityStatus() != AdSpaceAvailabilityStatus.AVAILABLE) {
            throw new UnavailableResourceException(String.format("Ad Space status must be %s", AdSpaceAvailabilityStatus.AVAILABLE));
        }

        if (this.bookingRequestRepository.existsAtLeastOneOverlappingDateRange(adSpaceEntity.getUuid(), bookingRequestEntity.getStartDate(), bookingRequestEntity.getEndDate(), BookingStatus.APPROVED)) {
            throw new ResourcesConflictException("Cannot create overlapping bookings for the same space (for approved bookings)");
        }

    }


}

