package com.untripical.dto.region;

import com.untripical.enums.RegionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequestDTO {

    @NotNull(message = "type must not be null")
    RegionType type;

    @NotBlank(message = "closestBigCity must not be blank")
    String closestBigCity;
}
