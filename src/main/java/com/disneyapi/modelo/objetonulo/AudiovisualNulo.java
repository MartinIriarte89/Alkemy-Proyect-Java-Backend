package com.disneyapi.modelo.objetonulo;

import com.disneyapi.modelo.Audiovisual;

public class AudiovisualNulo extends Audiovisual {

	public static Audiovisual contruir() {
		return new AudiovisualNulo();
	}
	
	@Override
	public boolean esNula() {
		return true;
	}
}
