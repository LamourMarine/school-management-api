package com.marine.gestionecole.dto;

import lombok.Data;

@Data
public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private Double average;
}
