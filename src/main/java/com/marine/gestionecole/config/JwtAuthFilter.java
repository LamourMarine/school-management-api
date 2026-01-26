package com.marine.gestionecole.config;
import com.marine.gestionecole.service.JwtService;
import com.marine.gestionecole.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
) throws ServletException, IOException {
    // Exclure seulement le health check du filtre JWT
    String path = request.getRequestURI();
    if (path.equals("/api/health")) {
        filterChain.doFilter(request, response);
        return;
    }
        
        final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    // 2. Si pas de header ou ne commence pas par "Bearer "
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);  // Passe au filtre suivant
        return;  // Sort de la méthode
    }
    // 3. Extraire le token (enlever "Bearer ")
    jwt = authHeader.substring(7);
    // 4. Extraire le username du token
    username = jwtService.extractUsername(jwt);
    // 4. Si username trouvé ET pas encore authentifié
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        
        // 5. Charger les détails de l'utilisateur
        UserDetails userDetails = userService.loadUserByUsername(username);

        // 6. Vérifier si le token est valide
        if (jwtService.isTokenValid(jwt, userDetails)) {
            
            // 7. Créer l'objet d'authentification
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,           // Principal (l'utilisateur)
                    null,                  // Credentials (pas besoin, on a le token)
                    userDetails.getAuthorities()  // Rôles (USER, ADMIN)
            );
            
            // 8. Ajouter les détails de la requête
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // 9. Dire à Spring Security : "Cet utilisateur est authentifié !"
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
        
        filterChain.doFilter(request, response);
    }
}
