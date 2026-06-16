package com.untripical.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class AnnouncementDateRequestDTO {

    @NotNull(message = "Date must not be null")
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate date;

}
