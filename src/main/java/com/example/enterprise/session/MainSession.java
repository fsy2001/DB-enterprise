package com.example.enterprise.session;

import com.example.enterprise.model.Employee;
import com.example.enterprise.repository.EmployeeRepository;
import com.example.enterprise.repository.RepositoryHolder;

import java.util.Scanner;

public class MainSession implements Session {
    private final EmployeeRepository employeeRepository;
    public RepositoryHolder holder;

    public MainSession(RepositoryHolder holder) {
        this.holder = holder;
        this.employeeRepository = holder.employeeRepository;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- enterprise management system started ---");
        Session session;

        /* 登录逻辑 */
        while (true) {
            try {
                System.out.print("> ");
                String command = scanner.nextLine();
                if (command.equals("login")) { // 登录
                    System.out.print("input username: ");
                    String username = scanner.nextLine();
                    if (username.equals("root")) {
                        session = new RootSession(holder);
                    } else {
                        Integer userId = Integer.parseInt(username);
                        Employee employee = employeeRepository.findById(userId).orElseThrow(
                                () -> new IllegalArgumentException("user not exist, try again"));
                        if (employee.instructor) session = new InstructorSession(holder, employee);
                        else if (employee.equals(employee.department.supervisor))
                            session = new SupervisorSession(holder, employee);
                        else session = new EmployeeSession(holder, employee);
                    }
                    session.start();
                } else if (command.equals("exit")) return;
                else System.out.println("command not found");

            } catch (NumberFormatException e) {
                System.out.println("invalid format");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
