package com.example.enterprise.session;

import com.example.enterprise.model.*;
import com.example.enterprise.repository.CourseRepository;
import com.example.enterprise.repository.LinkRepository;
import com.example.enterprise.repository.RepositoryHolder;

import javax.validation.ConstraintViolationException;
import java.util.List;

public class SupervisorSession extends EmployeeSession implements Session {
    private final LinkRepository linkRepository;
    private final Department department;
    private final CourseRepository courseRepository;

    public SupervisorSession(RepositoryHolder holder, Employee user) {
        super(holder, user);
        this.department = user.department;
        this.linkRepository = holder.linkRepository;
        this.courseRepository = holder.courseRepository;
    }

    @Override
    public void start() {
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

                    case "department-course":
                        departmentCourse();
                        break;

                    case "department-employee":
                        departmentEmployee();
                        break;

                    case "search-name":
                        searchName();
                        break;

                    case "list-score":
                        listScore();
                        break;

                    case "allocate-course":
                        allocateCourse();
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

    private void departmentCourse() { // 列出部门内所有课程
        List<Link> links = linkRepository.findLinksByDepartment(department);
        links.forEach(link -> System.out.println(link.course.toString()));
    }

    private void departmentEmployee() { // 列出部门内员工
        List<Employee> employees = employeeRepository.findEmployeesByDepartment(department);
        employees.forEach(employee -> System.out.println(employee + "\n"));
    }

    private void searchName() { // 根据姓名查找员工号（只能查找本部门）
        System.out.println("name: ");
        List<Employee> employees =
                employeeRepository.findEmployeesByNameLikeAndDepartment(scanner.nextLine(), department);
        System.out.println("found " + employees.size() + "results: ");
        employees.forEach(employee -> System.out.println("ID: " + employee.id));
    }

    private void listScore() { // 根据员工号查找成绩
        try {
            /* 根据ID查找员工 */
            System.out.print("ID: ");
            Employee employee =
                    employeeRepository.findById(Integer.parseInt(scanner.nextLine()))
                            .orElseThrow(() -> new IllegalArgumentException("no such employee"));

            /* 显示员工成绩信息 */
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

    private void allocateCourse() {
        try {
            /* 查找员工 */
            System.out.print("employee ID: ");
            int employeeId = Integer.parseInt(scanner.nextLine());
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("no such employee"));

            /* 查找课程 */
            System.out.print("course ID: ");
            int courseId = Integer.parseInt(scanner.nextLine());
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("no such course"));

            /* 若该课程不属于该部门，则部门主管无权分配 */
            if (!linkRepository.existsByCourseAndDepartment(course, department))
                throw new IllegalArgumentException("course not within department jurisdiction, request denied");

            /* 检查该员工是否正在修读这门课程，不得同时修同一门课两次 */
            boolean alreadyTaken =
                    takesRepository.existsByCourseAndEmployeeAndCompleted(course, employee, false);
            if (alreadyTaken) throw new IllegalArgumentException("course already taken");

            /* 创建并存储修读记录 */
            Takes takes = new Takes(course, employee);
            takesRepository.save(takes);

        } catch (NumberFormatException | ConstraintViolationException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
