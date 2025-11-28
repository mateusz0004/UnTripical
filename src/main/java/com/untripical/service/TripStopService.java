package com.untripical.service;

import com.untripical.dto.region.RegionRequestDTO;
import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import com.untripical.exception.place.PlaceDoesNotExist;
import com.untripical.exception.tripPlan.TripPlanDoesNotExist;
import com.untripical.exception.tripStop.TripStopAlreadyExists;
import com.untripical.exception.tripStop.TripStopDoesNotExist;
import com.untripical.mapper.tripStop.TripStopMapper;
import com.untripical.model.Place;
import com.untripical.model.TripPlan;
import com.untripical.model.TripStop;
import com.untripical.repository.PlaceRepository;
import com.untripical.repository.TripPlanRepository;
import com.untripical.repository.TripStopRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional

//TODO: dodaj obsługę Place, odkomentuj pola związane z Place

public class TripStopService {

    @Autowired
    private TripStopRepository tripStopRepository;

    @Autowired
    private TripPlanRepository tripPlanRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private TripStopMapper tripStopMapper;



    public TripStopResponseDTO addTripStop(TripStopRequestDTO dto) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        TripPlan tripPlan = tripPlanRepository.findByNameAndIsActiveTrueAndUser_Username(dto.getTripPlanName(), username)
                .orElseThrow(() -> new TripPlanDoesNotExist("Trip plan " + dto.getTripPlanName() + " does not exist"));

//        Place place = placeRepository.findById(dto.getPlaceId())
//                .orElseThrow(() -> new PlaceDoesNotExist("Place with ID " + dto.getPlaceId() + " does not exist"));

        int currentMaxOrderIndex = tripStopRepository.findMaxOrderIndexByTripPlanId(tripPlan.getId());
        int newMaxOrderIndex = currentMaxOrderIndex + 1;

        if(tripStopRepository.existsByNameAndTripPlan_Id(dto.getName(),tripPlan.getId())){
            throw new TripStopAlreadyExists("This trip stop already exists");
        }

        TripStop newTripStop = new TripStop();
        newTripStop.setName(dto.getName());
        newTripStop.setDescription(dto.getDescription());
        newTripStop.setEstimateHour(dto.getEstimateHour());
        newTripStop.setOrderIndex(newMaxOrderIndex);
        newTripStop.setDistanceToNext(dto.getDistanceToNext());
        newTripStop.setTripPlan(tripPlan);

       // newTripStop.setPlace(place);

        tripPlan.addTripStop(newTripStop);

        TripStop saved = tripStopRepository.save(newTripStop);

        return tripStopMapper.toResponse(saved);
    }

    public void removeTripStopByName(String name, String tripPlanName){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        TripPlan tripPlan = tripPlanRepository.findByNameAndUser_Username(tripPlanName, username)
                .orElseThrow(() -> new TripPlanDoesNotExist("Trip plan with name " + tripPlanName + " does not exist"));

        if(!tripPlan.getIsActive()){
            throw new TripPlanDoesNotExist("Trip plan is inactive");
        }

        TripStop tripStop = tripPlan.getTripStops().stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new TripStopDoesNotExist("TripStop " + name + "does not exist in this plan"));

        tripPlan.getTripStops().remove(tripStop);

        int i = 1;
        for (TripStop s : tripPlan.getTripStops()){
            s.setOrderIndex(i++);
        }

        tripPlanRepository.save(tripPlan);

    }
}
