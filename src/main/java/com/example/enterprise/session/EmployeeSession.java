package com.example.enterprise.session;

import com.example.enterprise.model.Employee;
import com.example.enterprise.model.Takes;
import com.example.enterprise.repository.EmployeeRepository;
import com.example.enterprise.repository.RepositoryHolder;
import com.example.enterprise.repository.TakesRepository;

import java.io.PrintWriter;
import java.util.Scanner;

public class EmployeeSession implements Session {
    protected final RepositoryHolder holder;
    protected final Employee user;
    protected final EmployeeRepository employeeRepository;
    private Scanner in;
    private PrintWriter out;

    public EmployeeSession(RepositoryHolder holder, Employee user) {
        this.holder = holder;
        this.employeeRepository = holder.employeeRepository;
        this.user = user;
    }

    @Override
    public void start(Scanner in, PrintWriter out) {
        this.in = in;
        this.out = out;

        out.println("--- logged in as an employee ---"); // TODO: 显示用户信息
        boolean alive = true;
        while (alive) {
            try {
                /* 获取并检查命令 */
                out.println("input command: ");
                String message = in.nextLine();
                String[] parts = message.split("\\s+");
                if (parts.length == 0) {
                    out.println("syntax error");
                    continue;
                }

                /* 根据命令内容分配任务 */
                String command = parts[0];
                switch (command) {
                    // TODO: 增加一些命令
                    case "alter":
                        updateInfo(parts);
                        break;
//                    case "lessons":
//                        lessonsInfo(parts);
//                        break;

                    case "exit":
                        alive = false;
                        break;
                    default:
                        out.println("command not exist");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out.println("--- logged out ---");
    }

    public void updateInfo(String[] parts) {
        if (parts.length < 3) {
            out.println("syntax error");
            return;
        }
        switch (parts[1]) {
            case "name":
                user.name = parts[2];
                break;
            case "gender":
                if (parts[2].equals("male")) {
                    user.gender = true;
                } else if (parts[2].equals("female")) {
                    user.gender = false;
                } else {
                    out.println("syntax error");
                }
                break;
            case "age":
                user.age = Integer.parseInt(parts[2]);
                break;
            case "phoneNumber":
                user.phoneNumber = parts[2];
                break;
            case "email":
                user.email = parts[2];
                break;
            default:
                out.println("syntax error");
        }

        employeeRepository.save(user);
    }

//    public void lessonsInfo(String[] parts){
//        if (parts.length < 2) {
//            out.println("syntax error");
//            return;
//        }
//
//    }
}
