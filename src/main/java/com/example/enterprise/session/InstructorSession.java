package com.example.enterprise.session;

import com.example.enterprise.model.Employee;
import com.example.enterprise.repository.RepositoryHolder;

import java.io.PrintWriter;
import java.util.Scanner;

public class InstructorSession extends EmployeeSession implements Session{
    public InstructorSession(RepositoryHolder holder, Employee user) {
        super(holder, user);
    }

    @Override
    public void start(Scanner in, PrintWriter out) {
        out.println("--- logged in as an instructor ---");
    }
}
