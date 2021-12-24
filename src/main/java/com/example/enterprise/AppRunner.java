package com.example.enterprise;

import com.example.enterprise.repository.*;
import com.example.enterprise.session.MainSession;
import com.example.enterprise.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

@Configuration
public class AppRunner {
    RepositoryHolder holder;

    @Autowired
    public AppRunner(CourseRepository courseRepository,
                     EmployeeRepository employeeRepository,
                     DepartmentRepository departmentRepository,
                     TakesRepository takesRepository) {
        this.holder =
                new RepositoryHolder(courseRepository,
                employeeRepository,
                departmentRepository,
                takesRepository);
    }

    private void initData() {

    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            initData();
            OutputStream outputStream = System.out;
            InputStream inputStream = System.in;
            PrintWriter printer = new PrintWriter(outputStream, true);
            Scanner scanner = new Scanner(inputStream);
            Session session = new MainSession(this.holder);
            session.start(scanner, printer);
        };
    }
}
