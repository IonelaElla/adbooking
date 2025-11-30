package com.generatik.adbooking.bookingrequest;

import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceType;
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
import com.generatik.adbooking.bookingrequest.repositories.BookingRequestRepository;
import com.generatik.adbooking.bookingrequest.services.BookingRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingRequestServiceTest {

    @Mock
    private BookingRequestRepository bookingRequestRepository;

    @Mock
    private AdSpaceRepository adSpaceRepository;

    @InjectMocks
    private BookingRequestService bookingRequestService;

    private AdSpaceEntity availableAdSpace;
    private AdSpaceEntity unavailableAdSpace;

    private BookingRequestEntity pendingBooking;
    private BookingRequestEntity approvedBooking;

    private UUID bookingUuid;
    private UUID adSpaceUuid;

    @BeforeEach
    void setUp() {
        adSpaceUuid = UUID.randomUUID();

        availableAdSpace = new AdSpaceEntity();
        availableAdSpace.setId(1);
        availableAdSpace.setName("Billboard Central");
        availableAdSpace.setCity("Cluj");
        availableAdSpace.setAddress("Strada X");
        availableAdSpace.setType(AdSpaceType.BILLBOARD);
        availableAdSpace.setPricePerDay(new BigDecimal("100.00"));
        availableAdSpace.setAvailabilityStatus(AdSpaceAvailabilityStatus.AVAILABLE);

        unavailableAdSpace = new AdSpaceEntity();
        unavailableAdSpace.setId(2);
        unavailableAdSpace.setName("Booked Billboard");
        unavailableAdSpace.setCity("Cluj");
        unavailableAdSpace.setAddress("Strada Y");
        unavailableAdSpace.setType(AdSpaceType.BILLBOARD);
        unavailableAdSpace.setPricePerDay(new BigDecimal("100.00"));
        unavailableAdSpace.setAvailabilityStatus(AdSpaceAvailabilityStatus.BOOKED);

        pendingBooking = new BookingRequestEntity();
        pendingBooking.setId(10);
        pendingBooking.setAdSpace(availableAdSpace);
        pendingBooking.setAdvertiserName("Tester");
        pendingBooking.setAdvertiserEmail("tester@example.com");
        pendingBooking.setStartDate(LocalDate.now().plusDays(1));
        pendingBooking.setEndDate(pendingBooking.getStartDate().plusDays(7));
        pendingBooking.setStatus(BookingStatus.PENDING);
        pendingBooking.setTotalCost(new BigDecimal("700.00"));

        bookingUuid = pendingBooking.getUuid();

        approvedBooking = new BookingRequestEntity();
        approvedBooking.setId(11);
        approvedBooking.setAdSpace(availableAdSpace);
        approvedBooking.setAdvertiserName("Approved");
        approvedBooking.setAdvertiserEmail("approved@example.com");
        approvedBooking.setStartDate(LocalDate.now().plusDays(10));
        approvedBooking.setEndDate(approvedBooking.getStartDate().plusDays(7));
        approvedBooking.setStatus(BookingStatus.APPROVED);
        approvedBooking.setTotalCost(new BigDecimal("700.00"));
    }


    @Test
    void getAllBookingRequests_returnsMappedList() {
        when(bookingRequestRepository.findAll()).thenReturn(List.of(pendingBooking, approvedBooking));

        List<BookingRequestResponseDto> result = bookingRequestService.getAllBookingRequests();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(pendingBooking.getUuid(), result.get(0).getUuid());
        assertEquals(approvedBooking.getUuid(), result.get(1).getUuid());

        verify(bookingRequestRepository).findAll();
    }


    @Test
    void getBookingRequest_whenExists_returnsDto() {
        when(bookingRequestRepository.findByUuid(bookingUuid)).thenReturn(Optional.of(pendingBooking));

        BookingRequestResponseDto dto = bookingRequestService.getBookingRequest(bookingUuid);

        assertNotNull(dto);
        assertEquals(bookingUuid, dto.getUuid());
        assertEquals(BookingStatus.PENDING, dto.getStatus());
        assertEquals(pendingBooking.getAdvertiserName(), dto.getAdvertiserName());

        verify(bookingRequestRepository).findByUuid(bookingUuid);
    }

    @Test
    void getBookingRequest_whenNotExists_throwsResourceNotFound() {
        UUID random = UUID.randomUUID();
        when(bookingRequestRepository.findByUuid(random)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingRequestService.getBookingRequest(random)
        );

        assertEquals("Booking request with uuid %s not found".formatted(random), ex.getMessage());
        verify(bookingRequestRepository).findByUuid(random);
    }


    @Test
    void createBookingRequest_validRequest_savesAndReturnsDto() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(7);
        BookingRequestCreateDto dto = new BookingRequestCreateDto();
        dto.setAdSpaceUuid(adSpaceUuid);
        dto.setAdvertiserName("Integration Tester");
        dto.setAdvertiserEmail("integration@test.com");
        dto.setStartDate(start);
        dto.setEndDate(end);

        when(adSpaceRepository.findByUuid(adSpaceUuid)).thenReturn(Optional.of(availableAdSpace));
        when(bookingRequestRepository.existsAtLeastOneOverlappingDateRange(
                eq(availableAdSpace.getUuid()),
                eq(start),
                eq(end),
                eq(BookingStatus.APPROVED)
        )).thenReturn(false);

        when(bookingRequestRepository.save(any(BookingRequestEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BookingRequestResponseDto responseDto = bookingRequestService.createBookingRequest(dto);

        assertNotNull(responseDto);
        assertEquals("Integration Tester", responseDto.getAdvertiserName());
        assertEquals("integration@test.com", responseDto.getAdvertiserEmail());
        assertEquals(availableAdSpace.getUuid(), responseDto.getAdSpaceUuid());
        assertNotNull(responseDto.getTotalCost());
        assertTrue(responseDto.getTotalCost().compareTo(BigDecimal.ZERO) > 0);

        verify(adSpaceRepository).findByUuid(adSpaceUuid);
        verify(bookingRequestRepository).existsAtLeastOneOverlappingDateRange(
                availableAdSpace.getUuid(), start, end, BookingStatus.APPROVED);
        verify(bookingRequestRepository).save(any(BookingRequestEntity.class));
    }

    @Test
    void createBookingRequest_startDateNotInFuture_throwsInvalidEntityException() {
        BookingRequestCreateDto dto = new BookingRequestCreateDto();
        dto.setAdSpaceUuid(adSpaceUuid);
        dto.setAdvertiserName("Test");
        dto.setAdvertiserEmail("test@example.com");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(10));

        InvalidEntityException ex = assertThrows(
                InvalidEntityException.class,
                () -> bookingRequestService.createBookingRequest(dto)
        );

        assertEquals("Start date must be in the future", ex.getMessage());
        verifyNoInteractions(adSpaceRepository, bookingRequestRepository);
    }

    @Test
    void createBookingRequest_durationLessThan7Days_throwsInvalidEntityException() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(3);

        BookingRequestCreateDto dto = new BookingRequestCreateDto();
        dto.setAdSpaceUuid(adSpaceUuid);
        dto.setAdvertiserName("Test");
        dto.setAdvertiserEmail("test@example.com");
        dto.setStartDate(start);
        dto.setEndDate(end);

        InvalidEntityException ex = assertThrows(
                InvalidEntityException.class,
                () -> bookingRequestService.createBookingRequest(dto)
        );

        assertEquals("End date must be after start date. Minimum booking duration: 7 days", ex.getMessage());
        verifyNoInteractions(adSpaceRepository, bookingRequestRepository);
    }

    @Test
    void createBookingRequest_adSpaceNotFound_throwsResourceNotFound() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(7);

        BookingRequestCreateDto dto = new BookingRequestCreateDto();
        dto.setAdSpaceUuid(adSpaceUuid);
        dto.setAdvertiserName("Test");
        dto.setAdvertiserEmail("test@example.com");
        dto.setStartDate(start);
        dto.setEndDate(end);

        when(adSpaceRepository.findByUuid(adSpaceUuid)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingRequestService.createBookingRequest(dto)
        );

        assertEquals("Ad space with uuid %s not found".formatted(adSpaceUuid), ex.getMessage());
        verify(adSpaceRepository).findByUuid(adSpaceUuid);
        verifyNoInteractions(bookingRequestRepository);
    }

    @Test
    void createBookingRequest_adSpaceNotAvailable_throwsUnavailableResource() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(7);

        BookingRequestCreateDto dto = new BookingRequestCreateDto();
        dto.setAdSpaceUuid(adSpaceUuid);
        dto.setAdvertiserName("Test");
        dto.setAdvertiserEmail("test@example.com");
        dto.setStartDate(start);
        dto.setEndDate(end);

        when(adSpaceRepository.findByUuid(adSpaceUuid)).thenReturn(Optional.of(unavailableAdSpace));

        UnavailableResourceException ex = assertThrows(
                UnavailableResourceException.class,
                () -> bookingRequestService.createBookingRequest(dto)
        );

        assertEquals("Ad Space status must be AVAILABLE", ex.getMessage());
        verify(adSpaceRepository).findByUuid(adSpaceUuid);
        verifyNoInteractions(bookingRequestRepository);
    }

    @Test
    void createBookingRequest_overlappingApprovedExists_throwsResourcesConflict() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(7);

        BookingRequestCreateDto dto = new BookingRequestCreateDto();
        dto.setAdSpaceUuid(adSpaceUuid);
        dto.setAdvertiserName("Test");
        dto.setAdvertiserEmail("test@example.com");
        dto.setStartDate(start);
        dto.setEndDate(end);

        when(adSpaceRepository.findByUuid(adSpaceUuid)).thenReturn(Optional.of(availableAdSpace));
        when(bookingRequestRepository.existsAtLeastOneOverlappingDateRange(
                availableAdSpace.getUuid(), start, end, BookingStatus.APPROVED))
                .thenReturn(true);

        ResourcesConflictException ex = assertThrows(
                ResourcesConflictException.class,
                () -> bookingRequestService.createBookingRequest(dto)
        );

        assertEquals("Cannot create overlapping bookings for the same space (for approved bookings)", ex.getMessage());
        verify(adSpaceRepository).findByUuid(adSpaceUuid);
        verify(bookingRequestRepository).existsAtLeastOneOverlappingDateRange(
                availableAdSpace.getUuid(), start, end, BookingStatus.APPROVED);
        verify(bookingRequestRepository, never()).save(any());
    }


    @Test
    void approveBookingRequest_whenNotFound_throwsResourceNotFound() {
        UUID random = UUID.randomUUID();
        when(bookingRequestRepository.findByUuid(random)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingRequestService.approveBookingRequest(random)
        );

        assertEquals("Booking request with uuid %s not found".formatted(random), ex.getMessage());
        verify(bookingRequestRepository).findByUuid(random);
    }

    @Test
    void approveBookingRequest_whenNotPending_throwsResourcesConflict() {
        BookingRequestEntity entity = new BookingRequestEntity();
        entity.setStatus(BookingStatus.APPROVED);

        when(bookingRequestRepository.findByUuid(bookingUuid)).thenReturn(Optional.of(entity));

        ResourcesConflictException ex = assertThrows(
                ResourcesConflictException.class,
                () -> bookingRequestService.approveBookingRequest(bookingUuid)
        );

        assertEquals("Only pending bookings can be approved", ex.getMessage());
        verify(bookingRequestRepository).findByUuid(bookingUuid);
        verify(bookingRequestRepository, never()).save(any());
    }

    @Test
    void approveBookingRequest_whenOverlappingApprovedExists_throwsResourcesConflict() {
        when(bookingRequestRepository.findByUuid(bookingUuid)).thenReturn(Optional.of(pendingBooking));
        when(bookingRequestRepository.existsAtLeastOneOverlappingDateRange(
                pendingBooking.getAdSpace().getUuid(),
                pendingBooking.getStartDate(),
                pendingBooking.getEndDate(),
                BookingStatus.APPROVED
        )).thenReturn(true);

        ResourcesConflictException ex = assertThrows(
                ResourcesConflictException.class,
                () -> bookingRequestService.approveBookingRequest(bookingUuid)
        );

        assertEquals("Cannot create overlapping bookings for the same space (for approved bookings)", ex.getMessage());
        verify(bookingRequestRepository).findByUuid(bookingUuid);
        verify(bookingRequestRepository).existsAtLeastOneOverlappingDateRange(
                pendingBooking.getAdSpace().getUuid(),
                pendingBooking.getStartDate(),
                pendingBooking.getEndDate(),
                BookingStatus.APPROVED
        );
        verify(bookingRequestRepository, never()).save(any());
    }

    @Test
    void approveBookingRequest_validPending_updatesStatusToApproved() {
        when(bookingRequestRepository.findByUuid(bookingUuid)).thenReturn(Optional.of(pendingBooking));
        when(bookingRequestRepository.existsAtLeastOneOverlappingDateRange(
                pendingBooking.getAdSpace().getUuid(),
                pendingBooking.getStartDate(),
                pendingBooking.getEndDate(),
                BookingStatus.APPROVED
        )).thenReturn(false);
        when(bookingRequestRepository.save(any(BookingRequestEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BookingRequestResponseDto dto = bookingRequestService.approveBookingRequest(bookingUuid);

        assertEquals(BookingStatus.APPROVED, dto.getStatus());
        verify(bookingRequestRepository).findByUuid(bookingUuid);
        verify(bookingRequestRepository).existsAtLeastOneOverlappingDateRange(
                pendingBooking.getAdSpace().getUuid(),
                pendingBooking.getStartDate(),
                pendingBooking.getEndDate(),
                BookingStatus.APPROVED
        );
        verify(bookingRequestRepository).save(any(BookingRequestEntity.class));
    }


    @Test
    void rejectBookingRequest_whenNotFound_throwsResourceNotFound() {
        UUID random = UUID.randomUUID();
        when(bookingRequestRepository.findByUuid(random)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingRequestService.rejectBookingRequest(random)
        );

        assertEquals("Booking request with uuid %s not found".formatted(random), ex.getMessage());
        verify(bookingRequestRepository).findByUuid(random);
    }

    @Test
    void rejectBookingRequest_whenNotPending_throwsResourcesConflict() {
        BookingRequestEntity entity = new BookingRequestEntity();
        entity.setStatus(BookingStatus.APPROVED);

        when(bookingRequestRepository.findByUuid(bookingUuid)).thenReturn(Optional.of(entity));

        ResourcesConflictException ex = assertThrows(
                ResourcesConflictException.class,
                () -> bookingRequestService.rejectBookingRequest(bookingUuid)
        );

        assertEquals("Only pending bookings can be rejected", ex.getMessage());
        verify(bookingRequestRepository).findByUuid(bookingUuid);
        verify(bookingRequestRepository, never()).save(any());
    }

    @Test
    void rejectBookingRequest_validPending_updatesStatusToRejected() {
        pendingBooking.setStatus(BookingStatus.PENDING);
        when(bookingRequestRepository.findByUuid(bookingUuid)).thenReturn(Optional.of(pendingBooking));
        when(bookingRequestRepository.save(any(BookingRequestEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BookingRequestResponseDto dto = bookingRequestService.rejectBookingRequest(bookingUuid);

        assertEquals(BookingStatus.REJECTED, dto.getStatus());
        verify(bookingRequestRepository).findByUuid(bookingUuid);
        verify(bookingRequestRepository).save(any(BookingRequestEntity.class));
    }
}
