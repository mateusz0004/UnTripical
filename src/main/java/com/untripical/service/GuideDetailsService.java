package com.untripical.service;

import com.untripical.dto.guideDetails.GuideDetailsResponseDTO;
import com.untripical.dto.guideDetails.GuideDetailsUpdateResponseWithTokenDTO;
import com.untripical.dto.guideDetailsOrUser.GuideDetailsOrUserUpdateDTO;
import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.dto.review.guide.ReviewResponseGuideDetailsDTO;
import com.untripical.dto.userDto.GuideDetailsOrUserRequest;
import com.untripical.enums.Specialisation;
import com.untripical.enums.UserRole;
import com.untripical.exception.guideDetails.GuideDetailsDoesNotExist;
import com.untripical.exception.guideDetails.ListOfGuideDetailsDoesNotExist;
import com.untripical.exception.user.UserWithThisUsernameAlreadyExist;
import com.untripical.mapper.guideDetails.GuideDetailsMapper;
import com.untripical.mapper.review.ReviewMapper;
import com.untripical.mapper.user.UserMapper;
import com.untripical.model.GuideDetails;
import com.untripical.model.Region;
import com.untripical.model.Review;
import com.untripical.model.User;
import com.untripical.repository.GuideDetailsRepository;
import com.untripical.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GuideDetailsService {
    @Autowired
    private GuideDetailsRepository guideDetailsRepository;
    @Autowired
    private GuideDetailsMapper guideDetailsMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RegionService regionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTService jwt;
    @Autowired
    private PlaceService placeService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewMapper reviewMapper;


    public GuideDetailsResponseDTO getGuideDetailsById(Long id){
        GuideDetails guideDetails = guideDetailsRepository.findById(id)
                .orElseThrow(()-> new GuideDetailsDoesNotExist("This guide does not exist"));
        GuideDetailsResponseDTO dto = guideDetailsMapper.toResponse(guideDetails);
        List<ReviewResponseGuideDetailsDTO> activeReviews = guideDetails.getReviews().stream()
                .filter(Review::getIsActive)
                .map(reviewMapper::toGuideDetailsResponse)
                .collect(Collectors.toList());
        dto.setReviews(activeReviews);
        return dto;
    }

    public GuideDetailsResponseDTO getGuideDetailsByGuideName(String guideName){
        User currentUser = userRepository.findByUsername(guideName)
                .orElseThrow(()-> new GuideDetailsDoesNotExist("This guide does not exist"));
        if(!currentUser.getIsActive()){
            throw new GuideDetailsDoesNotExist("This guide does not exist");
        }
        return getGuideDetailsById(currentUser.getId());
    }

    public List<GuideDetailsResponseDTO> getGuideDetailsBySpecialisation(Specialisation specialisation){
        List<GuideDetails> guideDetailsList = guideDetailsRepository.findAllBySpecialisation(specialisation)
                .orElseThrow(()-> new GuideDetailsDoesNotExist("Guide with this specialisation does not exist"));
        return guideDetailsList.stream()
                .filter(g-> g.getUser().getIsActive())
                .map(guideDetailsMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<GuideDetailsResponseDTO> getAllGuideDetails(){
        return guideDetailsRepository.findAll()
                .stream()
                .filter(g-> g.getUser().getIsActive())
                .map(guideDetailsMapper::toResponse)
                .collect(Collectors.toList());
    }

    public GuideDetailsResponseDTO guideRegister (GuideDetailsOrUserRequest dto){
        User user = userService.registerForGuides(dto.getRegisterRequest(), UserRole.GUIDE);
        GuideDetails guideDetails = guideDetailsMapper.toEntity(dto.getGuideDetailsRequestDTO());
        Region region = regionService.findRegionById(dto.getGuideDetailsRequestDTO().getRegionId());
        guideDetails.setRegion(region);
        guideDetails.setUser(user);
        GuideDetails saved = guideDetailsRepository.save(guideDetails);
        return guideDetailsMapper.toResponse(saved);
    }

    public GuideDetailsUpdateResponseWithTokenDTO updateGuideDetails(GuideDetailsOrUserUpdateDTO dto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(!currentUser.getIsActive()){
            throw new GuideDetailsDoesNotExist("This guide does not exist");
        }
        Long currentGuideId = currentUser.getId();
        GuideDetails currentGuideDetails = guideDetailsRepository.findById(currentGuideId)
                .orElseThrow(() -> new GuideDetailsDoesNotExist("This guide does not exist"));

        if(dto.getUserUpdateDTO().getEmail() != null)
            currentUser.setEmail(dto.getUserUpdateDTO().getEmail());

        if(dto.getUserUpdateDTO().getUsername() != null) {
            if (userRepository.findByUsername(dto.getUserUpdateDTO().getUsername()).isPresent())
                throw new UserWithThisUsernameAlreadyExist("This username already exists");

            currentUser.setUsername(dto.getUserUpdateDTO().getUsername());
        }

        if(dto.getUserUpdateDTO().getPassword() != null)
            currentUser.setPassword(encoder.encode(dto.getUserUpdateDTO().getPassword()));

        if(dto.getGuideDetailsUpdateDTO().getClosestBigCity() != null)
            currentGuideDetails.setClosestBigCity(dto.getGuideDetailsUpdateDTO().getClosestBigCity());

        if(dto.getGuideDetailsUpdateDTO().getCounterOfDidJourney()>=0)
            currentGuideDetails.setCounterOfDidJourney(dto.getGuideDetailsUpdateDTO().getCounterOfDidJourney());

        if(dto.getGuideDetailsUpdateDTO().getRegionId()!= null) {
            Region region = regionService.findRegionById(dto.getGuideDetailsUpdateDTO().getRegionId());
            currentGuideDetails.setRegion(region);
        }

        if(dto.getGuideDetailsUpdateDTO().getPhoneNumber()!=null)
            currentGuideDetails.setPhoneNumber(dto.getGuideDetailsUpdateDTO().getPhoneNumber());

        userRepository.save(currentUser);
        guideDetailsRepository.save(currentGuideDetails);

        String newToken = jwt.generateToken(currentUser.getUsername());

        return GuideDetailsUpdateResponseWithTokenDTO.builder()
                .guideDetails(guideDetailsMapper.toResponse(currentGuideDetails))
                .token(newToken)
                .build();
    }


    public void deleteGuideDetails(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(!currentUser.getIsActive()){
            throw new GuideDetailsDoesNotExist("This guide doest not exist");
        }
        Long currentGuideId = currentUser.getId();
        GuideDetails currentGuideDetails = guideDetailsRepository.findById(currentGuideId)
                .orElseThrow(()-> new GuideDetailsDoesNotExist("This guide does not exist"));
        userService.deleteUser();
        guideDetailsRepository.save(currentGuideDetails);
    }

    public void deleteGuideDetailsWithIdByAdmin(Long id){
        GuideDetails guideDetails = guideDetailsRepository.findById(id)
                .orElseThrow(()->new GuideDetailsDoesNotExist("This guide does not exist"));
        if(!guideDetails.getUser().getIsActive()){
            throw new GuideDetailsDoesNotExist("This guide does not exist");
        }
        userService.deleteUserByAdmin(id);
    }
    public List<PlaceResponseDTO> listOfPlaceWithStatusWaitingForApproval (){
        return placeService.listOfPlaceWithStatusWaitingForApproval();
    }
    public List<GuideDetailsResponseDTO> getGuideDetailsByClosestBigCity (String closestBigCity){

        List<GuideDetails> guideDetailsList =  guideDetailsRepository.findAllByClosestBigCity(closestBigCity)
                .orElseThrow(() -> new ListOfGuideDetailsDoesNotExist("There is no guides in this city"));


        return guideDetailsList.stream()
                .map(guideDetailsMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Double averageGuideReview(Long guideId){
        List<ReviewResponseGuideDetailsDTO> listOfReviewsForGuide = reviewService.getAllGuideDetailsReviews(guideId);

        if(listOfReviewsForGuide.isEmpty()){
            return 0.0;
        }

        double sum = listOfReviewsForGuide.stream().mapToDouble(ReviewResponseGuideDetailsDTO::getNumberOfStars).sum();

        return sum / listOfReviewsForGuide.size();
    }
}