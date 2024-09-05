package openchat.easytalk.Config;

import lombok.RequiredArgsConstructor;
import openchat.easytalk.Config.Jwt.JwtAuthenticationFilter;
import openchat.easytalk.User.Components.Enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {


    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**", "/api/v1/demo-controller/**",
                        "/api/v1/user/findAllDoctors",
                        "/api/v1/appointment/getDoctorWithAppointments", "/ws-endpoint/**")
                .permitAll()
                .requestMatchers("/api/v1/user/**", "/api/v1/friends/**"
                        , "/api/v1/appointment/**").hasAnyAuthority(Role.PATIENT.name(), Role.DOCTOR.name())
                .requestMatchers("/api/v1/admin/**").hasAnyAuthority((Role.ADMIN.name()))
                .anyRequest()
                .authenticated()


                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
}
