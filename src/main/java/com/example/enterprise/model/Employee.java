package com.example.enterprise.model;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.text.MessageFormat;
import java.util.Date;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "dept_name")
    public Department department;
    public Boolean instructor;

    @NotBlank
    public String name;
    public Boolean gender; // 男: true, 女: false
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

    @Override
    public String toString() {
        Object[] args = {
                this.id,
                this.name,
                this.age,
                this.gender ? "male" : "female",
                this.department == null ? "null" : this.department.departmentName,
                this.phoneNumber == null ? "null" : this.phoneNumber,
                this.email == null ? "null" : this.email,
                this.entranceDate.toString()};
        MessageFormat fmt = new MessageFormat(
                "ID: {0}\n" +
                        "name: {1}\n" +
                        "age: {2}\n" +
                        "gender: {3}\n" +
                        "department: {4}\n" +
                        "phone: {5}\n" +
                        "email: {6}\n" +
                        "entrance date: {7}");
        return fmt.format(args);
    }
}
