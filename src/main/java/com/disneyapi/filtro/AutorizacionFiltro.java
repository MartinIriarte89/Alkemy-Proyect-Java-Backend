package com.disneyapi.filtro;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.disneyapi.seguridad.JwtProveedor;
import com.disneyapi.servicio.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutorizacionFiltro extends OncePerRequestFilter {

	private final JwtProveedor jwtProveedor;
	private final UserDetailsServiceImpl detailsServiceImpl;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String token = obtenerTokenDeLaRequest(request);

			if (StringUtils.hasText(token) && jwtProveedor.esValido(token)) {
				UserDetails userDetails = detailsServiceImpl
						.loadUserByUsername(jwtProveedor.obtenerUsernameDeJWT(token));

				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, jwtProveedor.obtenerRolesDeJWT(token), userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}

		} catch (Exception e) {
			log.info("No se ha podido establecer la autenticación de usuario en el contexto de seguridad");
		}

		filterChain.doFilter(request, response);
	}

	private String obtenerTokenDeLaRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(JwtProveedor.TOKEN_HEADER);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtProveedor.TOKEN_PREFIX)) {
			return bearerToken.substring(JwtProveedor.TOKEN_PREFIX.length(), bearerToken.length());
		}

		return null;
	}

}
