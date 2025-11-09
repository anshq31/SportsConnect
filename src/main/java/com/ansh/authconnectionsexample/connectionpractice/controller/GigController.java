package com.ansh.authconnectionsexample.connectionpractice.controller;

import com.ansh.authconnectionsexample.connectionpractice.dto.GigCreateRequest;
import com.ansh.authconnectionsexample.connectionpractice.dto.GigDto;
import com.ansh.authconnectionsexample.connectionpractice.dto.GigRequestDto;
import com.ansh.authconnectionsexample.connectionpractice.service.GigService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/gigs")
public class GigController {

    @Autowired
    private GigService gigService;

    @PostMapping
    public ResponseEntity<GigDto> createGig(@Valid @RequestBody GigCreateRequest createRequest){
        GigDto newGig = gigService.createGig(createRequest);
        return new ResponseEntity<>(newGig, HttpStatus.CREATED);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<GigDto>> getActiveGigs(@PageableDefault(size = 10)Pageable pageable){
        Page<GigDto> gigs = gigService.getAllActiveGigs(pageable);
        return ResponseEntity.ok(gigs);
    }

    @PostMapping("/{gigId}/request-join")
    public ResponseEntity<?> requestToJoin(@PathVariable Long gigId){
        gigService.requestToJoinGig(gigId);
        return ResponseEntity.ok("Join request sent successfully");
    }

    @GetMapping("/my-gig/requests")
    public ResponseEntity<List<GigRequestDto>> getMyGigRequests(){
        List<GigRequestDto> requests = gigService.getGigRequestsForMyGig();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/my-gig/requests/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId){
        gigService.acceptGigRequest(requestId);
        return ResponseEntity.ok("Request accepted");
    }

    @PostMapping("/my-gig/requests/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long requestId) {
        gigService.rejectGigRequest(requestId);
        return ResponseEntity.ok("Request rejected.");
    }

    @PutMapping("/{gigId}/complete")
    public ResponseEntity<GigDto> completeGig(@PathVariable Long gigId) {
        GigDto completedGig = gigService.completeGig(gigId);
        return ResponseEntity.ok(completedGig);
    }

    @DeleteMapping("/{gigId}")
    public ResponseEntity<?> deleteGig(@PathVariable Long gigId) {
        gigService.deleteGig(gigId);
        return ResponseEntity.ok("Gig deleted successfully.");
    }
}
