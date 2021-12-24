package com.example.enterprise.session;

import com.example.enterprise.model.Department;
import com.example.enterprise.model.Employee;
import com.example.enterprise.repository.DepartmentRepository;
import com.example.enterprise.repository.EmployeeRepository;
import com.example.enterprise.repository.RepositoryHolder;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Scanner;

public class RootSession implements Session {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public RootSession(RepositoryHolder holder) {
        this.departmentRepository = holder.departmentRepository;
        this.employeeRepository = holder.employeeRepository;
    }

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- logged in as the system administrator ---");
        boolean alive = true;
        while (alive) {
            try {
                System.out.print("root> ");

                /* 根据命令内容分配任务 */
                String command = scanner.nextLine();
                switch (command) {
                    case "add-department":
                        addDepartment();
                        break;

                    case "set-supervisor":
                        setSupervisor();
                        break;

                    case "add-employee":
                        addEmployee();
                        break;

                    case "set-instructor":
                        setInstructor();
                        break;

                    case "exit":
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

    private void addDepartment() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("department name: ");
            String departmentName = scanner.nextLine();

            /* 检查是否重名 */
            if (departmentRepository.existsById(departmentName)) {
                System.out.println("name taken");
                return;
            }

            Department department = new Department(departmentName);
            departmentRepository.save(department);
        } catch (ConstraintViolationException e) {
            System.out.println("incorrect format");
        }
    }

    private void setSupervisor() {
        try {
            Scanner scanner = new Scanner(System.in);

            /* 获取部门对象 */
            System.out.print("department name: ");
            String departmentName = scanner.nextLine();
            Department department = departmentRepository.findById(departmentName)
                    .orElseThrow(() -> new IllegalArgumentException("department not exist"));

            /* 获取雇员对象 */
            System.out.print("supervisor ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("employee not exist"));

            /* 教员、部门主管不得兼职 */
            if (employee.instructor) throw new IllegalArgumentException("instructor can't be supervisor");

            /* 绑定并储存 */
            department.supervisor = employee;
            departmentRepository.save(department);
        } catch (NumberFormatException | ConstraintViolationException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addEmployee() {
        try {
            Scanner scanner = new Scanner(System.in);
            Employee employee = new Employee();

            System.out.print("name: ");
            employee.name = scanner.nextLine();

            employee.instructor = false;

            System.out.print("department: ");
            employee.department = departmentRepository.findById(scanner.nextLine())
                    .orElseThrow(() -> new IllegalArgumentException("department not exist"));

            System.out.print("age: ");
            employee.age = Integer.parseInt(scanner.nextLine());

            System.out.print("gender: ");
            String gender = scanner.nextLine();
            if (gender.equals("male")) employee.gender = true;
            else if (gender.equals("female")) employee.gender = false;
            else throw new IllegalArgumentException("incorrect format");

            employee.entranceDate = new Date();

            employeeRepository.save(employee);
        } catch (NumberFormatException | ConstraintViolationException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setInstructor() {
        try {
            /* 根据用户输入的ID找到对应员工 */
            Scanner scanner = new Scanner(System.in);
            System.out.print("instructor ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(()->new IllegalArgumentException("employee not exist"));

            if (employee.instructor)
                throw new IllegalArgumentException("already an instructor"); // 不能将已经是教员的人设置为教员

            if (employee.department.supervisor.equals(employee)) // 教员、部门主管不得兼职
                throw new IllegalArgumentException("department supervisor can't be instructor");

            employee.instructor = true;
            employeeRepository.save(employee);

        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
