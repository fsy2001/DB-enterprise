package com.example.enterprise.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Takes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer number;
    @ManyToOne
    public Course course;
    @ManyToOne
    public Employee employee;
    public Date enrollDate;

    public Boolean completed;
    public Boolean passed;
    @Size(max = 100)
    public Integer grade;
    
}