package com.untripical.dto.place;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequestDTO {

    @NotBlank(message = "name must not be blank")
    String name;

    @NotBlank(message = "city must not be blank")
    String city;

    @NotBlank(message = "addressStreet must not be blank")
    String addressStreet;

    @NotBlank(message = "addressNumber must not be blank")
    String addressNumber;

    @NotBlank(message = "postalCode must not be blank")
    String postalCode;

    @NotBlank(message = "placeType must not be null")
    String placeType;

    @NotBlank(message = "photoUrl must not be blank")
    String photoUrl;

    @NotNull(message = "region_id must not be null")
    Long regionId;
}
