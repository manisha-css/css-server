package com.cynosure.security.config;

import com.cynosure.filter.CorsFilter;
import com.cynosure.filter.exceptionhandler.ExceptionHandlerFilter;
import com.cynosure.security.filter.JWTAuthenticationFilter;
import com.cynosure.security.filter.JWTLoginFilter;
import com.cynosure.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;

@Configuration
@EnableWebSecurity
public class CssWebSecurity extends WebSecurityConfigurerAdapter {

  private static final String WILDCARD_EXPRESSION = "/*/**";
  @Autowired CorsFilter corsFilter;
  @Autowired ExceptionHandlerFilter exceptionHandlerFilter;
  @Autowired CustomUserDetailsService customUserDetailsService;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
        .antMatchers(HttpMethod.OPTIONS, WILDCARD_EXPRESSION)
        .antMatchers("/resources/**")
        .antMatchers(HttpMethod.GET, "/healthcheck/**")
        .antMatchers(HttpMethod.POST, "/user/register")
        .antMatchers(HttpMethod.POST, "/user/verification/**")
        .antMatchers(HttpMethod.POST, "/user/forgetPassword")
        .antMatchers(HttpMethod.POST, "/contactus")
        .antMatchers(
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, "/user/login")
        .permitAll()
        .antMatchers("/guestuser/" + WILDCARD_EXPRESSION)
        .permitAll()
        .antMatchers("/user/" + WILDCARD_EXPRESSION)
        .hasRole("USER")
        .antMatchers("/admin/" + WILDCARD_EXPRESSION)
        .hasRole("ADMIN")
        // all other requests need to be authenticated
        .anyRequest()
        .hasRole("USER")
        .and()
        .addFilterBefore(corsFilter, HeaderWriterFilter.class)
        .addFilterBefore(exceptionHandlerFilter, CorsFilter.class)
        .addFilterBefore(
            new JWTLoginFilter("/user/login", authenticationManager(), customUserDetailsService),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf()
        .disable();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
    auth.authenticationProvider(daoAuthenticationProvider);
  }
}
