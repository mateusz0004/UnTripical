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
    private UserService userService;

    public TripStopResponseDTO addTripStop(TripStopRequestDTO dto) {
        Long actualUserId = userService.getCurrentUser().getId();

        TripPlan tripPlan = tripPlanRepository.findByIdAndUser_Id(dto.getTripPlanId(), actualUserId)
                .orElseThrow(() -> new TripPlanDoesNotExist("Trip plan with ID " + dto.getTripPlanId() + " does not exist"));

        Place place = placeRepository.findById(dto.getPlaceId())
                .orElseThrow(() -> new PlaceDoesNotExist("Place with ID " + dto.getPlaceId() + " does not exist"));

        List<TripStop> existingStops = tripStopRepository.findAllByTripPlan_IdAndTripPlan_User_Id(dto.getTripPlanId(), actualUserId)
                .stream()
                .sorted(Comparator.comparingInt(TripStop::getOrderIndex))
                .collect(Collectors.toList());

        int amountOfTripStopsInTripPlan = existingStops.size();

        if (amountOfTripStopsInTripPlan >= 0) {
            try {
                Place pOrigin;
                Place pDest = place;

                if (amountOfTripStopsInTripPlan > 0) {
                    TripStop previousStop = existingStops.stream()
                            .filter(ts -> ts.getOrderIndex() == amountOfTripStopsInTripPlan)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Could not find previous trip stop"));
                    pOrigin = previousStop.getPlace();
                } else {
                    pOrigin = place;
                }

                String streetO = pOrigin.getAddressStreet() != null ? pOrigin.getAddressStreet() : "";
                String cityO = pOrigin.getCity() != null ? pOrigin.getCity() : "";
                String zipO = pOrigin.getPostalCode() != null ? pOrigin.getPostalCode() : "";

                String streetD = pDest.getAddressStreet() != null ? pDest.getAddressStreet() : "";
                String cityD = pDest.getCity() != null ? pDest.getCity() : "";
                String zipD = pDest.getPostalCode() != null ? pDest.getPostalCode() : "";

                String tempOriginAddress = (streetO + " " + zipO + " " + cityO + ", Polska").replaceAll("\\s+", " ").trim();
                String tempDestinationAddress = (streetD + " " + zipD + " " + cityD + ", Polska").replaceAll("\\s+", " ").trim();

                double distance = distanceService.getDistanceInKm(tempOriginAddress, tempDestinationAddress);

                if (amountOfTripStopsInTripPlan > 0) {
                    TripStop previousStop = existingStops.get(amountOfTripStopsInTripPlan - 1);
                    previousStop.setDistanceToNext(distance);
                    tripStopRepository.save(previousStop);
                }

            } catch (Exception e) {
                if (amountOfTripStopsInTripPlan > 0) {
                    TripStop previousStop = existingStops.get(amountOfTripStopsInTripPlan - 1);
                    previousStop.setDistanceToNext(0.0);
                    tripStopRepository.save(previousStop);
                }
            }
        }

        TripStop newTripStop = new TripStop();
        newTripStop.setDescription(dto.getDescription());
        newTripStop.setOrderIndex(amountOfTripStopsInTripPlan + 1);
        newTripStop.setPlace(place);
        newTripStop.setTripPlan(tripPlan);
        newTripStop.setDistanceToNext(0.0);

        TripStop savedNewStop = tripStopRepository.save(newTripStop);
        tripPlan.getTripStops().add(savedNewStop);

        double totalDistance = tripPlan.getTripStops()
                .stream()
                .mapToDouble(TripStop::getDistanceToNext)
                .sum();

        tripPlan.setTotalDistance(totalDistance);
        tripPlanRepository.save(tripPlan);

        return tripStopMapper.toResponse(savedNewStop);
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

            Place pOrigin = origin.getPlace();
            Place pDest = destination.getPlace();

            String streetO = pOrigin.getAddressStreet() != null ? pOrigin.getAddressStreet() : "";
            String cityO = pOrigin.getCity() != null ? pOrigin.getCity() : "";
            String zipO = pOrigin.getPostalCode() != null ? pOrigin.getPostalCode() : "";

            String streetD = pDest.getAddressStreet() != null ? pDest.getAddressStreet() : "";
            String cityD = pDest.getCity() != null ? pDest.getCity() : "";
            String zipD = pDest.getPostalCode() != null ? pDest.getPostalCode() : "";

            String originAddress = (streetO + " " + zipO + " " + cityO + ", Polska").replaceAll("\\s+", " ").trim();
            String destinationAddress = (streetD + " " + zipD + " " + cityD + ", Polska").replaceAll("\\s+", " ").trim();

            try {
                double distance = distanceService.getDistanceInKm(originAddress, destinationAddress);
                origin.setDistanceToNext(distance);
            } catch (Exception e) {
                origin.setDistanceToNext(0.0);
            }
        }

        tripStopRepository.saveAll(stops);
        double totalDistance = tripPlan.getTripStops()
                .stream()
                .mapToDouble(TripStop::getDistanceToNext)
                .sum();

        tripPlan.setTotalDistance(totalDistance);
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