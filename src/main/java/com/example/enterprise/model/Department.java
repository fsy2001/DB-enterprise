package com.example.enterprise.model;

import javax.persistence.*;

@Entity
@Table(name = "department")
public class Department {
    @Id
    @Column(name = "dept_name")
    public String departmentName;
    @OneToOne
    public Employee supervisor;
}
