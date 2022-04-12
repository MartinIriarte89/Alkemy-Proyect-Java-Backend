package com.disneyapi.modelo.objetonulo;

import com.disneyapi.modelo.ProductoAudiovisual;

public class ProductoAudiovisualNulo extends ProductoAudiovisual {

	public static ProductoAudiovisual contruir() {
		return new ProductoAudiovisualNulo();
	}
	
	@Override
	public boolean esNula() {
		return true;
	}
}
