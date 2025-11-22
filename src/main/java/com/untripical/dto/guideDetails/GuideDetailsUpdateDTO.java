package com.untripical.dto.guideDetails;

import com.untripical.enums.ExperienceLevel;
import com.untripical.enums.Specialisation;
import com.untripical.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideDetailsUpdateDTO {

    private String phoneNumber;

    private Specialisation specialisation;

    private String closestBigCity;

    private ExperienceLevel experienceLevel;

    private int counterOfDidJourney;

    private Long regionId;
}
