package com.example.enterprise.repository;

import com.example.enterprise.model.Employee;
import com.example.enterprise.model.Takes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TakesRepository extends JpaRepository<Takes, Integer> {
    List<Takes> findByEmployee(Employee employee);

}
