package com.generatik.adbooking.adspace.mappers;

import com.generatik.adbooking.adspace.dto.AdSpaceResponseDto;
import com.generatik.adbooking.adspace.entities.AdSpaceEntity;

public class AdSpaceMapper {

    public static AdSpaceResponseDto toResponseDto(AdSpaceEntity entity) {
        AdSpaceResponseDto dto = new AdSpaceResponseDto();

        dto.setUuid(entity.getUuid());
        dto.setName(entity.getName());
        dto.setCity(entity.getCity());
        dto.setType(entity.getType());
        dto.setAddress(entity.getAddress());
        dto.setPricePerDay(entity.getPricePerDay());
        dto.setAvailabilityStatus(entity.getAvailabilityStatus());

        return dto;
    }
}
