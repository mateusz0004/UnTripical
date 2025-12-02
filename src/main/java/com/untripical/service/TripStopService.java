package com.untripical.service;

import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import com.untripical.dto.tripStop.TripStopUpdateRequestDTO;
import com.untripical.exception.place.PlaceDoesNotExist;
import com.untripical.exception.tripPlan.TripPlanDoesNotExist;
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
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
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

    @Autowired
    UserService userService;



    public TripStopResponseDTO addTripStop(TripStopRequestDTO dto) {
        Long actualUserId = userService.getCurrentUser().getId();


        TripPlan tripPlan = tripPlanRepository.findByIdAndUser_Id(dto.getTripPlanId(), actualUserId)
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
                String tempOriginAddress = origin.getPlace().getAddressStreet() + " " + origin.getPlace().getAddressNumber() + ", "
                        + origin.getPlace().getPostalCode() + " " + origin.getPlace().getCity();
                String tempDestinationAddress = destination.getPlace().getAddressStreet() + " " + destination.getPlace().getAddressNumber()
                        + ", " + destination.getPlace().getPostalCode() + " "  + destination.getPlace().getCity();
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
        double totalDistance = tripPlan.getTripStops()
                .stream()
                .mapToDouble(TripStop::getDistanceToNext)
                .sum();

        tripPlan.setTotalDistance(totalDistance);
        tripPlanRepository.save(tripPlan);
        return tripStopMapper.toResponse(saved);
    }


    public void deleteTripStop(Long orderIndex, Long tripPlanId) throws Exception {
        Long userId = userService.getCurrentUser().getId();

        TripStop toDelete = tripStopRepository
                .findByOrderIndexAndTripPlan_User_IdAndTripPlan_Id(orderIndex, userId, tripPlanId)
                .orElseThrow(() -> new TripStopDoesNotExist("This trip stop does not exist for this user"));

        TripPlan tripPlan = tripPlanRepository.findByIdAndUser_Id(tripPlanId, userId)
                .orElseThrow(() -> new TripPlanDoesNotExist("This trip plan does not exist"));

        int stopsBeforeDelete = tripPlan.getTripStops().size();
        tripPlan.getTripStops().remove(toDelete);
        tripStopRepository.delete(toDelete);

        List<TripStop> stops = tripStopRepository
                .findAllByTripPlan_IdAndTripPlan_User_Id(tripPlanId, userId)
                .stream()
                .sorted(Comparator.comparingInt(TripStop::getOrderIndex))
                .collect(Collectors.toList());

        int newIndex = 1;
        for (TripStop ts : stops) {
            ts.setOrderIndex(newIndex++);
        }

        int deletedIndex = orderIndex.intValue();

        if (deletedIndex > 1 && deletedIndex < stopsBeforeDelete) {
            TripStop origin = stops.get(deletedIndex - 2);
            TripStop destination = stops.get(deletedIndex - 1);

            String originAddress = origin.getPlace().getAddressStreet() + " " +
                    origin.getPlace().getAddressNumber() + ", " +
                    destination.getPlace().getPostalCode() + " " +
                    origin.getPlace().getCity();

            String destinationAddress = destination.getPlace().getAddressStreet() + " " +
                    destination.getPlace().getAddressNumber() + ", " +
                    destination.getPlace().getPostalCode() + " " +
                    destination.getPlace().getCity();

            double distance = distanceService.getDistanceInKm(originAddress, destinationAddress);
            origin.setDistanceToNext(distance);
        }

        tripStopRepository.saveAll(stops);
        double totalDistance = tripPlan.getTripStops()
                .stream()
                .mapToDouble(TripStop::getDistanceToNext)
                .sum();

        tripPlan.setTotalDistance(totalDistance);
        tripPlanRepository.save(tripPlan);
        tripPlanRepository.save(tripPlan);
    }


    public TripStopResponseDTO updateTripStop(TripStopUpdateRequestDTO dto, Long orderIndex, Long tripPlanId){
        Long actualUserId = userService.getCurrentUser().getId();
        TripStop tripStop = tripStopRepository.findByOrderIndexAndTripPlan_User_IdAndTripPlan_Id(orderIndex, actualUserId, tripPlanId)
                .orElseThrow(()-> new TripStopDoesNotExist("This trip stop does not exist"));

        if(dto.getDescription()!=null){
            tripStop.setDescription(dto.getDescription());
        }
        TripStop saved = tripStopRepository.save(tripStop);

        return tripStopMapper.toResponse(saved);
    }

    public List<TripStop> findAllTripStopsWithPlanIdEntity(Long tripPlanId, Long userId){
        return tripStopRepository.findAllByTripPlan_IdAndTripPlan_User_Id(tripPlanId, userId);
    }

    public List<TripStopResponseDTO> findAllTripStopsWithPlanId(Long tripPlanId){
        Long userId = userService.getCurrentUser().getId();
        return findAllTripStopsWithPlanIdEntity(tripPlanId, userId)
                .stream()
                .map(tripStopMapper::toResponse)
                .collect(Collectors.toList());
    }
}
