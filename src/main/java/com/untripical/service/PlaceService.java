package com.untripical.service;

import com.untripical.dto.place.PlaceRequestDTO;
import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.dto.place.PlaceUpdateDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.enums.PlaceType;
import com.untripical.enums.RegionType;
import com.untripical.enums.VerificationStatus;
import com.untripical.exception.guideDetails.GuideDetailsDoesNotExist;
import com.untripical.exception.place.PlaceDoesNotExist;
import com.untripical.exception.place.PlaceWithNameIsExisting;
import com.untripical.exception.region.IncorrectRegionType;
import com.untripical.exception.region.RegionDoesNotExist;
import com.untripical.exception.review.ReviewDoesNotExist;
import com.untripical.exception.user.IncorrectRoleTypeException;
import com.untripical.mapper.place.PlaceMapper;
import com.untripical.model.GuideDetails;
import com.untripical.model.Place;
import com.untripical.model.Region;
import com.untripical.model.User;
import com.untripical.repository.GuideDetailsRepository;
import com.untripical.repository.PlaceRepository;
import com.untripical.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlaceService {
    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceMapper placeMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private GuideDetailsRepository guideDetailsRepository;

    @Autowired
    private ReviewService reviewService;
    
    public PlaceResponseDTO getPlaceById(Long id) {
        Place place = placeRepository.findById(id)
                .filter(existingPlace -> existingPlace.getStatus() == VerificationStatus.APPROVED)
                .filter(Place::getIsActive)
                .orElseThrow(() -> new PlaceDoesNotExist("This place does not exist"));
        return placeMapper.toResponse(place);
    }

    public List<PlaceResponseDTO> getAllByRegionType(RegionType type){
        return placeRepository.findAllByRegion_Type(type)
                .stream()
                .filter(existingPlace -> existingPlace.getStatus() == VerificationStatus.APPROVED)
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PlaceResponseDTO> getAllByClosestBigCity(String closestBigCity){
        return placeRepository.findAllByRegion_ClosestBigCity(closestBigCity)
                .stream()
                .filter(existingPlace -> existingPlace.getStatus() == VerificationStatus.APPROVED)
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PlaceResponseDTO addPlaceResponse(PlaceRequestDTO dto){
        Place place = placeRepository.findByName(dto.getName());
        if(place!=null&&place.getCity().equals(dto.getCity())
                &&place.getAddressStreet().equals(dto.getAddressStreet())
                &&place.getAddressNumber().equals(dto.getAddressNumber())){
            throw new PlaceWithNameIsExisting("Place with this name is existing");
        }

        User actualUser = userService.getCurrentUser();
        Region region = regionRepository.findById(dto.getRegionId())
                .orElseThrow(()-> new RegionDoesNotExist("This region does not exist"));
        Place entity = placeMapper.toEntity(dto);
        entity.setUser(actualUser);
        entity.setRegion(region);
        entity.setStatus(VerificationStatus.WAITING_FOR_APPROVAL);
        Place saved = placeRepository.save(entity);

        return placeMapper.toResponse(saved);
    }

    public PlaceResponseDTO updatePlace(PlaceUpdateDTO dto, Long placeId){
        Long actualUserId = userService.getCurrentUser().getId();
        Place place = placeRepository.findById(placeId)
                .orElseThrow(()-> new PlaceDoesNotExist("This place does not exist"));

        if(place.getUser().getId()!=actualUserId){
            throw new IncorrectRoleTypeException("You are not owner of this place");
        }

        if(dto.getRegionId()!=null){
            Region region = regionRepository.findById(dto.getRegionId())
                    .orElseThrow(()-> new RegionDoesNotExist("This region does not exist"));
            place.setRegion(region);
        }

        if(dto.getName()!=null){
            place.setName(dto.getName());
        }

        if(dto.getCity()!=null){
            place.setCity(dto.getCity());
        }

        if(dto.getPhotoUrl()!=null){
            place.setPhotoUrl(dto.getPhotoUrl());
        }
        if(dto.getAddressStreet()!=null){
            place.setAddressStreet(dto.getAddressStreet());
        }
        if(dto.getAddressNumber()!=null){
            place.setAddressNumber(dto.getAddressNumber());
        }
        if(dto.getPostalCode()!=null){
            place.setPostalCode(dto.getPostalCode());
        }
        if(dto.getPlaceType()!=null){
            place.setPlaceType(PlaceType.valueOf(dto.getPlaceType()));
        }

        Place saved = placeRepository.save(place);
        return placeMapper.toResponse(saved);
    }

    public void deletePlaceById (Long id){
        Place place = findPlaceEntityById(id);
        place.setIsActive(false);
    }

    private Place findPlaceEntityById(Long id) {
        return placeRepository.findById(id)
                .filter(existingPlace -> existingPlace.getStatus() == VerificationStatus.APPROVED)
                .orElseThrow(() -> new PlaceDoesNotExist("This place does not exist"));
    }

    public PlaceResponseDTO setVerificationStatusOfPlaceByGuide(Long id, VerificationStatus status){
        Long actualUserId = userService.getCurrentUser().getId();
        GuideDetails guideDetails = guideDetailsRepository.findById(actualUserId)
                .orElseThrow(()-> new GuideDetailsDoesNotExist("This guide does not exist"));
        Place place = placeRepository.findById(id)
                .orElseThrow(()-> new PlaceDoesNotExist("This place does not exist"));

        if(place.getRegion().getType()!=guideDetails.getRegion().getType()){
            throw new IncorrectRegionType("This guide is not having correct region type to change verification status of this place");
        }
        place.setStatus(status);
        Place saved = placeRepository.save(place);
        return placeMapper.toResponse(saved);
    }

    public List<PlaceResponseDTO> listOfPlaceWithStatusWaitingForApproval(){
        return placeRepository.findAllByStatus(VerificationStatus.WAITING_FOR_APPROVAL)
                .stream()
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PlaceResponseDTO> getPlaceWithAvgOpinionHigherThan(Long avgOpinion){
        return placeRepository.findAllByAvgRatingGreaterThan(avgOpinion)
                .stream()
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PlaceResponseDTO> getPlaceWithAmountOfOpinionsHigherThan(Long minReviews){
        List<Place> places = placeRepository.findPlacesWithMoreThanReviews(minReviews);
        return places.stream()
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PlaceResponseDTO> getPlacesAddedByUserAndWaitingForApproval(Long userId){
        return placeRepository.findAllByStatusAndUser_Id(VerificationStatus.WAITING_FOR_APPROVAL, userId)
                .stream()
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PlaceResponseDTO> getSimilarPlacesToPlaceWithId(Long placeId){
        Place place = placeRepository.findById(placeId)
                .orElseThrow(()-> new PlaceDoesNotExist("This place does not exist"));
        return placeRepository.findAllByPlaceTypeAndRegion_Id(place.getPlaceType(), place.getRegion().getId())
                .stream()
                .filter(p->!p.getId().equals(placeId))
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Double getAveragePlaceRating (Long placeId){
        Place place = placeRepository.findById(placeId)
                .orElseThrow(()-> new PlaceDoesNotExist("This place does not exist"));
        return place.getAvgRating();
    }

    public List<PlaceResponseDTO> getPlaceByPlaceType(PlaceType placeType){
        return placeRepository.findAllByPlaceType(placeType)
                .stream()
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PlaceResponseDTO> getPlaceByRegionAndAverageRatingHigherThan(Long regionId, double avgRating){
        Region region = regionRepository.findById(regionId)
                .orElseThrow(()-> new RegionDoesNotExist("This region does not exist"));
        return placeRepository.findAllByAvgRatingGreaterThanAndRegion(avgRating, region)
                .stream()
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PlaceResponseDTO> getPlaceByCityAndAverageRatingHigherThan(String nameOfCity, double avgRating){
        return placeRepository.findAllByAvgRatingGreaterThanAndCity(avgRating, nameOfCity)
                .stream()
                .filter(Place::getIsActive)
                .map(placeMapper::toResponse)
                .collect(Collectors.toList());
    }
}
