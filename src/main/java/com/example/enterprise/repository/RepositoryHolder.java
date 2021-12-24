package com.example.enterprise.repository;

public class RepositoryHolder {
    public CourseRepository courseRepository;
    public EmployeeRepository employeeRepository;
    public DepartmentRepository departmentRepository;
    public TakesRepository takesRepository;

    public RepositoryHolder(CourseRepository courseRepository,
                            EmployeeRepository employeeRepository,
                            DepartmentRepository departmentRepository,
                            TakesRepository takesRepository) {
        this.courseRepository = courseRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.takesRepository = takesRepository;
    }
}
