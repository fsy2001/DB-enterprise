package com.example.enterprise.repository;

import com.example.enterprise.model.Course;
import com.example.enterprise.model.Employee;
import com.example.enterprise.model.Takes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TakesRepository extends JpaRepository<Takes, Integer> {
    List<Takes> findTakesByEmployeeAndCompleted(Employee employee, Boolean completed);
    /* 查找该课程未结业的修读记录（登分用） */
    List<Takes> findTakesByCourseAndCompleted(Course course, Boolean completed);
    /* 查找该员工该门课是否正在修读 */
    boolean existsByCourseAndEmployeeAndCompleted(Course course, Employee employee, Boolean completed);
}