package com.generatik.adbooking.bookingrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestCreateDto {

    @NotNull(message = "adSpaceUuid cannot be empty.")
    private UUID adSpaceUuid;

    @NotBlank(message = "advertiserName cannot be empty.")
    private String advertiserName;

    @NotBlank(message = "advertiserEmail cannot be empty.")
    private String advertiserEmail;

    @NotNull(message = "startDate cannot be empty.")
    private LocalDate startDate;

    @NotNull(message = "endDate cannot be empty.")
    private LocalDate endDate;
}
