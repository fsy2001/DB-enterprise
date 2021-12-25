package com.example.enterprise.repository;

import com.example.enterprise.model.Department;
import com.example.enterprise.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findEmployeesByDepartment(Department department);
    List<Employee> findEmployeesByNameLikeAndDepartment(String name, Department department);
}
