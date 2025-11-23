package com.ansh.authconnectionsexample.connectionpractice.service;

import com.ansh.authconnectionsexample.connectionpractice.model.enums.GigStatus;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Gig;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public  class GigSpecificationService {
    public static Specification<Gig> hasStatus(GigStatus status){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"),status));
    }

    public static Specification<Gig> hasSport(String sport){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("sport")),sport.toLowerCase()));
    }

    public static Specification<Gig> hasLocation(String location){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("location")),"%"+location.toLowerCase()+"%"));
    }
}
