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

}
