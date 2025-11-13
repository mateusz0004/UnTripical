package com.untripical.dto.region;

import com.untripical.enums.RegionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class RegionRequestDTO {

    @NotNull(message = "type must not be null")
    RegionType type;

    @NotBlank(message = "closestBigCity must not be blank")
    String closestBigCity;
}
