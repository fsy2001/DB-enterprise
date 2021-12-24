package com.example.enterprise.session;

import com.example.enterprise.model.Employee;
import com.example.enterprise.repository.EmployeeRepository;
import com.example.enterprise.repository.RepositoryHolder;
import com.example.enterprise.repository.TakesRepository;

import javax.validation.ConstraintViolationException;
import java.util.Scanner;

public class EmployeeSession implements Session {
    protected final RepositoryHolder holder;
    protected final Employee user;
    protected final EmployeeRepository employeeRepository;
    protected final TakesRepository takesRepository;

    public EmployeeSession(RepositoryHolder holder, Employee user) {
        this.holder = holder;
        this.employeeRepository = holder.employeeRepository;
        this.takesRepository = holder.takesRepository;
        this.user = user;
    }

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- logged in as an employee ---");
        boolean alive = true;
        while (alive) {
            try {
                System.out.print("employee> ");

                /* 根据命令内容分配任务 */
                String command = scanner.nextLine();
                switch (command) {
                    case "update-info":
                        updateInfo();
                        break;

                    case "show-course":
                        showCourse();
                        break;

                    case "show-info":
                        showInfo();
                        break;

                    case "show-history-score":
                        showHistoryScore();
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

    protected void updateInfo() {
        try {
            String property;
            Scanner scanner = new Scanner(System.in);

            System.out.print("enter property (or finish): ");
            while (!(property = scanner.nextLine()).equals("finish")) {
                switch (property) {
                    case "name":
                        System.out.print("name: ");
                        user.name = scanner.nextLine();
                        break;

                    case "age":
                        System.out.print("age: ");
                        user.age = Integer.parseInt(scanner.nextLine());
                        break;

                    case "gender":
                        System.out.print("gender: ");
                        String gender = scanner.nextLine();
                        if (gender.equals("male")) user.gender = true;
                        else if (gender.equals("female")) user.gender = false;
                        else throw new IllegalArgumentException("incorrect format");
                        break;

                    case "phone":
                        System.out.print("phone: ");
                        user.phoneNumber = scanner.nextLine();
                        break;

                    case "email":
                        System.out.print("email: ");
                        user.email = scanner.nextLine();
                        break;

                    default:
                        System.out.println("no such property");
                }
                System.out.print("enter property (or finish): ");
            }
            employeeRepository.save(user);
        } catch (NumberFormatException | ConstraintViolationException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }

    protected void showInfo() {
        System.out.println(user.toString());
    }

    protected void showCourse() {
//        List<Takes> takesList = takesRepository.findCurrentCourse(user.id);
//        if (takesList.size() == 0) {
//            System.out.println("empty course list");
//            return;
//        }
//        for (Takes record : takesList)
//            System.out.println("course: " + record.course.courseName +
//                    " instructor: " + record.course.instructor.name);
    }

    protected void showHistoryScore() {
//        List<Takes> takesList = takesRepository.findHistory(user);
//        if (takesList.size() == 0) {
//            System.out.println("empty history list");
//            return;
//        }
//        for (Takes record : takesList) {
//            System.out.println(record.completedFormat());
//        }
    }
}
