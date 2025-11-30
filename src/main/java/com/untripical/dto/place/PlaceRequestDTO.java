package com.untripical.dto.place;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class PlaceRequestDTO {

    @NotBlank(message = "name must not be blank")
    String name;

    @NotBlank(message = "city must not be blank")
    String city;

    @NotBlank(message = "addressStreet must not be blank")
    String addressStreet;

    @NotBlank(message = "addressNumber must not be blank")
    String addressNumber;

    @NotBlank(message = "photoUrl must not be blank")
    String photoUrl;

    @NotNull(message = "region_id must not be null")
    Long regionId;
}
