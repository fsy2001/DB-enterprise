package com.example.enterprise.repository;

public class RepositoryHolder {
    public CourseRepository courseRepository;
    public EmployeeRepository employeeRepository;
    public DepartmentRepository departmentRepository;
    public TakesRepository takesRepository;
    public LinkRepository linkRepository;

    public RepositoryHolder(CourseRepository courseRepository,
                            EmployeeRepository employeeRepository,
                            DepartmentRepository departmentRepository,
                            TakesRepository takesRepository,
                            LinkRepository linkRepository) {
        this.courseRepository = courseRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.takesRepository = takesRepository;
        this.linkRepository = linkRepository;
    }
}
