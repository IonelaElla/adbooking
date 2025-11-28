package com.generatik.adbooking.adspace.controller;

import com.generatik.adbooking.adspace.dto.AdSpaceResponseDto;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceType;
import com.generatik.adbooking.adspace.services.AdSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ad-spaces")
@CrossOrigin(origins = "http://localhost:3000")
public class AdSpaceController {
    private final AdSpaceService adSpaceService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<AdSpaceResponseDto>> getAvailableSpaces(@RequestParam(value = "type", required = false) AdSpaceType type, @RequestParam(value = "city", required = false) String city) {

        return ResponseEntity.ok(adSpaceService.getAdSpacesBy(AdSpaceAvailabilityStatus.AVAILABLE, type, city));

    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AdSpaceResponseDto> getAdSpace(@PathVariable UUID uuid) {
        return ResponseEntity.ok(adSpaceService.getAdSpace(uuid));

    }

}
