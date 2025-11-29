package com.untripical.dto.place;

import lombok.Data;

@Data
public class PlaceUpdateDTO {
    private String name;
    private String city;
    private String photoUrl;
    private Long regionId;
}
