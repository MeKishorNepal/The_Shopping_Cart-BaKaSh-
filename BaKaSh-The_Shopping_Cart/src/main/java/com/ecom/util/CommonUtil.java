package com.ecom.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component   //for making bean of it
public class CommonUtil {
	
	@Autowired
	private  JavaMailSender mailSender;
	
	public  Boolean sendMail(String url,String reciepentemail) throws UnsupportedEncodingException, MessagingException {
		
		
		//code for mail sending process
		MimeMessage message=mailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(message);
		
		helper.setFrom("nepalkishor21@gmail.com","Shooping Cart");
		helper.setTo(reciepentemail);
		
		
		String content="<p>Hello,</p>" +"<p>You have requested to reset your password</p>"
		                +"<p> Click the link below to change your password:</p>"+"<p> <a href=\""+ url
		                + "\"><b>Change my password</b></a></p>";
		
		
		helper.setSubject("Password Reset");
		helper.setText(content,true);
		
		mailSender.send(message);
		return true;
		
	
	}
	
	
	
	
//getting url and replace its servlet path
	public static String generateUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        return scheme + "://" + serverName + ":" + serverPort + contextPath + "/";
    }

}
