package com.untripical.untripical.dto.guideDetails;

import com.untripical.untripical.enums.ExperienceLevel;
import com.untripical.untripical.enums.Specialisation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class GuideDetailsRequestDTO {
    @NotBlank(message = "Guide name must not be blank")
    private String guideName;
    @NotBlank(message = "Phone number must not be blank")
    private String phoneNumber;
    @NotNull(message = "Specialisation must not be null")
    private Specialisation specialisation;
    @NotBlank(message = "Closest big city must not be blank")
    private String closestBigCity;
    @NotNull(message = "Experience level must not be null")
    private ExperienceLevel experienceLevel;
    @Min(value = 0, message = "Counter must be non-negative")
    private int counterOfDidJourney;
    @NotNull(message = "User ID must not be null")
    private Long userId;
    @NotNull(message = "Region ID must not be null")
    private Long regionId;
}
