package com.untripical.dto.guideDetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuideDetailsUpdateResponseWithTokenDTO {
    private GuideDetailsResponseDTO guideDetails;
    private String token;
}
