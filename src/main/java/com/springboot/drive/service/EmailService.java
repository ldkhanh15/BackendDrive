package com.springboot.drive.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    public EmailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine
    ) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    public void sendEmail(){
        SimpleMailMessage mail=new SimpleMailMessage();
        mail.setTo("ledinhkhanh2004@gmail.com");
        mail.setText("Hello world");
        this.mailSender.send(mail);
    }
    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR WITH EMAIL: "+e);
        }
    }

    @Async
    public void sendEmailFromTemplate(String to,String subject,String templateName,Object value){
        Context context=new Context();

        context.setVariable("data",value);
        String content=springTemplateEngine.process(templateName,context);

        this.sendEmailSync(to,subject,content,false,true);
    }

}
