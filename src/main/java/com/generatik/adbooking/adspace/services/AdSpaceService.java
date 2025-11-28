package com.generatik.adbooking.adspace.services;

import com.generatik.adbooking.adspace.dto.AdSpaceResponseDto;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceType;
import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import com.generatik.adbooking.adspace.exceptions.ResourceNotFoundException;
import com.generatik.adbooking.adspace.mappers.AdSpaceMapper;
import com.generatik.adbooking.adspace.repositories.AdSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdSpaceService {

    private final AdSpaceRepository adSpaceRepository;


    public List<AdSpaceResponseDto> getAdSpacesBy(AdSpaceAvailabilityStatus availabilityStatus, AdSpaceType type, String city) {
        List<AdSpaceEntity> adSpaceEntities = this.adSpaceRepository.getAdSpacesBy(availabilityStatus, type, city);
        return adSpaceEntities.stream().map((AdSpaceMapper::toResponseDto)).collect(Collectors.toList());
    }

    public AdSpaceResponseDto getAdSpace(UUID uuid) {
        AdSpaceEntity adSpaceEntity = this.adSpaceRepository.findByUuid(uuid).orElse(null);
        if (adSpaceEntity == null) {
            throw new ResourceNotFoundException(String.format("Ad space with uuid %s not found", uuid));
        }
        return AdSpaceMapper.toResponseDto(adSpaceEntity);
    }
}
