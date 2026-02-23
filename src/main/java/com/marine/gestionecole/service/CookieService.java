package com.marine.gestionecole.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

  @Value("${cookie.secure:true}")
  private boolean cookieSecure;

  @Value("${cookie.same-site:None}")
  private String cookieSameSite;  

  @Value("${cookie.partitioned:true}")
  private boolean cookiePartitioned;

  public void addRefreshTokenCookie(
    HttpServletResponse response,
    String refreshToken
  ) {
    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(cookieSecure); // true en production avec HTTPS
    cookie.setPath("/api/auth");
    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours en secondes
    cookie.setAttribute("SameSite", cookieSameSite); // Protection CSRF
    if (cookiePartitioned) {
    cookie.setAttribute("Partitioned", "");
}
    response.addCookie(cookie);
  }

  public void deleteRefreshTokenCookie(HttpServletResponse response) {
    Cookie deleteCookie = new Cookie("refreshToken", null);
    deleteCookie.setPath("/api/auth");
    deleteCookie.setHttpOnly(true);
    deleteCookie.setSecure(cookieSecure);
    deleteCookie.setMaxAge(0);
    deleteCookie.setAttribute("SameSite", "None");
    deleteCookie.setAttribute("Partitioned", "");
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
