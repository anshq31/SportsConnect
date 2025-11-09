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
import com.ansh.authconnectionsexample.connectionpractice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    private User getAuthenticatedUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Authenticated user could not be found with username:"+username));
    }

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

        return mapToGigDto(savedGig);
    }

    @Transactional(readOnly = true)
    public Page<GigDto> getAllActiveGigs(Pageable pageable){
        Page<Gig> gigPage = gigRepository.findByStatus(GigStatus.ACTIVE,pageable);
        return gigPage.map(this::mapToGigDto);
    }

    public void requestToJoinGig(Long gigId){
        User requester = getAuthenticatedUser();
        Gig gig = gigRepository.findById(gigId)
                .orElseThrow(()->new RuntimeException("Gig not found"));
        if (gig.getStatus() != GigStatus.ACTIVE){
            throw new RuntimeException("This gig is not longer Active");
        }

        GigRequest gigRequest = GigRequest.builder()
                .gig(gig)
                .requester(requester)
                .status(RequestStatus.PENDING)
                .build();
        gigRequestRepository.save(gigRequest);
    }

    @Transactional(readOnly = true)
    public List<GigRequestDto> getGigRequestForMyGig(){
        User user = getAuthenticatedUser();
        Gig gig = gigRepository.findByGigMasterAndStatusIn(user,List.of(GigStatus.ACTIVE,GigStatus.FULL))
                .orElseThrow(()->new RuntimeException("User doest not have an active gig"));

        List<GigRequest> requests = gigRequestRepository.findByGigAndStatus(gig,RequestStatus.PENDING);
        return requests.stream().map(this::mapToGigRequestDto).collect(Collectors.toList());
    }

    public void acceptGigRequest(Long requestId){
        User gigMaster = getAuthenticatedUser();
        GigRequest request = gigRequestRepository.findById(requestId)
                .orElseThrow(()-> new RuntimeException("Request was not found"));

        Gig gig = request.getGig();

        if (!gig.getGigMaster().equals(gigMaster)){
            throw new RuntimeException("User is not authorized to accept this request");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        gig.getAcceptedParticipants().add(request.getRequester());

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

        if (request.getGig().getGigMaster() != gigMaster){
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
        gig.setStatus(GigStatus.ACTIVE);
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

        chatService.deleteChatGroupForGig(gig);
        gigRepository.delete(gig);
    }



    private GigDto mapToGigDto(Gig gig){
        return GigDto.builder()
                .id(gig.getId())
                .sport(gig.getSport())
                .location(gig.getLocation())
                .dateTime(gig.getDateTime())
                .playersNeeded(gig.getPlayersNeeded())
                .status(gig.getStatus().name())
                .gigMasterUsername(gig.getGigMaster().getUsername())
                .acceptedParticipants(gig.getAcceptedParticipants().stream().map(User::getUsername).collect(Collectors.toSet()))
                .build();
    }

    private GigRequestDto mapToGigRequestDto(GigRequest request){
        return GigRequestDto.builder()
                .requestId(request.getId())
                .requesterId(request.getRequester().getId())
                .requesterUsername(request.getRequester().getUsername())
                .build();
    }
}