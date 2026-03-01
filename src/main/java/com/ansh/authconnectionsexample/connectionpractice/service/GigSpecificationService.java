package com.ansh.authconnectionsexample.connectionpractice.service;

import com.ansh.authconnectionsexample.connectionpractice.model.enums.GigStatus;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Gig;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
                criteriaBuilder.like(criteriaBuilder.lower(root.get("location")),"%"+location.toLowerCase()+"%"));
    }

    public static Specification<Gig> notCreatedBy(String username){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(criteriaBuilder.lower(root.get("gigMaster").get("username")), username));
    }

    public static Specification<Gig> hasParticipant(String username){
        return (root, query, criteriaBuilder) -> {
            if (query != null) {
                query.distinct(true);
            }

            Join<Object,Object> participants = root.join("acceptedParticipants", JoinType.INNER);

            return criteriaBuilder.equal(participants.get("username"),username);
        };
    }

    public static Specification<Gig> hasGigMaster(String username){
        return (root, query, criteriaBuilder) -> {
            if (query != null) {
                query.distinct(true);
            }

            Join<Gig, User> gigMaster = root.join("gigMaster",JoinType.INNER);

            return criteriaBuilder.equal(gigMaster.get("username"),username);
        };
    }

    public static Specification<Gig> userNotParticipant(String username){
        return (root, query, criteriaBuilder) -> {
            if (query!= null){
                query.distinct(true);
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Gig> subRoot = subquery.from(Gig.class);
            Join<Gig,User> participants = subRoot.join("acceptedParticipants",JoinType.LEFT);

            subquery.select(subRoot.get("id"))
                    .where(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
                                    criteriaBuilder.equal(participants.get("username"), username)
                            )
                    );

            return criteriaBuilder.not(criteriaBuilder.exists(subquery));

        };
    }
}
