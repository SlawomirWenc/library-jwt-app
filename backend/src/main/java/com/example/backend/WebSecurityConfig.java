package com.example.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${secret}")
    String secret;

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/books").permitAll()
                .antMatchers( "/signUp", "/signIn").permitAll();

        http.authorizeRequests()
                .antMatchers("/booksByUsername", "/addBook", "/removeBook").authenticated()
                .antMatchers("/users", "/createUser", "/deleteUser", "/getUser", "/updateUser").hasRole("ADMIN")
                .and()
                .addFilter(new JWTFilter(authenticationManager(), secret));
    }
}
