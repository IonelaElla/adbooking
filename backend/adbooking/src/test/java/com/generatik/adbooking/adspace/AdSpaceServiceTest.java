package com.generatik.adbooking.adspace;

import com.generatik.adbooking.adspace.dto.AdSpaceResponseDto;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceType;
import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import com.generatik.adbooking.adspace.exceptions.ResourceNotFoundException;
import com.generatik.adbooking.adspace.repositories.AdSpaceRepository;
import com.generatik.adbooking.adspace.services.AdSpaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdSpaceServiceTest {

    @Mock
    private AdSpaceRepository adSpaceRepository;

    @InjectMocks
    private AdSpaceService adSpaceService;

    private AdSpaceEntity adSpace1;
    private AdSpaceEntity adSpace2;

    private final AdSpaceAvailabilityStatus availabilityStatus = AdSpaceAvailabilityStatus.AVAILABLE;
    private final AdSpaceType type = AdSpaceType.BILLBOARD;
    private final String city = "Cluj-Napoca";

    @BeforeEach
    void setUp() {
        adSpace1 = new AdSpaceEntity();
        adSpace1.setId(1);
        adSpace1.setName("Billboard Central");
        adSpace1.setType(type);
        adSpace1.setCity(city);
        adSpace1.setAddress("Bd. Eroilor 1");
        adSpace1.setPricePerDay(new BigDecimal("150.00"));
        adSpace1.setAvailabilityStatus(availabilityStatus);

        adSpace2 = new AdSpaceEntity();
        adSpace2.setId(2);
        adSpace2.setName("Billboard Gara");
        adSpace2.setType(type);
        adSpace2.setCity(city);
        adSpace2.setAddress("Str. Horea 10");
        adSpace2.setPricePerDay(new BigDecimal("200.00"));
        adSpace2.setAvailabilityStatus(availabilityStatus);
    }

    @Test
    void getAdSpacesBy_whenAdSpacesExist_returnsDtoList() {
        List<AdSpaceEntity> entities = List.of(adSpace1, adSpace2);

        when(adSpaceRepository.getAdSpacesBy(availabilityStatus, type, city))
                .thenReturn(entities);

        List<AdSpaceResponseDto> result =
                adSpaceService.getAdSpacesBy(availabilityStatus, type, city);

        assertNotNull(result);
        assertEquals(2, result.size());

        AdSpaceResponseDto first = result.get(0);
        assertEquals(city, first.getCity());
        assertEquals(type, first.getType());
        assertEquals(availabilityStatus, first.getAvailabilityStatus());
        assertEquals("Billboard Central", first.getName());

        verify(adSpaceRepository).getAdSpacesBy(availabilityStatus, type, city);
        verifyNoMoreInteractions(adSpaceRepository);
    }

    @Test
    void getAdSpace_whenAdSpaceExists_returnsDto() {
        UUID uuid = adSpace1.getUuid();

        when(adSpaceRepository.findByUuid(uuid))
                .thenReturn(Optional.of(adSpace1));

        AdSpaceResponseDto result = adSpaceService.getAdSpace(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals("Billboard Central", result.getName());
        assertEquals(city, result.getCity());
        assertEquals("Bd. Eroilor 1", result.getAddress());
        assertEquals(type, result.getType());
        assertEquals(availabilityStatus, result.getAvailabilityStatus());
        assertEquals(new BigDecimal("150.00"), result.getPricePerDay());

        verify(adSpaceRepository).findByUuid(uuid);
        verifyNoMoreInteractions(adSpaceRepository);
    }

    @Test
    void getAdSpace_whenAdSpaceDoesNotExist_throwsResourceNotFoundException() {
        UUID uuid = UUID.randomUUID();

        when(adSpaceRepository.findByUuid(uuid))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> adSpaceService.getAdSpace(uuid)
        );

        assertEquals(
                String.format("Ad space with uuid %s not found", uuid),
                ex.getMessage()
        );

        verify(adSpaceRepository).findByUuid(uuid);
        verifyNoMoreInteractions(adSpaceRepository);
    }
}
