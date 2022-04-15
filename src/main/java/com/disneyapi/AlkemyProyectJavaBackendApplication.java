package com.disneyapi;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Genero;
import com.disneyapi.modelo.Pelicula;
import com.disneyapi.modelo.Personaje;
import com.disneyapi.modelo.Usuario;
import com.disneyapi.servicio.AlmacenamientoServicio;
import com.disneyapi.servicio.AudiovisualServicio;
import com.disneyapi.servicio.GeneroServicio;
import com.disneyapi.servicio.PersonajeServicio;
import com.disneyapi.servicio.UsuarioServicio;
import com.disneyapi.util.enumerados.RolUsuario;

@SpringBootApplication
public class AlkemyProyectJavaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlkemyProyectJavaBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(PersonajeServicio personajeServicio, AudiovisualServicio audiovisualServicio,
			GeneroServicio generoServicio, UsuarioServicio usuarioServicio, AlmacenamientoServicio servicioAlmacenamiento) {
		return args -> {

			servicioAlmacenamiento.borrarTodos();
			servicioAlmacenamiento.init();
			Genero g1 = new Genero(null, "accion", null);
			Genero g2 = new Genero(null, "drama", null);

			Personaje p = new Personaje(null, null, "popeye", 46, 85, null, null);
			Personaje p2 = new Personaje(null, null, "Micky", 22, 35, null, null);

			Audiovisual peli = new Pelicula(null, "Peli", LocalDate.now(), 4, null, null);
			Audiovisual peli2 = new Pelicula(null, "Arnold", LocalDate.of(2022, 2, 5), 3, null, null);

			p.setAudiovisuales(Arrays.asList(peli));
			peli.setPersonajes(Arrays.asList(p));
			p2.setAudiovisuales(Arrays.asList(peli2));
			peli2.setPersonajes(Arrays.asList(p2));
			peli.setGenero(g2);
			peli2.setGenero(g1);

			generoServicio.guardar(g1);
			generoServicio.guardar(g2);

			personajeServicio.guardar(p);
			personajeServicio.guardar(p2);

			audiovisualServicio.guardar(peli2);
			audiovisualServicio.guardar(peli);

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			Usuario admin = new Usuario(null, "Martin Iriarte", "iriartemartin", encoder.encode("34770296"),
					"algo@algo.com", Arrays.asList(RolUsuario.ROLE_ADMIN));
			usuarioServicio.guardar(admin);
		};
	}

}
