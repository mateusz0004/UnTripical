package com.untripical.guideDetailsOrUser;

import com.untripical.dto.guideDetails.GuideDetailsUpdateDTO;
import com.untripical.dto.userDto.UserUpdateDTO;
import lombok.Value;

@Value
public class GuideDetailsOrUserUpdateDTO {
    private GuideDetailsUpdateDTO guideDetailsUpdateDTO;
    private UserUpdateDTO userUpdateDTO;
}
