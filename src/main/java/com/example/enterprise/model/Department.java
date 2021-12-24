package com.example.enterprise.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "department")
public class Department {
    @Id
    @Column(name = "dept_name")
    @NotBlank
    public String departmentName;
    @OneToOne
    public Employee supervisor;

    public Department() {
    }

    public Department(String departmentName) {
        this.departmentName = departmentName;
    }
}
