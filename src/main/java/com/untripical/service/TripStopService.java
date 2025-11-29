package com.untripical.service;

import com.untripical.dto.region.RegionRequestDTO;
import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import com.untripical.exception.place.PlaceDoesNotExist;
import com.untripical.exception.tripPlan.TripPlanDoesNotExist;
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
        int amountOfTripStopsInTripPlan = tripPlan.getTripStops().size();

        TripStop newTripStop = new TripStop();
        newTripStop.setDescription(dto.getDescription());
        newTripStop.setEstimateHour(dto.getEstimateHour());
        if(tripPlan.getTripStops()==null){
            newTripStop.setOrderIndex(1);
        }else{
            newTripStop.setOrderIndex(amountOfTripStopsInTripPlan);
        }///// poprpawiłem ten orderIndex, bo podawany był z palca
        newTripStop.setDistanceToNext(dto.getDistanceToNext());

       // newTripStop.setPlace(place);
        newTripStop.setTripPlan(tripPlan);

        tripPlan.addTripStop(newTripStop);

        TripStop origin = tripPlan.getTripStops().get(amountOfTripStopsInTripPlan-2);
        TripStop destination = tripPlan.getTripStops().get(amountOfTripStopsInTripPlan-1);

        if(tripPlan.getTripStops().size()>1){
            try {
                double distance = distanceService.getDistanceInKm(origin.getPlace().getName(), destination.getPlace().getName());
                origin.setDistanceToNext(distance);
                tripStopRepository.save(origin);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }////////////////napisałem tą kalkulacje miedzy tripstopami

        TripStop saved = tripStopRepository.save(newTripStop);
        return tripStopMapper.toResponse(saved);
    }
}
