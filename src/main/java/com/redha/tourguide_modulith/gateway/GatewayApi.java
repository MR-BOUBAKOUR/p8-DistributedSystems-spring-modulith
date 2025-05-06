package com.redha.tourguide_modulith.gateway;

import com.redha.tourguide_modulith.location.dto.NearbyAttractionDTO;

import java.util.List;
import java.util.UUID;

public interface GatewayApi {

    List<NearbyAttractionDTO> getNearbyAttractionsWithRewards(UUID userId);

}
