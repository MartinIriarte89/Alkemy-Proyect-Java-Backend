package com.disneyapi.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.util.constantes.Ruta;
import com.disneyapi.util.enumerados.RolUsuario;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SeguridadConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final AutorizacionFiltro autorizacionFiltro;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().headers()
				.frameOptions().disable()
			.and()
			.authorizeHttpRequests()
				.antMatchers("/auth/**").permitAll()
				.antMatchers(HttpMethod.POST, Ruta.AUDIOVISUALES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(HttpMethod.PUT, Ruta.AUDIOVISUALES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers( HttpMethod.DELETE, Ruta.AUDIOVISUALES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(HttpMethod.POST, Ruta.PERSONAJES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(HttpMethod.PUT, Ruta.PERSONAJES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers( HttpMethod.DELETE, Ruta.PERSONAJES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(HttpMethod.POST, Ruta.GENEROS).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(Ruta.USUARIOS).hasRole(RolUsuario.ROLE_ADMIN.getRol())
			.anyRequest().authenticated();
		http.addFilterBefore(autorizacionFiltro, UsernamePasswordAuthenticationFilter.class);
	}
}
