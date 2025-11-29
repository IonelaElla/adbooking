package com.generatik.adbooking.adspace.dto;


import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSpaceResponseDto {

    private UUID uuid;
    private String name;
    private AdSpaceType type;
    private String city;
    private String address;
    private BigDecimal pricePerDay;
    private AdSpaceAvailabilityStatus availabilityStatus;

}
