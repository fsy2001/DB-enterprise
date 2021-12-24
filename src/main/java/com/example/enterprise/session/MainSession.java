package com.example.enterprise.session;

import com.example.enterprise.model.Employee;
import com.example.enterprise.repository.DepartmentRepository;
import com.example.enterprise.repository.EmployeeRepository;
import com.example.enterprise.repository.RepositoryHolder;

import java.io.PrintWriter;
import java.util.Scanner;

public class MainSession implements Session {
    public RepositoryHolder holder;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public MainSession(RepositoryHolder holder) {
        this.holder = holder;
        this.employeeRepository = holder.employeeRepository;
        this.departmentRepository = holder.departmentRepository;
    }

    public void start(Scanner in, PrintWriter out) {
        out.println("--- enterprise management system started ---");
        Session session;

        /* 登录逻辑 */
        while (true) {
            try {
                out.println("input username: ");
                String username = in.nextLine();
                if (username.equals("exit")) break; // 退出指令

                if (username.equals("root")) {
                    session = new RootSession(holder);
                } else {
                    Integer userId = Integer.parseInt(username);
                    Employee employee = employeeRepository.findById(userId).orElseThrow(
                            () -> new IllegalArgumentException("user not exist, try again"));
                    if (employee.instructor) session = new InstructorSession(holder, employee);
                    else { // 判断是否为部门主管
                        Employee supervisor =
                                departmentRepository.getById(employee.department.departmentName).supervisor;
                        if (employee.equals(supervisor)) session = new SupervisorSession(holder, employee);
                        else session = new EmployeeSession(holder, employee);
                    }
                }
                session.start(in, out);
            } catch (NumberFormatException e) {
                out.println("invalid format");
            } catch (IllegalArgumentException e) {
                out.println(e.getMessage());
            }
        }
    }
}
