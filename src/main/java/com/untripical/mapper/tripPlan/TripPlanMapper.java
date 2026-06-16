package com.untripical.mapper.tripPlan;

import com.untripical.dto.tripPlan.TripPlanRequestDTO;
import com.untripical.dto.tripPlan.TripPlanResponseDTO;
import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import com.untripical.model.TripPlan;

import com.untripical.model.User;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Builder
public class TripPlanMapper {

    public TripPlanResponseDTO toResponse(TripPlan entity) {

        List<TripStopResponseDTO> stopsDto = null;

        if (entity.getTripStops() != null) {
            stopsDto = entity.getTripStops()
                    .stream()
                    .map(stop -> new TripStopResponseDTO(
                            stop.getId(),
                            stop.getDescription(),
                            stop.getOrderIndex(),
                            stop.getDistanceToNext(),
                            stop.getPlace().getId()))
                    .collect(Collectors.toList());
        }

        return new TripPlanResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getAssignedAt(),
                entity.getIsActive(),
                entity.getDayWhenTripPlanIsStarting(),
                stopsDto,
                entity.getUser().getId(),
                entity.getId(),
                entity.getTotalDistance()
        );


    }

    public TripPlan toEntity(TripPlanRequestDTO dto, User user) {

        return TripPlan.builder()
                .name(dto.getName())
                .assignedAt(new Date())
                .isActive(true)
                .user(user)
                .dayWhenTripPlanIsStarting(dto.getDayWhenTripPlanIsStarting())
                .tripStops(new ArrayList<>())
                .build();
    }
}






