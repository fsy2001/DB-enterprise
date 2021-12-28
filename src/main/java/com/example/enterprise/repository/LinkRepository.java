package com.example.enterprise.repository;

import com.example.enterprise.model.Course;
import com.example.enterprise.model.Department;
import com.example.enterprise.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<Link, Integer> {

    List<Link> findLinksByDepartment(Department department);

    /* 查找必修课 */
    List<Link> findLinksByDepartmentAndMandatory(Department department, boolean mandatory);

    boolean existsByCourseAndDepartment(Course course, Department department);
}
