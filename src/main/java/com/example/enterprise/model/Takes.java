package com.example.enterprise.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.text.MessageFormat;
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

    public Date finishDate;
    public Boolean completed;
    public Boolean passed;
    @Size(max = 100)
    public Integer score;

    /* 打印已完成的修读信息 */
    public String completedFormat() {
        if (!completed)
            return "";
        Object[] args = {
                finishDate.toString(),
                course.courseName,
                passed ? "passed" : "failed",
                score
        };
        MessageFormat fmt = new MessageFormat(
                "{0} - {1}, {2}, score: {3}");

        return fmt.format(args);
    }
}