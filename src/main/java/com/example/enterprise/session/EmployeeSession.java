package com.example.enterprise.session;

import com.example.enterprise.model.Employee;
import com.example.enterprise.repository.RepositoryHolder;

import java.io.PrintWriter;
import java.util.Scanner;

public class EmployeeSession implements Session {
    protected final RepositoryHolder holder;
    protected final Employee user;

    public EmployeeSession(RepositoryHolder holder, Employee user) {
        this.holder = holder;
        this.user = user;
    }

    @Override
    public void start(Scanner in, PrintWriter out) {
        out.println("--- logged in as an employee ---");
    }
}
