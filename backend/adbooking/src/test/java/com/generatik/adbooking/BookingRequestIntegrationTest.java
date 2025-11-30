
package com.generatik.adbooking;

import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceType;
import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import com.generatik.adbooking.adspace.repositories.AdSpaceRepository;
import com.generatik.adbooking.bookingrequest.dto.BookingRequestCreateDto;
import com.generatik.adbooking.bookingrequest.dto.BookingRequestResponseDto;
import com.generatik.adbooking.bookingrequest.dto.enums.BookingStatus;
import com.generatik.adbooking.bookingrequest.entities.BookingRequestEntity;
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
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class BookingRequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRequestRepository bookingRequestRepository;

    @Autowired
    private AdSpaceRepository adSpaceRepository;

    private AdSpaceEntity savedAdSpace;

    @BeforeEach
    void setUp() {
        bookingRequestRepository.deleteAll();
        adSpaceRepository.deleteAll();

        AdSpaceEntity ad = new AdSpaceEntity();
        ad.setName("Billboard Central");
        ad.setType(AdSpaceType.BILLBOARD);
        ad.setCity("Cluj-Napoca");
        ad.setAddress("Bd. Eroilor 1");
        ad.setPricePerDay(new BigDecimal("100.00"));
        ad.setAvailabilityStatus(AdSpaceAvailabilityStatus.AVAILABLE);
        savedAdSpace = adSpaceRepository.save(ad);
    }

    private BookingRequestResponseDto createBookingViaApi() throws Exception {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(7);

        BookingRequestCreateDto dto = new BookingRequestCreateDto();
        dto.setAdSpaceUuid(savedAdSpace.getUuid());
        dto.setAdvertiserName("Test Advertiser");
        dto.setAdvertiserEmail("test@example.com");
        dto.setStartDate(start);
        dto.setEndDate(end);

        var result = mockMvc.perform(post("/api/v1/booking-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.adSpaceUuid").value(savedAdSpace.getUuid().toString()))
                .andExpect(jsonPath("$.advertiserName").value("Test Advertiser"))
                .andExpect(jsonPath("$.advertiserEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value(BookingStatus.PENDING.name()))
                .andExpect(jsonPath("$.totalCost").isNumber())
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookingRequestResponseDto.class
        );
    }

    @Test
    void saveBookingRequest_validRequest_returns201AndCreates() throws Exception {
        long beforeCount = bookingRequestRepository.count();

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(7);

        BookingRequestCreateDto dto = new BookingRequestCreateDto();
        dto.setAdSpaceUuid(savedAdSpace.getUuid());
        dto.setAdvertiserName("Integration Tester");
        dto.setAdvertiserEmail("integration@test.com");
        dto.setStartDate(start);
        dto.setEndDate(end);

        mockMvc.perform(post("/api/v1/booking-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.adSpaceUuid").value(savedAdSpace.getUuid().toString()))
                .andExpect(jsonPath("$.advertiserName").value("Integration Tester"))
                .andExpect(jsonPath("$.advertiserEmail").value("integration@test.com"))
                .andExpect(jsonPath("$.status").value(BookingStatus.PENDING.name()))
                .andExpect(jsonPath("$.totalCost").isNumber());

        assertEquals(beforeCount + 1, bookingRequestRepository.count());
    }

    @Test
    void getBookingRequests_returns200AndList() throws Exception {
        createBookingViaApi();
        createBookingViaApi();

        mockMvc.perform(get("/api/v1/booking-requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status").value(BookingStatus.PENDING.name()))
                .andExpect(jsonPath("$[1].status").value(BookingStatus.PENDING.name()));
    }

    @Test
    void getBookingRequest_existingUuid_returns200AndBooking() throws Exception {
        BookingRequestResponseDto created = createBookingViaApi();

        mockMvc.perform(get("/api/v1/booking-requests/{uuid}", created.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(created.getUuid().toString()))
                .andExpect(jsonPath("$.adSpaceUuid").value(savedAdSpace.getUuid().toString()))
                .andExpect(jsonPath("$.advertiserName").value("Test Advertiser"))
                .andExpect(jsonPath("$.advertiserEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value(BookingStatus.PENDING.name()))
                .andExpect(jsonPath("$.totalCost").isNumber());
    }

    @Test
    void getBookingRequest_nonExistingUuid_returns404() throws Exception {
        UUID randomUuid = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/booking-requests/{uuid}", randomUuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void approveBookingRequest_existingPending_returns200AndStatusApproved() throws Exception {
        BookingRequestResponseDto created = createBookingViaApi();

        mockMvc.perform(patch("/api/v1/booking-requests/{uuid}/approve", created.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(created.getUuid().toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.name()));

        BookingRequestEntity entity = bookingRequestRepository.findAll().get(0);
        assertEquals(BookingStatus.APPROVED, entity.getStatus());
    }

    @Test
    void rejectBookingRequest_existingPending_returns200AndStatusRejected() throws Exception {
        BookingRequestResponseDto created = createBookingViaApi();

        mockMvc.perform(patch("/api/v1/booking-requests/{uuid}/reject", created.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(created.getUuid().toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.REJECTED.name()));

        BookingRequestEntity entity = bookingRequestRepository.findAll().get(0);
        assertEquals(BookingStatus.REJECTED, entity.getStatus());
    }
}
