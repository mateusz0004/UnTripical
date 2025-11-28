package com.untripical.service;

import com.untripical.dto.tripPlan.TripPlanRequestDTO;
import com.untripical.dto.tripPlan.TripPlanResponseDTO;
import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.exception.tripPlan.TripPlanAlreadyExists;
import com.untripical.exception.tripPlan.TripPlanDoesNotExist;
import com.untripical.exception.user.UserDoesNotExist;
import com.untripical.mapper.tripPlan.TripPlanMapper;
import com.untripical.model.TripPlan;
import com.untripical.model.User;
import com.untripical.repository.TripPlanRepository;
import com.untripical.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TripPlanService {

    @Autowired
    TripStopService tripStopService;

    @Autowired
    TripPlanMapper tripPlanMapper;

    @Autowired
    TripPlanRepository tripPlanRepository;

    @Autowired
    UserRepository userRepository;


    public TripPlanResponseDTO addTripPlan(TripPlanRequestDTO dto){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserDoesNotExist("User with username: " + username + " does not exist"));

        boolean exists = tripPlanRepository.findByNameAndIsActiveTrueAndUser_Username(dto.getName(), username)
                .map(TripPlan::getIsActive)
                .isPresent();

        if(exists){
            throw new TripPlanAlreadyExists("Plan " + dto.getName() + " already exists");
        }


        TripPlan plan = tripPlanMapper.toEntity(dto, currentUser);
        plan.setUser(currentUser);

        TripPlan saved = tripPlanRepository.save(plan);

        return tripPlanMapper.toResponse(saved);
    }

    public TripPlanResponseDTO getTripPlanByName(String planName){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        TripPlan tripPlan = tripPlanRepository.findByNameAndUser_Username(planName, username)
                .orElseThrow(() -> new TripPlanDoesNotExist("Trip plan with name: " + planName + " does not exist"));


        if(!checkIsActive(tripPlan)){
            throw new TripPlanDoesNotExist("Trip plan " + planName + "does not exist or is inactive");
        }

        return tripPlanMapper.toResponse(tripPlan);
    }

    public List<TripPlanResponseDTO> getAllTripPlans(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<TripPlan> allTripPlans = tripPlanRepository.findByUser_Username(username)
                .orElseThrow(() -> new TripPlanDoesNotExist("You don't have any plans"));

        return allTripPlans.stream().filter(this::checkIsActive).map(tripPlanMapper::toResponse).collect(Collectors.toList());
    }

    public void deleteTripPlan(String name){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        TripPlan tripPlan = tripPlanRepository.findByNameAndIsActiveTrueAndUser_Username(name, username)
                .orElseThrow(() -> new TripPlanDoesNotExist("Trip plan " + name + "does not exist"));

       tripPlan.setIsActive(false);
       tripPlanRepository.save(tripPlan);

    }

    public boolean checkIsActive(TripPlan tripPlan){return tripPlan.getIsActive();}


    //TODO: DO NAPRAWY
    public List<TripPlanResponseDTO> getTripPlanByDate (Date date){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TripPlan> tripPlansByDate = tripPlanRepository.findByAssignedAtAndUser_Username(date, username)
                .orElseThrow(() -> new TripPlanDoesNotExist("Trip plan with this Date " + date + " does not exist"));

        return tripPlansByDate.stream().filter(this::checkIsActive).map(tripPlanMapper::toResponse).collect(Collectors.toList());
    }

}
