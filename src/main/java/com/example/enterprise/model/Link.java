package com.example.enterprise.model;

import javax.persistence.*;

@Entity
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer listId;

    @ManyToOne
    public Course course;
    @ManyToOne
    public Department department;
    public Boolean mandatory;

    public Link() {
    }

    public Link(Course course, Department department, Boolean mandatory) {
        this.course = course;
        this.department = department;
        this.mandatory = mandatory;
    }
}
