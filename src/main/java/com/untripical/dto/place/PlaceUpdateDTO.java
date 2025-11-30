package com.untripical.dto.place;

import lombok.Data;

@Data
public class PlaceUpdateDTO {
    private String name;
    private String city;
    private String addressStreet;
    private String addressNumber;
    private String photoUrl;
    private Long regionId;
}
