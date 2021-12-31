package com.example.enterprise.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public Date time;
    public String type;
    public String entity;
    public String description;

    public Log(String type, String entity, String description) {
        this.time = new Date();
        this.type = type;
        this.entity = entity;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id: " + id +
                ", time: " + time +
                ", type: '" + type + '\'' +
                ", entity: '" + entity + '\'' +
                ", description: '" + description + '\'' +
                '}';
    }
}
