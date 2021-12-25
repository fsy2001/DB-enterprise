package com.example.enterprise.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.text.MessageFormat;

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

    @Override
    public String toString() {
        Object[] args = {
                this.courseId,
                this.courseName,
                this.instructor.name,
                this.summary
        };
        MessageFormat fmt = new MessageFormat(
                "{1} ({0}) - instructor: {2}, summary: {3}");

        return fmt.format(args);
    }
}