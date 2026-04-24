package com.marine.gestionecole.dto;

import lombok.Data;

@Data
public class CreateTeacherRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
