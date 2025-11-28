package com.generatik.adbooking.bookingrequest.mappers;

import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import com.generatik.adbooking.bookingrequest.dto.BookingRequestCreateDto;
import com.generatik.adbooking.bookingrequest.dto.BookingRequestResponseDto;
import com.generatik.adbooking.bookingrequest.dto.enums.BookingStatus;
import com.generatik.adbooking.bookingrequest.entities.BookingRequestEntity;

public class BookingRequestMapper {

    public static BookingRequestEntity toEntity(BookingRequestCreateDto dto, AdSpaceEntity adSpaceEntity) {
        BookingRequestEntity entity = new BookingRequestEntity();

        entity.setStatus(BookingStatus.PENDING);
        entity.setAdSpace(adSpaceEntity);
        entity.setAdvertiserName(dto.getAdvertiserName());
        entity.setAdvertiserEmail(dto.getAdvertiserEmail());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());

        return entity;
    }

    public static BookingRequestResponseDto toDto(BookingRequestEntity entity) {
        BookingRequestResponseDto dto = new BookingRequestResponseDto();

        dto.setUuid((entity.getUuid()));
        dto.setAdSpaceUuid(entity.getAdSpace().getUuid());
        dto.setAdSpaceName(entity.getAdSpace().getName());
        dto.setAdvertiserName(entity.getAdvertiserName());
        dto.setAdvertiserEmail(entity.getAdvertiserEmail());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus());
        dto.setTotalCost(entity.getTotalCost());

        return dto;
    }
}
