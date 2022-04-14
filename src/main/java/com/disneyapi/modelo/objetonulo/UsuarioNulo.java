package com.disneyapi.modelo.objetonulo;

import com.disneyapi.modelo.Usuario;

public class UsuarioNulo extends Usuario{

	public static Usuario construir() {
		return new UsuarioNulo();
	}
	
	@Override
	public boolean esNulo() {
		return true;
	}
}
