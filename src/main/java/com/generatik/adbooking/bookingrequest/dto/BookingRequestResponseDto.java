package com.generatik.adbooking.bookingrequest.dto;

import com.generatik.adbooking.bookingrequest.dto.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestResponseDto {

    private Long id;
    private Long adSpaceId;
    private String adSpaceName;
    private String advertiserName;
    private String advertiserEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
    private BigDecimal totalCost;
}