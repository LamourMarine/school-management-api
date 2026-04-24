package com.marine.gestionecole.service;

import com.marine.gestionecole.entity.Teacher;
import com.marine.gestionecole.repository.TeacherRepository;

import jakarta.validation.constraints.Email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  final JavaMailSender mailSender;

  EmailService(
    JavaMailSender mailSender
  ) {
    this.mailSender = mailSender;
  }

  public void sendCredentials(
    String email,
    String username,
    String rawPassword
  ) {
    SimpleMailMessage message  = new SimpleMailMessage();
    message.setFrom("marine@marine.com");
    message.setTo(email);
    message.setSubject("Your School Management credentials");
    message.setText(
        "Hello " + username + ",\n\n" + 
        "Your account has been created on School Management.\n\n" +
        "Here are your credentials:\n" + 
        "Username: " + username + "\n" + 
        "Password: " + rawPassword + "\n\n" + 
        "Please log in and change your password as soon as possible. \n\n" +
        "Best regards,\n" +
        "School Management Team"
    );
    mailSender.send(message);
  }
}
