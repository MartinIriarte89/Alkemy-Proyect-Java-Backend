package com.disneyapi.modelo.objetonulo;

import com.disneyapi.modelo.Personaje;

public class PersonajeNulo extends Personaje {

	public static Personaje construir() {
		return new PersonajeNulo();
	}

	@Override
	public boolean esNulo() {
		return true;
	}
}
