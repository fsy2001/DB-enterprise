package com.example.enterprise.session;

import com.example.enterprise.model.Employee;
import com.example.enterprise.repository.RepositoryHolder;

import java.util.Scanner;

public class SupervisorSession extends EmployeeSession implements Session {
    public SupervisorSession(RepositoryHolder holder, Employee user) {
        super(holder, user);
    }

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- logged in as a department supervisor ---");
        boolean alive = true;
        while (alive) {
            try {
                System.out.print("supervisor> ");

                /* 根据命令内容分配任务 */
                String command = scanner.nextLine();
                switch (command) {
                    case "update-info":
                        updateInfo();
                        break;

                    case "show-info":
                        showInfo();
                        break;

                    case "exit":
                    case "logout":
                        alive = false;
                        break;

                    default:
                        System.out.println("command not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("--- logged out ---");
    }
}
