package com.generatik.adbooking.bookingrequest.controller;

import com.generatik.adbooking.bookingrequest.dto.BookingRequestCreateDto;
import com.generatik.adbooking.bookingrequest.dto.BookingRequestResponseDto;
import com.generatik.adbooking.bookingrequest.services.BookingRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/booking-requests")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingRequestController {

    private final BookingRequestService bookingRequestService;

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BookingRequestResponseDto> saveBookingRequest(@Valid @RequestBody BookingRequestCreateDto bookingRequestCreateDto) {
        BookingRequestResponseDto bookingRequestResponseDto = this.bookingRequestService.createBookingRequest(bookingRequestCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingRequestResponseDto);
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<BookingRequestResponseDto>> getBookingRequests() {
        return ResponseEntity.ok(bookingRequestService.getAllBookingRequests());

    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BookingRequestResponseDto> getBookingRequest(@PathVariable UUID uuid) {
        return ResponseEntity.ok(bookingRequestService.getBookingRequest(uuid));

    }

    @PatchMapping(path = "/{uuid}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BookingRequestResponseDto> approveBookingRequest(@PathVariable("uuid") UUID uuid) {

        BookingRequestResponseDto bookingRequestResponseDto = this.bookingRequestService.approveBookingRequest(uuid);
        return ResponseEntity.status(HttpStatus.OK).body(bookingRequestResponseDto);

    }

    @PatchMapping(path = "/{uuid}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BookingRequestResponseDto> rejectBookingRequest(@PathVariable("uuid") UUID uuid) {

        BookingRequestResponseDto bookingRequestResponseDto = this.bookingRequestService.rejectBookingRequest(uuid);
        return ResponseEntity.status(HttpStatus.OK).body(bookingRequestResponseDto);

    }


}
