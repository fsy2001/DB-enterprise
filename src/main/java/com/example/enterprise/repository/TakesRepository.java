package com.example.enterprise.repository;

import com.example.enterprise.model.Course;
import com.example.enterprise.model.Department;
import com.example.enterprise.model.Employee;
import com.example.enterprise.model.Takes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TakesRepository extends JpaRepository<Takes, Integer> {
    List<Takes> findTakesByEmployeeAndCompleted(Employee employee, Boolean completed);

    /* 查找该课程未结业的修读记录（登分用） */
    List<Takes> findTakesByCourseAndCompleted(Course course, Boolean completed);

    /* 查找该员工该门课是否正在修读 */
    boolean existsByCourseAndEmployeeAndCompleted(Course course, Employee employee, Boolean completed);

    boolean existsByCourseAndEmployee(Course course, Employee employee);

    List<Takes> findTakesByCourse_CourseIdAndEmployee_Department(Integer course_courseId, Department employee_department);

    List<Takes> findTakesByCourse_CourseIdAndEmployee_DepartmentAndScoreLessThan(Integer course_courseId, Department employee_department, Integer score);

    List<Takes> findTakesByCourse_CourseIdAndEmployee_DepartmentAndScoreGreaterThan(Integer course_courseId, Department employee_department, Integer score);

    List<Takes> findTakesByCourse_CourseIdAndEmployee_DepartmentAndScore(Integer course_courseId, Department employee_department, Integer score);

    boolean existsByEmployeeAndCourseAndPassed(Employee employee, Course course, boolean passed);

    boolean existsByEmployeeAndCompleted(Employee employee, Boolean completed);

    @Query(value = "SELECT COUNT(DISTINCT course_course_id) FROM takes WHERE employee_id = ?1",
            nativeQuery = true)
    int allCourse(int employeeId);

    int countByEmployeeAndPassed(Employee employee, Boolean passed);

    @Query(value = "select distinct(employee_id) as target_id from takes as general " +
            "where " +
            "(select count(number) from takes where course_course_id = ?1 " +
            "and passed = false and employee_id = general.employee_id) = ?2",
            nativeQuery = true)
    List<Integer> findFailedEmployeesEquals(int courseId, int failCount);

    @Query(value = "select distinct(employee_id) as target_id from takes as general " +
            "where " +
            "(select count(number) from takes where course_course_id = ?1 " +
            "and passed = false and employee_id = general.employee_id) > ?2",
            nativeQuery = true)
    List<Integer> findFailedEmployeesGreater(int courseId, int failCount);

    @Query(value = "select distinct(employee_id) as target_id from takes as general " +
            "where " +
            "(select count(number) from takes where course_course_id = ?1 " +
            "and passed = false and employee_id = general.employee_id) < ?2",
            nativeQuery = true)
    List<Integer> findFailedEmployeesLesser(int courseId, int failCount);
}