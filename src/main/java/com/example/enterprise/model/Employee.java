package com.example.enterprise.model;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.sql.Date;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "dept_name")
    public Department department;
    public Boolean instructor;

    @NotBlank
    public String name;
    public Boolean gender;
    @Positive
    public Integer age;
    public Date entranceDate;
    public String phoneNumber;
    @Email
    public String email;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Employee)) return false;
        Employee employee = (Employee) obj;
        return this.id.equals(employee.id);
    }
}
