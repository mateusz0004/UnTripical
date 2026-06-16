package com.untripical.dto.guideDetails;

import com.untripical.enums.ExperienceLevel;
import com.untripical.enums.Specialisation;
import com.untripical.enums.UserRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class GuideDetailsRequestDTO {
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

    @NotNull(message = "Region ID must not be null")
    private Long regionId;
}
