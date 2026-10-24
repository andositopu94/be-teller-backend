package com.multipolar.uiteller.backend.service.email;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class MailNotificationSender {
	
	private static Logger logger = LoggerFactory.getLogger(MailNotificationSender.class);
	
	@Autowired
	public JavaMailSender sender;
	
	@Autowired
    private Configuration freemarkerConfig;
	
	@Value("${smtp.mail.from}")
	private String mailFrom;
	@Value("${smtp.alias.from")
	private String aliasFrom;
	
	public void sendEmailWithoutTemplating(MailModel model) {
		if (model.getMailTo() == null) {
			logger.error("Null mail to is NULL; " + model);
			throw new IllegalArgumentException("Mail to can't NULL");
		}
		
		try {
			MimeMessage message = sender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message);
	        
	        helper.setFrom(new InternetAddress(mailFrom, aliasFrom));
	        helper.setTo(model.getMailTo());
	        helper.setText(model.getBody());
	        helper.setSubject(model.getSubject());
	        
	        sender.send(message);
		} catch (MessagingException | UnsupportedEncodingException ex) {
			logger.error("error happen when try to sending email", ex);
		}
	}

	public void sendEmailWithTemplating(MailModel model){
		if (model.getMailTo() == null) {
			logger.error("Null mail to is NULL; " + model);
			throw new IllegalArgumentException("Mail to can't NULL");
		}
		
		try {
			MimeMessage message = sender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message);
	        
	        // set loading location to src/main/resources
	        // You may want to use a subfolder such as /templates here
	        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
	        
	        Template t = freemarkerConfig.getTemplate(model.getTemplateName());
	        String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model.getTemplateModel());

	        helper.setTo(model.getMailTo());
	        helper.setText(text, true); // set to html
	        helper.setSubject(model.getSubject());

	        sender.send(message);
		} catch (IOException | TemplateException | MessagingException ex) {
			logger.error("error happen when try to sending email", ex);
		}
		
	}
}
