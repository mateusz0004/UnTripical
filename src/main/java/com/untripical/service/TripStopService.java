package com.untripical.service;

import com.untripical.dto.region.RegionRequestDTO;
import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import com.untripical.exception.place.PlaceDoesNotExist;
import com.untripical.exception.tripPlan.TripPlanDoesNotExist;
import com.untripical.exception.tripStop.TripStopAlreadyExists;
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

//TODO: pola przystanków w tripPLanie mają null, dodaj obsługę Place, zabezpiecz, żeby nie można było dodawać tripStopa
//TODO: do planu, który nie istnieje, problem z EstimateHour, odkomentuj pola związane z Place
public class TripStopService {

    @Autowired
    private TripStopRepository tripStopRepository;

    @Autowired
    private TripPlanRepository tripPlanRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private TripStopMapper tripStopMapper;

    @Autowired
    private DistanceService distanceService;



    public TripStopResponseDTO addTripStop(TripStopRequestDTO dto) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        TripPlan tripPlan = tripPlanRepository.findByIdAndUser_Username(dto.getTripPlanId(), username)
                .orElseThrow(() -> new TripPlanDoesNotExist("Trip plan with ID " + dto.getTripPlanId() + " does not exist"));

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

        // newTripStop.setPlace(place);

        newTripStop.setTripPlan(tripPlan);
        tripPlan.addTripStop(newTripStop);

        if(currentMaxOrderIndex>1){
            try {
                TripStop origin = tripPlan.getTripStops().get(currentMaxOrderIndex-1);
                TripStop destination = tripPlan.getTripStops().get(currentMaxOrderIndex);
                double distance = distanceService.getDistanceInKm(origin.getName(), destination.getName());
                origin.setDistanceToNext(distance);
                tripStopRepository.save(origin);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            newTripStop.setDistanceToNext(0d);
        }
        ////////////////napisałem tą kalkulacje miedzy tripstopami

        TripStop saved = tripStopRepository.save(newTripStop);
        return tripStopMapper.toResponse(saved);
    }
}
