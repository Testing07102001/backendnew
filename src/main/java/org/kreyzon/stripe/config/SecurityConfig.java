package org.kreyzon.stripe.config;

import org.kreyzon.stripe.security.JwtAuthenticationEntryPoint;
import org.kreyzon.stripe.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint point;

    @Autowired
    private JwtAuthenticationFilter filter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().disable()
                .authorizeRequests()
                .antMatchers("/auth/login",
                        "/api/v1/stripe/capture-payment",
                        "/api/v1/stripe/create-payment",
                        "/auth/create-user",
                        "/alumni/{nric}",
                        "/categorys",
                        "/category/{id}",
                        "/courses",
                        "/requeststudent",
                        "/request-otp",
                        "/verify-otp",
                        "/api/auth/create-user",
                        "/registrationadvances/{id}",
                        "/registrationadvances",

                        "/registrationadvances/alumni/{nric}",
                        "/registrationadvances/{id}/session-details",
                        "/registrationadvances/info/{id}",
                        "/batch-summary",
                        "/registrationadvances/sendInvoice",
                        "/registrationadvances/{id}/session-details",
                        "/registrationadvances/{id}/payment-status",
                        "/student/{id}/activate",
                        "/registrationadvances/{id}/course-fee-details",
                        "/registrationadvances/{id}/course-fee-details/set-status",
                        "/student/{id}/register",
                        "/registrationadvances/send",
                        "/registrationadvances/detailsForSendInvoice/{id}",
                        "/registrationadvances/send-receipt/{id}",
                        "/registrationadvances/send-receipts/{id}",
                        "/home/{sfid}/activate",
                        "/home/check-email",
                        "/home/usertype/{usertype}",
                        "/reset-password").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(point)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
