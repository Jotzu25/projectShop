package com.example.demo;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendEmailWithImage(String to, String subject, String text, ArrayList<String> imagePaths,
			ArrayList<String> names) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		{
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom("jo.tzu23@gmail.com");

			StringBuilder htmlContent = new StringBuilder("<html><body>");

			htmlContent.append("<h2>Hai acquistato : </h2>");
			htmlContent.append("<table border='1' style='border-collapse: collapse; width: 40%;'>");
			htmlContent.append("<tr><th>Nome</th><th>Immagine</th></tr>");

			for (int i = 0; i < names.size(); i++) {
				htmlContent.append("<tr>");
				htmlContent.append("<td >").append(names.get(i)).append("</td>");
				htmlContent.append("<td><img src='").append(imagePaths.get(i))
						.append("' style='width: 200px; height: 100px;'/></td>");
				htmlContent.append("</tr>");
			}

			htmlContent.append("</table>");

			htmlContent.append("<p>").append(text).append("</p>");

			htmlContent.append("</body></html>");
			helper.setText(htmlContent.toString(), true);

			mailSender.send(mimeMessage);
		}
	}

}
