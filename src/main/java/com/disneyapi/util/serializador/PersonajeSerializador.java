package com.disneyapi.util.serializador;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.disneyapi.modelo.Audiovisual;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PersonajeSerializador extends StdSerializer<List<Audiovisual>> {

	private static final long serialVersionUID = 4471937060615484971L;

	public PersonajeSerializador() {
		this(null);
	}

	public PersonajeSerializador(Class<List<Audiovisual>> t) {
		super(t);
	}

	@Override
	public void serialize(List<Audiovisual> audiovisuales, JsonGenerator generator, SerializerProvider provider)
			throws IOException {

		generator.writeStartArray();
		for (Audiovisual a : audiovisuales) {
			generator.writeStartObject();
			generator.writeFieldName("id");
			generator.writeNumber(a.getId());
			generator.writeFieldName("urlImagen");
			generator.writeString(a.getUrlImagen());
			generator.writeFieldName("titulo");
			generator.writeString(a.getTitulo());
			generator.writeFieldName("fechaDeCreacion");
			generator.writeString(a.getFechaDeCreacion()
									.format(DateTimeFormatter
									.ofPattern("dd-MM-yyyy")));
			generator.writeFieldName("calificacion");
			generator.writeNumber(a.getCalificacion());
			generator.writeFieldName("genero");
			generator.writeObject(a.getGenero());
			generator.writeEndObject();
		}
		generator.writeEndArray();
	}
}
