package com.parkinglot.config;

import com.parkinglot.entity.User;
import com.parkinglot.enums.UserRole;
import com.parkinglot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/admin/**","/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**", "/api/parking/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .defaultSuccessUrl("/home", true)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    // Role mapper: assign USER by default, ADMIN manually
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);

            String googleId = oidcUser.getSubject();
            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName();

            // Save or update in DB
            User user = userRepository.findByGoogleId(googleId)
                    .orElseGet(User::new);
            user.setGoogleId(googleId);
            user.setUsername(email);
            user.setName(name);

            // Assign role (default USER)




            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            String regId = userRequest.getClientRegistration().getRegistrationId();

            if ("google-admin".equals(regId)) {
                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                user.setRole(UserRole.ADMIN);
            } else {
                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                user.setRole(UserRole.USER);
            }
            userRepository.save(user);

            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }
}
