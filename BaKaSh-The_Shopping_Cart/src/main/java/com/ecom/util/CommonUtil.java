package com.ecom.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecom.model.ProductOrder;

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
	

	
	
	//mail sending when customer book a order
	
	String msg="<b>Hello [[Name]],</b><br>" +
			"<p> Thank you for visiting our site . Your order <b>[[orderStatus]]</b>.</p>"
               + "<p> <b>Product Details:</b> </p>"
			   +"<p> Name:[[productName]] </p>"
			   +"<p> Category:[[category]] </p>"
			   +"<p> Quantity:[[quantity]]</p>"
			   +"<p> Price:[[price]] <p>"
			   +"<p> Payment Type:[[paymentType]]</p>";
	
	public Boolean sendMailForProductOrder(ProductOrder order,String  status) throws Exception{
		
		//code for mail sending process
				MimeMessage message=mailSender.createMimeMessage();
				MimeMessageHelper helper=new MimeMessageHelper(message);
				
				helper.setFrom("nepalkishor21@gmail.com","Shooping Cart");
				helper.setTo(order.getOrderAddress().getEmail());
				
				/*
				 * //This is for replacing the order success or cancel msg OrderStatus[]
				 * values=OrderStatus.values(); for(OrderStatus status:values) {
				 * if(status.getId().equals(statusCode)) { msg=msg.replace("[[orderStatus]]",
				 * status.getName()); } }
				 */
				
				//This is for replacing or getting product details dynamically in every product order
				msg=msg.replace("[[Name]]",order.getOrderAddress().getFirstName());
				msg=msg.replace("[[orderStatus]]", status);
			    msg=msg.replace("[[productName]]", order.getProduct().getTitle());
			    msg=msg.replace("[[category]]", order.getProduct().getCategory());
			    msg=msg.replace("[[quantity]]", order.getQuantity().toString());
			    msg=msg.replace("[[price]]", order.getPrice().toString());
			    msg=msg.replace("[[paymentType]]", order.getPaymentType());
				
				
				helper.setSubject("Product Ordered Status");
				helper.setText(msg,true);
				
				mailSender.send(message);
				return true;
	}
}
