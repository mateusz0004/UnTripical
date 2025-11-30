package com.untripical.service;

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

        Place place = placeRepository.findById(dto.getPlaceId())
                .orElseThrow(() -> new PlaceDoesNotExist("Place with ID " + dto.getPlaceId() + " does not exist"));
        int amountOfTripStopsInTripPlan = tripPlan.getTripStops().size();

        TripStop newTripStop = new TripStop();
        newTripStop.setDescription(dto.getDescription());
        newTripStop.setOrderIndex(amountOfTripStopsInTripPlan + 1);
        newTripStop.setPlace(place);
        newTripStop.setTripPlan(tripPlan);

        tripPlan.addTripStop(newTripStop);


        if(amountOfTripStopsInTripPlan>=1){
            try {
                TripStop origin = tripPlan.getTripStops().get(amountOfTripStopsInTripPlan-1);
                TripStop destination = tripPlan.getTripStops().get(amountOfTripStopsInTripPlan);
                String tempOriginAddress = origin.getPlace().getAddressStreet() + " " + origin.getPlace().getAddressNumber() + ", " + origin.getPlace().getCity();
                String tempDestinationAddress = destination.getPlace().getAddressStreet() + " " + destination.getPlace().getAddressNumber() + ", " + destination.getPlace().getCity();
                double distance = distanceService.getDistanceInKm(tempOriginAddress, tempDestinationAddress);
                origin.setDistanceToNext(distance);
                destination.setDistanceToNext(0.0);
                tripStopRepository.save(origin);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            newTripStop.setDistanceToNext(0.0);
        }

        TripStop saved = tripStopRepository.save(newTripStop);
        return tripStopMapper.toResponse(saved);
    }
    /// posprawdzać czy nie powinienem najpierw tego poustawiać i czy nie pobieram z bazy czegos a ona jest pusta
}
