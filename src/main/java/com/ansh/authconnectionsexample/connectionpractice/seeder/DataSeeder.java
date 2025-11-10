package com.ansh.authconnectionsexample.connectionpractice.seeder;

import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.Skill;
import com.ansh.authconnectionsexample.connectionpractice.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private SkillRepository skillRepository;



    @Override
    public void run(String... args) throws Exception {
        if (skillRepository.count()==0){
            System.out.println("Seeding Skill data...");

            Skill s1 = new Skill();
            s1.setName("Basketball");

            Skill s2 = new Skill();
            s2.setName("Soccer");

            Skill s3 = new Skill();
            s3.setName("Tennis");

            Skill s4 = new Skill();
            s4.setName("Volleyball");

            Skill s5 = new Skill();
            s5.setName("Cricket");

            skillRepository.saveAll(List.of(s1,s2,s3,s4,s5));

            System.out.println("Skills data seeded!");
        }else {
            System.out.println("Skill data already exists, no seeding needed.");
        }
    }
}
