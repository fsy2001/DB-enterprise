package com.example.enterprise.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Course {
    @Id
    public Integer courseId;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    public Employee instructor;
    @NotBlank
    public String courseName;
    public CourseType type;

    @ManyToMany
    public List<Department> departments;
}