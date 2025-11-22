package com.untripical.dto.userDto;

import com.untripical.dto.guideDetails.GuideDetailsRequestDTO;
import lombok.Value;

@Value
public class GuideDetailsOrUserRequest {
    private UserRegisterRequestDTO registerRequest;
    private GuideDetailsRequestDTO guideDetailsRequestDTO;
}
