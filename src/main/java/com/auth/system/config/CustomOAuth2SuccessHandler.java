package com.auth.system.config;

import com.auth.system.entity.Provider;
import com.auth.system.entity.Role;
import com.auth.system.entity.User;
import com.auth.system.repository.UserRepository;
import com.auth.system.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final String frontendUrl;

    public CustomOAuth2SuccessHandler(
            JwtService jwtService, 
            UserRepository userRepository,
            @org.springframework.beans.factory.annotation.Value("${app.frontend.url}") String frontendUrl) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken authToken = (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        if (email == null) {
            email = oAuth2User.getAttribute("login") + "@github.com";
        }

        // Check if user exists, otherwise create
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : email.split("@")[0]);
            user.setRole(Role.USER);
            user.setPassword(""); // OAuth users don't have local password
            
            if ("google".equalsIgnoreCase(registrationId)) {
                user.setProvider(Provider.GOOGLE);
            } else if ("github".equalsIgnoreCase(registrationId)) {
                user.setProvider(Provider.GITHUB);
            }
            
            userRepository.save(user);
        }

        String token = jwtService.generateToken(email);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
