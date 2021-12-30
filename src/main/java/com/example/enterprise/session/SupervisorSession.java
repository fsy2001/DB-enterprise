package com.example.enterprise.session;

import com.example.enterprise.model.*;
import com.example.enterprise.repository.CourseRepository;
import com.example.enterprise.repository.DepartmentRepository;
import com.example.enterprise.repository.LinkRepository;
import com.example.enterprise.repository.RepositoryHolder;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class SupervisorSession extends EmployeeSession implements Session {
    private final LinkRepository linkRepository;
    private final Department department;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public SupervisorSession(RepositoryHolder holder, Employee user) {
        super(holder, user);
        this.department = user.department;
        this.linkRepository = holder.linkRepository;
        this.courseRepository = holder.courseRepository;
        this.departmentRepository = holder.departmentRepository;
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

                    case "search-course-score":
                        searchCourseScore();
                        break;

                    case "search-score-employee":
                        searchScoreEmployee();
                        break;

                    case "check-transfer":
                        checkTransfer();
                        break;

                    case "list-transfer-qualified":
                        listTransferQualified();
                        break;

                    case "list-new-course":
                        listNewCourse();
                        break;

                    case "transfer":
                        transfer();
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

            /* 非本部门员工不得添加 */
            if (!employee.department.equals(department))
                throw new IllegalArgumentException("employee not belongs to this department");

            if (employee.instructor ||
                    (employee.department.supervisor != null && employee.department.supervisor.equals(employee)))
                throw new IllegalArgumentException("instructor or supervisor cannot take course"); // 教员和部门主管免修

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

            /* 检查该员工是否已通过该课程，已经通过的不得再选修 */
            boolean alreadyPassed =
                    takesRepository.existsByEmployeeAndCourseAndPassed(employee, course, true);
            if (alreadyPassed) throw new IllegalArgumentException("course already passed");

            /* 创建并存储修读记录 */
            Takes takes = new Takes(course, employee);
            takesRepository.save(takes);

        } catch (NumberFormatException | ConstraintViolationException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchCourseScore() {
        try {
            System.out.print("Course ID: ");
            int courseId = Integer.parseInt(scanner.nextLine());
            List<Takes> takesList =
                    takesRepository.findTakesByCourse_CourseIdAndEmployee_Department(courseId, department);
            if (takesList.size() == 0) {
                System.out.println("nobody takes the course or the course doesn't exist");
                return;
            }
            takesList.forEach(takes -> System.out.println(takes.courseScore()));
        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        }
    }

    private void searchScoreEmployee() {
        try {
            System.out.print("Course ID: ");
            int courseId = Integer.parseInt(scanner.nextLine());
            System.out.print("please choose >, < or = : ");
            String in = scanner.nextLine();
            switch (in) {
                case "<":
                    System.out.print("input score: ");
                    int score = Integer.parseInt(scanner.nextLine());
                    List<Takes> takesList =
                            takesRepository.findTakesByCourse_CourseIdAndEmployee_DepartmentAndScoreLessThan(courseId, department, score);
                    if (takesList.size() == 0) {
                        System.out.println("no result found");
                        return;
                    }
                    takesList.forEach(takes -> System.out.println(takes.employee.id + " " + takes.employee.name + " " + takes.score));
                    break;

                case ">":
                    System.out.print("input score: ");
                    int score2 = Integer.parseInt(scanner.nextLine());
                    List<Takes> takesList2 = takesRepository.findTakesByCourse_CourseIdAndEmployee_DepartmentAndScoreGreaterThan(courseId, department, score2);
                    if (takesList2.size() == 0) {
                        System.out.println("no result found");
                        return;
                    }
                    takesList2.forEach(takes -> System.out.println(takes.employee.id + " " + takes.employee.name + " " + takes.score));
                    break;

                case "=":
                    System.out.print("input score: ");
                    int score3 = Integer.parseInt(scanner.nextLine());
                    List<Takes> takesList3 = takesRepository.findTakesByCourse_CourseIdAndEmployee_DepartmentAndScore(courseId, department, score3);
                    if (takesList3.size() == 0) {
                        System.out.println("no result found");
                        return;
                    }
                    takesList3.forEach(takes -> System.out.println(takes.employee.id + " " + takes.employee.name + " " + takes.score));
                    break;

                default:
                    System.out.println("incorrect format");
            }
        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        }
    }

    private void checkTransfer() {
        try {
            /* 根据ID查找员工 */
            System.out.print("ID: ");
            Employee employee =
                    employeeRepository.findById(Integer.parseInt(scanner.nextLine()))
                            .orElseThrow(() -> new IllegalArgumentException("no such employee"));

            if (transferable(employee))
                System.out.println("transfer qualified");
            else System.out.println("transfer not qualified");

        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listTransferQualified() {
        List<Employee> employees = employeeRepository.findEmployeesByDepartment(department);
        employees
                .stream()
                .filter(this::transferable)
                .forEach(employee -> System.out.println(employee.toString()));
    }

    private void listNewCourse() {
        try {
            /* 根据ID查找员工 */
            System.out.print("ID: ");
            Employee employee =
                    employeeRepository.findById(Integer.parseInt(scanner.nextLine()))
                            .orElseThrow(() -> new IllegalArgumentException("no such employee"));

            /* 如员工不能转部门，则无须查询 */
            if (!transferable(employee))
                throw new IllegalArgumentException("transfer not qualified");

            /* 获取新部门 */
            System.out.print("department: ");
            Department newDepartment = departmentRepository.findById(scanner.nextLine())
                    .orElseThrow(() -> new IllegalArgumentException("department not exist"));

            /* 查询并打印 */
            List<Course> courseList = newCourseAfterTransfer(employee, newDepartment);
            courseList.forEach(course -> System.out.println(course.toString()));
        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void transfer() {
        try {
            /* 根据ID查找员工 */
            System.out.print("ID: ");
            Employee employee =
                    employeeRepository.findById(Integer.parseInt(scanner.nextLine()))
                            .orElseThrow(() -> new IllegalArgumentException("no such employee"));

            /* 不是本部门的员工不能转 */
            if (!employee.department.equals(department))
                throw new IllegalArgumentException("employee not belong to this department, access denied");

            /* 教员和部门主管不能转 */
            if (employee.equals(user) || employee.instructor)
                throw new IllegalArgumentException("cannot transfer instructor or supervisor");

            /* 如员工不能转部门，则无须查询 */
            if (!transferable(employee))
                throw new IllegalArgumentException("transfer not qualified");

            /* 获取新部门 */
            System.out.print("department: ");
            Department newDepartment = departmentRepository.findById(scanner.nextLine())
                    .orElseThrow(() -> new IllegalArgumentException("department not exist"));

            /* 选修新课程 */
            List<Course> courseList = newCourseAfterTransfer(employee, newDepartment);
            List<Takes> toTakeList = new ArrayList<>();
            courseList.forEach(course -> toTakeList.add(new Takes(course, employee)));
            takesRepository.saveAll(toTakeList);

            /* 更改部门 */
            employee.department = newDepartment;
            employeeRepository.save(employee);
        } catch (NumberFormatException e) {
            System.out.println("incorrect format");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<Course> newCourseAfterTransfer(Employee employee, Department newDepartment) {
        List<Link> links = linkRepository.findLinksByDepartmentAndMandatory(newDepartment, true);
        List<Course> courseList = new ArrayList<>();
        links.stream()
                /* 过滤已经修读的课程 */
                .filter(link -> !takesRepository.existsByCourseAndEmployee(link.course, employee))
                .forEach(link -> courseList.add(link.course));
        return courseList;
    }

    private boolean transferable(Employee employee) { // 检查员工是否能转出该部门
        if ((employee.department.supervisor != null && employee.department.supervisor.equals(employee))
                || employee.instructor)
            return false;
        boolean allCompleted = !takesRepository.existsByEmployeeAndCompleted(employee, false);
        boolean allPassed =
                takesRepository.allCourse(employee.id) == takesRepository.countByEmployeeAndPassed(employee, true);
        return allCompleted && allPassed;
    }
}
