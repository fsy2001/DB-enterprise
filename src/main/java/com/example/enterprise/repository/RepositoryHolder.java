package com.example.enterprise.repository;


public class RepositoryHolder {
    public CourseRepository courseRepository;
    public EmployeeRepository employeeRepository;
    public DepartmentRepository departmentRepository;
    public TakesRepository takesRepository;
    public LinkRepository linkRepository;
    public LogRepository logRepository;

    public RepositoryHolder(CourseRepository courseRepository,
                            EmployeeRepository employeeRepository,
                            DepartmentRepository departmentRepository,
                            TakesRepository takesRepository,
                            LinkRepository linkRepository,
                            LogRepository logRepository) {
        this.courseRepository = courseRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.takesRepository = takesRepository;
        this.linkRepository = linkRepository;
        this.logRepository = logRepository;
    }
}
