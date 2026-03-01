package com.ansh.authconnectionsexample.connectionpractice.service;

import com.ansh.authconnectionsexample.connectionpractice.dto.GigCreateRequest;
import com.ansh.authconnectionsexample.connectionpractice.dto.GigDto;
import com.ansh.authconnectionsexample.connectionpractice.dto.GigRequestDto;
import com.ansh.authconnectionsexample.connectionpractice.model.enums.GigStatus;
import com.ansh.authconnectionsexample.connectionpractice.model.enums.RequestStatus;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Gig;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.GigRequest;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import com.ansh.authconnectionsexample.connectionpractice.repository.GigRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.GigRequestRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.ReviewRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GigService {
    @Autowired
    private GigRepository gigRepository;

    @Autowired
    private GigRequestRepository gigRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ReviewRepository reviewRepository;

    private User getAuthenticatedUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Authenticated user could not be found with username:"+username));
    }

    @Transactional
    public GigDto createGig(GigCreateRequest createRequest){
        User user = getAuthenticatedUser();

        boolean hasActiveGig = gigRepository
                .findByGigMasterAndStatusIn(user, List.of(GigStatus.ACTIVE,GigStatus.FULL))
                .isPresent();
        if (hasActiveGig){
            throw new RuntimeException("User already has an active gig");
        }

        Gig savedGig = gigRepository.save(Gig.builder()
                .gigMaster(user)
                .sport(createRequest.getSport())
                .location(createRequest.getLocation())
                .dateTime(createRequest.getDateTime())
                .playersNeeded(createRequest.getPlayersNeeded())
                .status(GigStatus.ACTIVE)
                .build());

        chatService.addMemberToGigChat(savedGig,savedGig.getGigMaster());

        return mapToGigDto(savedGig);
    }

    @Transactional(readOnly = true)
    public GigDto getGigById(Long gigId){

        User user = getAuthenticatedUser();

        Gig gig = gigRepository.findById(gigId)
            .orElseThrow(()-> new RuntimeException("Gig not found with id: " + gigId));
            return mapToGigDto(gig,user);
    }

    @Transactional(readOnly = true)
    public Page<GigDto> getGigByGigMaster(Pageable pageable){
        User user = getAuthenticatedUser();

        Specification<Gig> spec = Specification.where(GigSpecificationService.hasGigMaster(user.getUsername()));

        Page<Gig> gigPage = gigRepository.findAll(spec,pageable);

        return gigPage.map(this::mapToGigDto);
    }

    @Transactional(readOnly = true)
    public Page<GigDto> getAllActiveGigs(String sport, String location,Pageable pageable){

        User user = getAuthenticatedUser();

        Specification<Gig> spec = Specification.where(GigSpecificationService.hasStatus(GigStatus.ACTIVE))
                .and(GigSpecificationService.notCreatedBy(user.getUsername()))
                .and(GigSpecificationService.userNotParticipant(user.getUsername()));

        if (sport != null && !sport.isEmpty()) {
            spec = spec.and(GigSpecificationService.hasSport(sport));
        }

        if (location != null && !location.isEmpty()){
            spec = spec.and(GigSpecificationService.hasLocation(location));
        }

        Page<Gig> gigPage = gigRepository.findAll(spec,pageable);

        return gigPage.map(this::mapToGigDto);
    }

    public Page<GigDto> getGigUserParticipatedIn(Pageable pageable){
        User user = getAuthenticatedUser();

        Specification<Gig> spec = Specification.where(GigSpecificationService.hasParticipant(user.getUsername()));

        Page<Gig> gigPage = gigRepository.findAll(spec,pageable);

        return gigPage.map(this::mapToGigDto);
    }

    public void requestToJoinGig(Long gigId){
        User requester = getAuthenticatedUser();
        Gig gig = gigRepository.findById(gigId)
                .orElseThrow(()->new RuntimeException("Gig not found"));
        if (gig.getStatus() != GigStatus.ACTIVE){
            throw new RuntimeException("This gig is not longer Active");
        }

        if (gig.getGigMaster().getUsername().equals(requester.getUsername())){
            throw  new RuntimeException("You cannot join your own gig");
        }

        boolean isAlreadyParticipant = gig.getAcceptedParticipants().stream().anyMatch(u-> u.getUsername().equals(requester.getUsername()));

        if (isAlreadyParticipant){
            throw new RuntimeException("Already a participant in this gig");
        }

        boolean hasPendingRequest = gigRequestRepository
                .existsByGigAndRequesterAndStatus(gig, requester, RequestStatus.PENDING);
        if (hasPendingRequest) {
            throw new RuntimeException("You already have a pending request for this gig");
        }

        GigRequest gigRequest = GigRequest.builder()
                .gig(gig)
                .requester(requester)
                .status(RequestStatus.PENDING)
                .build();
        gigRequestRepository.save(gigRequest);
    }

    @Transactional(readOnly = true)
    public Page<GigRequestDto> getGigRequestsForMyGig(Pageable pageable){
        User user = getAuthenticatedUser();
        Gig gig = gigRepository.findByGigMasterAndStatusIn(user,List.of(GigStatus.ACTIVE,GigStatus.FULL))
                .orElseThrow(()->new RuntimeException("User doest not have an active gig"));

        Page<GigRequest> requestPage = gigRequestRepository.findByGigAndStatus(gig,RequestStatus.PENDING,pageable);
        return requestPage.map(this::mapToGigRequestDto);
    }

    public void acceptGigRequest(Long requestId){
        User gigMaster = getAuthenticatedUser();
        GigRequest request = gigRequestRepository.findById(requestId)
                .orElseThrow(()-> new RuntimeException("Request was not found"));

        Gig gig = request.getGig();

        if (gig.getStatus() != GigStatus.ACTIVE) {
            throw new RuntimeException("Cannot accept requests for a non active gig");
        }

        if (!gig.getGigMaster().equals(gigMaster)){
            throw new RuntimeException("User is not authorized to accept this request");
        }


        gig.getAcceptedParticipants().add(request.getRequester());
        request.setStatus(RequestStatus.ACCEPTED);

        if (gig.getAcceptedParticipants().size() == gig.getPlayersNeeded()){
            gig.setStatus(GigStatus.FULL);
        }

        chatService.addMemberToGigChat(gig, request.getRequester());

        gigRequestRepository.save(request);
        gigRepository.save(gig);
    }

    public void rejectGigRequest(Long requestId){
        User gigMaster = getAuthenticatedUser();
        GigRequest request = gigRequestRepository.findById(requestId)
                .orElseThrow(()-> new RuntimeException("Request not found"));

        if (!request.getGig().getGigMaster().equals( gigMaster)){
            throw new RuntimeException("User is not authorized to reject this request");
        }

        request.setStatus(RequestStatus.REJECTED);
        gigRequestRepository.save(request);
    }

    public GigDto completeGig(Long gigId){
        User gigMaster = getAuthenticatedUser();
        Gig gig = gigRepository.findById(gigId)
                .orElseThrow(()-> new RuntimeException("Gig not found"));
        if (!gig.getGigMaster().equals(gigMaster)){
            throw new RuntimeException("User is not authorized to complete the gig");
        }

        if (gig.getStatus() != GigStatus.ACTIVE && gig.getStatus() != GigStatus.FULL) {
            throw new RuntimeException("Gig can only be completed if it is ACTIVE or FULL");
        }

        gig.setStatus(GigStatus.COMPLETED);
        Gig completeGig = gigRepository.save(gig);

        chatService.deleteChatGroupForGig(gig);
        return mapToGigDto(completeGig);
    }

    public void  deleteGig(Long gigId){
        User gigMaster = getAuthenticatedUser();
        Gig gig  = gigRepository.findById(gigId)
                .orElseThrow(()-> new RuntimeException("Gig not found"));
        if (!gig.getGigMaster().equals(gigMaster)){
            throw new RuntimeException("User is not authorized to delete the gig");
        }

        gigRequestRepository.deleteByGig(gig);
        reviewRepository.deleteByGig(gig);
        chatService.deleteChatGroupForGig(gig);
        gigRepository.delete(gig);
    }



    private GigDto mapToGigDto(Gig gig,User user){


        String requestStatus = "NONE";

        if (user != null){
               Optional<GigRequest> existingRequest = gigRequestRepository.findByGigAndRequester(gig,user);

            if (existingRequest.isPresent()){
                requestStatus = existingRequest.get().getStatus().name();
            }
        }


        boolean isOwner = user != null && gig.getGigMaster().getId().equals(user.getId());

        boolean isParticipant = user != null && gig.getAcceptedParticipants().stream().anyMatch(u-> u.getId().equals(user.getId()));

//        SOME LOGS :::::::
//        ____________________________________________________
        System.out.println(gig.getId());

        System.out.println(gig.getGigMaster().getUsername());

        System.out.println(user != null ? user.getUsername() : "null");

        System.out.println(isOwner);
//__________________________________________________________
        return GigDto.builder()
                .id(gig.getId())
                .sport(gig.getSport())
                .location(gig.getLocation())
                .dateTime(gig.getDateTime())
                .playersNeeded(gig.getPlayersNeeded())
                .status(gig.getStatus().name())
                .gigMasterUsername(gig.getGigMaster().getUsername())
                .acceptedParticipants(gig.getAcceptedParticipants().stream().map(User::getUsername).collect(Collectors.toSet()))
                .requestStatus(requestStatus)
                .Owner(isOwner)
                .Participant(isParticipant)
                .build();
    }

    private GigDto mapToGigDto(Gig gig){
        try {
            User currentUser = getAuthenticatedUser();
            return mapToGigDto(gig, currentUser);
        } catch (Exception e) {
            return mapToGigDto(gig, null);
        }
    }

    private GigRequestDto mapToGigRequestDto(GigRequest request){
        return GigRequestDto.builder()
                .requestId(request.getId())
                .requesterId(request.getRequester().getId())
                .requesterUsername(request.getRequester().getUsername())
                .build();
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void autoExpireGigs(){
        List<Gig> expiredGigs = gigRepository.findByStatusInAndDateTimeBefore(List.of(GigStatus.ACTIVE,GigStatus.FULL), LocalDateTime.now());

        if (expiredGigs.isEmpty()){
            return;
        }

        System.out.println("Found " + expiredGigs.size() + " expired gigs. Processing...");

        for (Gig gig : expiredGigs){
            gigRequestRepository.deleteByGig(gig);

            reviewRepository.deleteByGig(gig);

            chatService.deleteChatGroupForGig(gig);

            gigRepository.delete(gig);
        }
    }
}