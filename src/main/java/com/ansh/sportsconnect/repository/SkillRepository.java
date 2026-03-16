package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.userAndAuthEntities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long> {

}
