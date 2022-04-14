package com.disneyapi.seguridad;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProveedor {

	@Value("${jwt.secret}")
	private String jwtSecreto;

	@Value("${jwt.expiration}")
	private int jwtDuracionEnSeg;
	
	private final UsuarioServicio usuarioServicio;
	private final Algorithm algoritmo = Algorithm.HMAC256(jwtSecreto.getBytes());
	private final JWTVerifier verificador = JWT.require(algoritmo).build();
	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String TOKEN_TYPE = "JWT";

	

	public String generarToken(String username) {
		Usuario usuario = usuarioServicio.buscarPorEmail(username);
		
		Date fechaExpiracionToken = new Date(System.currentTimeMillis() + (jwtDuracionEnSeg * 1000));
		String rolesConFormato = usuario.getRoles().stream()
													.map(RolUsuario::name)
													.collect(Collectors.joining(", "));
		
		return JWT.create().withHeader(Map.of("type", TOKEN_TYPE))
							.withSubject(Long.toString(usuario.getId()))
							.withIssuedAt(new Date())
							.withExpiresAt(fechaExpiracionToken)
							.withClaim("roles", rolesConFormato)
							.withClaim("username", usuario.getEmail())
							.sign(algoritmo);
	}
	
	public Long obtenerIdDeJWT(String token) {
		DecodedJWT jwtDecodificado = verificador.verify(token);
		
		return Long.parseLong(jwtDecodificado.getSubject());
	}
	
	public List<String> obtenerRolesDeJWT(String token){
		DecodedJWT jwtDecodificado = verificador.verify(token);
		
		return jwtDecodificado.getClaim("roles").asList(String.class);
	}
	
	public String obtenerUsernameDeJWT(String token) {
		DecodedJWT jwtDecodificado = verificador.verify(token);
		
		return jwtDecodificado.getClaim("username").asString();
	}
	
	public boolean esValido(String token) {
		try {
			verificador.verify(token);
			return true;
		}catch (JWTDecodeException e) {
			log.info("Token malformado: " + e.getMessage());
		} catch (TokenExpiredException e) {
			log.info("El token ha expirado: " + e.getMessage());
		} catch (SignatureVerificationException e) {
			log.info("Error en la firma del token JWT: " + e.getMessage());
		} catch (InvalidClaimException e) {
			log.info("JWT claims vacío");
		} catch (AlgorithmMismatchException e) {
			log.info("Token JWT no soportado: " + e.getMessage());
		}
	}
}
