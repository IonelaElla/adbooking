package com.generatik.adbooking;


import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceType;
import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import com.generatik.adbooking.adspace.repositories.AdSpaceRepository;
import com.generatik.adbooking.bookingrequest.repositories.BookingRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AdSpaceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdSpaceRepository adSpaceRepository;

    @Autowired
    private BookingRequestRepository bookingRequestRepository;

    private AdSpaceEntity savedAdSpace1;
    private AdSpaceEntity savedAdSpace2;
    private AdSpaceEntity savedAdSpaceOtherStatus;

    @BeforeEach
    void setUp() {
        bookingRequestRepository.deleteAll();

        adSpaceRepository.deleteAll();

        AdSpaceEntity ad1 = new AdSpaceEntity();
        ad1.setName("Billboard Central");
        ad1.setType(AdSpaceType.BILLBOARD);
        ad1.setCity("Cluj-Napoca");
        ad1.setAddress("Bd. Eroilor 1");
        ad1.setPricePerDay(new BigDecimal("150.00"));
        ad1.setAvailabilityStatus(AdSpaceAvailabilityStatus.AVAILABLE);
        savedAdSpace1 = adSpaceRepository.save(ad1);

        AdSpaceEntity ad2 = new AdSpaceEntity();
        ad2.setName("Mall Display Unirii");
        ad2.setType(AdSpaceType.MALL_DISPLAY);
        ad2.setCity("Bucuresti");
        ad2.setAddress("Piata Unirii 10");
        ad2.setPricePerDay(new BigDecimal("200.00"));
        ad2.setAvailabilityStatus(AdSpaceAvailabilityStatus.AVAILABLE);
        savedAdSpace2 = adSpaceRepository.save(ad2);

        AdSpaceEntity ad3 = new AdSpaceEntity();
        ad3.setName("Booked Board");
        ad3.setType(AdSpaceType.BILLBOARD);
        ad3.setCity("Cluj-Napoca");
        ad3.setAddress("Str. Memorandumului 5");
        ad3.setPricePerDay(new BigDecimal("300.00"));
        ad3.setAvailabilityStatus(AdSpaceAvailabilityStatus.BOOKED);
        savedAdSpaceOtherStatus = adSpaceRepository.save(ad3);
    }

    @Test
    void getAvailableSpaces_withoutFilters_returnsOnlyAvailable() throws Exception {
        mockMvc.perform(get("/api/v1/ad-spaces")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].availabilityStatus", everyItem(is("AVAILABLE"))));
    }

    @Test
    void getAvailableSpaces_withTypeAndCityFilters_returnsFilteredList() throws Exception {
        mockMvc.perform(get("/api/v1/ad-spaces")
                        .param("type", AdSpaceType.BILLBOARD.name())
                        .param("city", "Cluj-Napoca")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(savedAdSpace1.getUuid().toString()))
                .andExpect(jsonPath("$[0].name").value("Billboard Central"))
                .andExpect(jsonPath("$[0].city").value("Cluj-Napoca"))
                .andExpect(jsonPath("$[0].type").value(AdSpaceType.BILLBOARD.name()))
                .andExpect(jsonPath("$[0].availabilityStatus").value("AVAILABLE"))
                .andExpect(jsonPath("$[0].pricePerDay").value(150.00));
    }

    @Test
    void getAdSpace_existingUuid_returns200AndAdSpace() throws Exception {
        mockMvc.perform(get("/api/v1/ad-spaces/{uuid}", savedAdSpace1.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(savedAdSpace1.getUuid().toString()))
                .andExpect(jsonPath("$.name").value("Billboard Central"))
                .andExpect(jsonPath("$.city").value("Cluj-Napoca"))
                .andExpect(jsonPath("$.address").value("Bd. Eroilor 1"))
                .andExpect(jsonPath("$.type").value(AdSpaceType.BILLBOARD.name()))
                .andExpect(jsonPath("$.availabilityStatus").value("AVAILABLE"))
                .andExpect(jsonPath("$.pricePerDay").value(150.00));
    }

    @Test
    void getAdSpace_nonExistingUuid_returns404() throws Exception {
        UUID randomUuid = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/ad-spaces/{uuid}", randomUuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}
