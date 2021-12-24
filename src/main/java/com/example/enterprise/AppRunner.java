package com.example.enterprise;

import com.example.enterprise.repository.*;
import com.example.enterprise.session.MainSession;
import com.example.enterprise.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppRunner {
    RepositoryHolder holder;

    @Autowired
    public AppRunner(CourseRepository courseRepository,
                     EmployeeRepository employeeRepository,
                     DepartmentRepository departmentRepository,
                     TakesRepository takesRepository,
                     LinkRepository linkRepository) {
        this.holder =
                new RepositoryHolder(courseRepository,
                        employeeRepository,
                        departmentRepository,
                        takesRepository,
                        linkRepository);
    }

    private void initData() {

    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            initData();
            Session session = new MainSession(this.holder);
            session.start();
        };
    }
}
