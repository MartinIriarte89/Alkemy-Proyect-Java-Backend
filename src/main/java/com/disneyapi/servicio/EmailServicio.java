package com.disneyapi.servicio;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServicio {

	private final SendGrid sendGrid;
	@Value("${mi.email}")
	private String miEmail;

	public boolean enviarMail(String nombreUsuario, String emailUsuario) {

		Email de = new Email(miEmail);
		String asunto = "¡¡Bienvenido " + nombreUsuario;
		Email para = new Email(emailUsuario);
		Content contenido = new Content("text/plain", "Gracias por registrarte en DisneyWorldLATAM");
		Mail mail = new Mail(de, asunto, para, contenido);

		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());

			Response response = sendGrid.api(request);
			log.info("estado", response.getStatusCode());
			log.info(response.getBody());
			log.info("cabeceras", response.getHeaders());
			return true;
		} catch (IOException ex) {
			return false;
		}
	}
}
