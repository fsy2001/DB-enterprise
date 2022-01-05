package com.example.enterprise.session;

import com.example.enterprise.model.*;
import com.example.enterprise.repository.*;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class RootSession implements Session {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final CourseRepository courseRepository;
    private final LinkRepository linkRepository;
    private final TakesRepository takesRepository;
    private final LogRepository logRepository;
    private final Scanner scanner = new Scanner(System.in);

    public RootSession(RepositoryHolder holder) {
        this.departmentRepository = holder.departmentRepository;
        this.employeeRepository = holder.employeeRepository;
        this.courseRepository = holder.courseRepository;
        this.linkRepository = holder.linkRepository;
        this.takesRepository = holder.takesRepository;
        this.logRepository = holder.logRepository;
    }

    @Override
    public void start() {

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

                    case "search-employee":
                        searchEmployee();
                        break;

                    case "update-employee":
                        updateEmployee();
                        break;

                    case "update-course":
                        updateCourse();
                        break;

                    case "dump-log":
                        dumpLog();
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
            logRepository.save(new Log("add", "department", department.departmentName));
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

            /* 非本部门员工不得担任主管 */
            if (!employee.department.equals(department))
                throw new IllegalArgumentException("department supervisor must be chosen inside department");

            /* 绑定并储存 */
            department.supervisor = employee;
            departmentRepository.save(department);
            logRepository.save(new Log("update", "supervisor", employee.id + " as " + departmentName +"'s supervisor"));
        } catch (NumberFormatException | ConstraintViolationException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addEmployee() {
        try {
            /* 员工基本信息 */
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

            Employee newEmployee = employeeRepository.save(employee);

            logRepository.save(new Log("add", "employee", employee.name));

            /* 为新入职的员工分配必修课程 */
            List<Link> links =
                    linkRepository.findLinksByDepartmentAndMandatory(newEmployee.department, true);
            List<Takes> toTakeList = new ArrayList<>();
            links.forEach(link -> {
                Takes takes = new Takes(link.course, newEmployee);
                toTakeList.add(takes);
                logRepository.save(new Log("add", "take", employee.name + " - " + link.course.courseName));
            });
            takesRepository.saveAll(toTakeList);

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
                    .orElseThrow(() -> new IllegalArgumentException("employee not exist"));

            if (employee.instructor)
                throw new IllegalArgumentException("already an instructor"); // 不能将已经是教员的人设置为教员

            if (employee.department.supervisor != null
                    && employee.department.supervisor.equals(employee)) // 教员、部门主管不得兼职
                throw new IllegalArgumentException("department supervisor can't be instructor");

            employee.instructor = true;
            employeeRepository.save(employee);
            logRepository.save(new Log("update", "employee", employee.name + " - instructor"));
        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchEmployee() {
        try {
            System.out.print("id: ");
            int id = Integer.parseInt(scanner.nextLine());
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("no such employee"));
            System.out.println(employee.toString());
            System.out.println("history scores: ");
            List<Takes> takesList =
                    takesRepository.findTakesByEmployeeAndCompleted(employee, true);
            if (takesList.size() == 0) {
                System.out.println("empty score list");
                return;
            }
            takesList.forEach(takes -> System.out.println(takes.completedFormat()));
        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateEmployee() {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("no such employee"));

            String property;

            System.out.print("enter property (or finish): ");
            while (!(property = scanner.nextLine()).equals("finish")) {
                switch (property) {
                    case "name":
                        System.out.print("name: ");
                        employee.name = scanner.nextLine();
                        break;

                    case "age":
                        System.out.print("age: ");
                        employee.age = Integer.parseInt(scanner.nextLine());
                        break;

                    case "gender":
                        System.out.print("gender: ");
                        String gender = scanner.nextLine();
                        if (gender.equals("male")) employee.gender = true;
                        else if (gender.equals("female")) employee.gender = false;
                        else throw new IllegalArgumentException("incorrect format");
                        break;

                    case "phone":
                        System.out.print("phone: ");
                        employee.phoneNumber = scanner.nextLine();
                        break;

                    case "email":
                        System.out.print("email: ");
                        employee.email = scanner.nextLine();
                        break;

                    default:
                        System.out.println("no such property");
                }
                System.out.print("enter property (or finish): ");
            }
            employeeRepository.save(employee);
            logRepository.save(new Log("update", "employee", employee.id.toString()));
        } catch (NumberFormatException | ConstraintViolationException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateCourse() {
        try {
            System.out.print("course ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("no such course"));
            System.out.print("course name: ");
            course.courseName = scanner.nextLine();

            System.out.print("course summary: ");
            course.summary = scanner.nextLine();
            courseRepository.save(course);

            logRepository.save(new Log("update", "course", course.courseId.toString()));
        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void dumpLog() {
        logRepository.findAll().forEach(System.out::println);
    }
}
