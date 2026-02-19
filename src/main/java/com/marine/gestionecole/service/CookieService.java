package com.marine.gestionecole.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

  public void addRefreshTokenCookie(
    HttpServletResponse response,
    String refreshToken
  ) {
    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // true en production avec HTTPS
    cookie.setPath("/api/auth");
    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours en secondes
    cookie.setAttribute("SameSite", "None"); // Protection CSRF
    response.addCookie(cookie);
  }

  public void deleteRefreshTokenCookie(HttpServletResponse response) {
      Cookie deleteCookie = new Cookie("refreshToken", null);
      deleteCookie.setPath("/api/auth");
      deleteCookie.setHttpOnly(true);
      deleteCookie.setMaxAge(0); //  Supprime le cookie
      response.addCookie(deleteCookie);

  }

  public String getRefreshTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
    }
    return null;
}
}
