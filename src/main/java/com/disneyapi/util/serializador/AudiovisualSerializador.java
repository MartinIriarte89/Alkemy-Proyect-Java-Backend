package com.disneyapi.util.serializador;

import java.io.IOException;
import java.util.List;

import com.disneyapi.modelo.Personaje;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class AudiovisualSerializador extends StdSerializer<List<Personaje>> {

	private static final long serialVersionUID = 2978761378196745461L;

	public AudiovisualSerializador() {
		this(null);
	}

	public AudiovisualSerializador(Class<List<Personaje>> t) {
		super(t);
	}

	@Override
	public void serialize(List<Personaje> personajes, JsonGenerator generator, SerializerProvider provider)
			throws IOException {

		generator.writeStartArray();
		for (Personaje p : personajes) {
			generator.writeStartObject();
			generator.writeFieldName("id");
			generator.writeNumber(p.getId());
			generator.writeFieldName("urlImagen");
			generator.writeString(p.getUrlImagen());
			generator.writeFieldName("nombre");
			generator.writeString(p.getNombre());
			generator.writeFieldName("edad");
			generator.writeNumber(p.getEdad());
			generator.writeFieldName("peso");
			generator.writeNumber(p.getPeso());
			generator.writeFieldName("historia");
			generator.writeString(p.getHistoria());
			generator.writeEndObject();
		}
		generator.writeEndArray();
	}
}
