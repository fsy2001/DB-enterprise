package com.example.enterprise.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer courseId;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    public Employee instructor;

    @NotBlank
    public String courseName;
    public String summary;
}