package com.example.enterprise.repository;

import com.example.enterprise.model.Employee;
import com.example.enterprise.model.Takes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TakesRepository extends JpaRepository<Takes, Integer> {
    List<Takes> findByEmployee(Employee employee);

//    @Query("SELECT takes FROM Takes takes " +
//            "WHERE takes.employee.id = :id " +
//            "AND NOT (takes.completed)")
//    List<Takes> findCurrentCourse(Integer id);

//    @Query("SELECT takes FROM Takes takes " +
//            "WHERE takes.employee = :employee " +
//            "AND takes.completed")
//    List<Takes> findHistory(Employee employee);
}