package com.disneyapi.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.util.constantes.RutaUtilidades;
import com.disneyapi.util.enumerados.RolUsuario;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SeguridadConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final AutorizacionFiltro autorizacionFiltro;
	private static final String[] AUT_LISTAPERMITIDOS = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/auth/**",
            "/h2-console/**"
    };
	
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
				.antMatchers(AUT_LISTAPERMITIDOS).permitAll()
				.antMatchers(HttpMethod.POST, RutaUtilidades.AUDIOVISUALES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(HttpMethod.PUT, RutaUtilidades.AUDIOVISUALES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers( HttpMethod.DELETE, RutaUtilidades.AUDIOVISUALES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(HttpMethod.POST, RutaUtilidades.PERSONAJES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(HttpMethod.PUT, RutaUtilidades.PERSONAJES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers( HttpMethod.DELETE, RutaUtilidades.PERSONAJES).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(HttpMethod.POST, RutaUtilidades.GENEROS).hasRole(RolUsuario.ROLE_ADMIN.getRol())
				.antMatchers(RutaUtilidades.USUARIOS).hasRole(RolUsuario.ROLE_ADMIN.getRol())
			.anyRequest().authenticated();
		http.addFilterBefore(autorizacionFiltro, UsernamePasswordAuthenticationFilter.class);
	}
}
