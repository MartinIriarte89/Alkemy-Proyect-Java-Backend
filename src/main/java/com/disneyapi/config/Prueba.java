package com.disneyapi.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;


public class Prueba {
	
	@Value("${sendgrid.api.key}")
	private static String key;

	public static void main(String[] args)throws IOException {
		
			System.out.println(key);
			
			System.out.println("hola " + key);
		
		   Email from = new Email("alkemypruebajava@gmail.com");
		    String subject = "Sending with SendGrid is Fun";
		    Email to = new Email("test@example.com");
		    Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
		    Mail mail = new Mail(from, subject, to, content);

		    
		    SendGrid sg = new SendGrid("algo");
		    Request request = new Request();
		    try {
		      request.setMethod(Method.POST);
		      request.setEndpoint("mail/send");
		      request.setBody(mail.build());
		      
		      request.setBody(mail.build());
		      Response response = sg.api(request);
		      System.out.println(response.getStatusCode());
		      System.out.println(response.getBody());
		      System.out.println(response.getHeaders());
		    } catch (IOException ex) {
		      throw ex;
		    }
		  
	}

	}
