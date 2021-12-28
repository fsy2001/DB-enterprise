package com.example.enterprise.session;

import com.example.enterprise.model.*;
import com.example.enterprise.repository.CourseRepository;
import com.example.enterprise.repository.DepartmentRepository;
import com.example.enterprise.repository.LinkRepository;
import com.example.enterprise.repository.RepositoryHolder;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class InstructorSession extends EmployeeSession implements Session {
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final LinkRepository linkRepository;

    public InstructorSession(RepositoryHolder holder, Employee user) {
        super(holder, user);
        this.courseRepository = holder.courseRepository;
        this.departmentRepository = holder.departmentRepository;
        this.linkRepository = holder.linkRepository;
    }

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- logged in as an instructor ---");
        boolean alive = true;
        while (alive) {
            try {
                System.out.print("instructor> ");

                /* 根据命令内容分配任务 */
                String command = scanner.nextLine();
                switch (command) {
                    case "update-info":
                        updateInfo();
                        break;

                    case "show-info":
                        showInfo();
                        break;

                    case "add-course":
                        addCourse();
                        break;

                    case "set-score":
                        setScore();
                        break;

                    case "update-course":
                        updateCourse();
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

    private void addCourse() {
        Scanner scanner = new Scanner(System.in);

        try {
            /* 基本信息 */
            Course course = new Course();
            course.instructor = user;

            System.out.print("course name: ");
            course.courseName = scanner.nextLine();

            System.out.print("course summary: ");
            course.summary = scanner.nextLine();

            /* 选择课程关联的部门列表，并指定其必修/选修 */
            List<Link> links = new ArrayList<>();
            List<String> departmentNames = new ArrayList<>(); // 用于防止部门重复
            String departmentName;
            System.out.print("department name: ");
            departmentName = scanner.nextLine();
            do {
                if (departmentNames.contains(departmentName))
                    throw new IllegalArgumentException("duplicate"); // 发现重复部门，报错退出
                departmentNames.add(departmentName);
                Department department = departmentRepository.findById(departmentName)
                        .orElseThrow(() -> new IllegalArgumentException("department not exist"));

                boolean mandatory;
                System.out.print("course type (mandatory / optional): ");
                String type = scanner.nextLine();
                if (type.equals("mandatory")) mandatory = true;
                else if (type.equals("optional")) mandatory = false;
                else throw new IllegalArgumentException("incorrect format");

                links.add(new Link(course, department, mandatory));
                System.out.print("department name (or finish to exit): ");
            }
            while (!(departmentName = scanner.nextLine()).equals("finish"));

            /* 保存数据 */
            courseRepository.save(course);
            linkRepository.saveAll(links);

            /* 将必修课员工添加到名单 */
            for (Link link : links)
                if (link.mandatory) enroll(link.department, course);

        } catch (NumberFormatException | ConstraintViolationException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }


    /* 辅助函数，将该部门的员工全部加入该课程 */
    private void enroll(Department department, Course course) {
        List<Employee> employees = employeeRepository.findEmployeesByDepartment(department);
        List<Takes> takesList = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.instructor ||
                    (employee.department.supervisor != null && employee.department.supervisor.equals(employee)))
                continue; // 教员和部门主管免修
            Takes takes = new Takes(course, employee);
            takesList.add(takes);
        }

        takesRepository.saveAll(takesList);
    }

    private void setScore() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("course ID: ");
            int courseId = Integer.parseInt(scanner.nextLine());
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("course not exist"));
            List<Takes> takesList =
                    takesRepository.findTakesByCourseAndCompleted(course, false);

            /* 依次手动登记分数 */
            for (Takes takes : takesList) {
                String name = takes.employee.name;
                int employeeId = takes.employee.id;
                System.out.print(name + " (" + employeeId + "), score: ");
                String input = scanner.nextLine();
                if (input.equals("pass"))
                    continue;
                int score = Integer.parseInt(input);
                takes.completed = true;
                takes.score = score;
                takes.passed = (score >= 60);
                takes.finishDate = new Date();
            }

            takesRepository.saveAll(takesList);

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
        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
