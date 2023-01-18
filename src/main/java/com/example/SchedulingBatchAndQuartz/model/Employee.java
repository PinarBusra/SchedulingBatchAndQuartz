package com.example.SchedulingBatchAndQuartz.model;

import lombok.Data;

@Data
public class Employee {
    String id;
    String firstName;
    String lastName;
    @Override
    public String toString() {
        return "Employee [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }
}
