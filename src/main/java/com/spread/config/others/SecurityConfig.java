package com.spread.config.others;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {		
		auth.inMemoryAuthentication().withUser("haytham").password("h@rr!2016").roles("ADMIN");
		//auth.inMemoryAuthentication().withUser("arouri").password("h@rr!2016").roles("ADMIN");
		//auth.inMemoryAuthentication().withUser("ahmad").password("123456").roles("ADMIN");
		//auth.inMemoryAuthentication().withUser("dba").password("123456").roles("DBA");
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
		//.antMatchers("/harri/**").access("hasRole('ROLE_ADMIN')")
		.and()
		    .formLogin().loginPage("/login").failureUrl("/login?error").defaultSuccessUrl("/admin")
		    .usernameParameter("username").passwordParameter("password")
		.and()
		    .logout().logoutSuccessUrl("/login?logout");
		//.and()
		 //   .csrf();
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(11);
	}
}
