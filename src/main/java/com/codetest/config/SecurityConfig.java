package com.codetest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnProperty(name = "auth.disabled", matchIfMissing = true, havingValue = "false")
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${user.password}")
    private String userPassword;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public UserDetailsService userDetailsService() {
        final InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
        userDetailsService.createUser(User.withUsername("user").password("{noop}" + userPassword).roles("read").build());
        userDetailsService.createUser(User.withUsername("admin").password("{noop}" + adminPassword).roles("read", "write").build());
        return userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                    auth.requestMatchers("/h2-console/*").permitAll()
                    .requestMatchers(HttpMethod.POST).hasRole("write")
                    .requestMatchers(HttpMethod.DELETE).hasRole("write")
                    .requestMatchers("/**").hasAnyRole("read", "write")
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
