package com.disneyapi.util.enumerados;

public enum RolUsuario {

	ROLE_ADMIN("ADMIN"), ROLE_USER("USER"), ROLE_MAGANER_ADMIN("MANAGER_ADMIN");
	
	private String rol;
	
	private RolUsuario(String rol) {
		this.rol = rol;
	}
	
	public String getRol() {
		return this.rol;
	}
}
