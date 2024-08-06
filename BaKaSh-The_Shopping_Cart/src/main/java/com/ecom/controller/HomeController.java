package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	CategoryService catService;
	
	@Autowired
	ProductService proService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
     CartService cartService;
	
	
	//Whenever request come then automatically called this method
	@ModelAttribute
	public void getUserDetails(Principal p,Model m) {
		if(p!=null) {
			String email=p.getName();
			UserDtls  userDtls=userService.getUserByEmail(email);
			m.addAttribute("user",userDtls);
			//This is for to count how many product select by user 
			Integer countCart=cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart",countCart);
		}
		
		List<Category> allActiveCategory=catService.getAllActiveCategory();
		m.addAttribute("categorys",allActiveCategory);
		
		
	}
	
	@GetMapping("/")
	public String index() {
		
		return "index";
	}
	
	
	@GetMapping("/signin")
	public String login() {
		
		return "login";
	}

	
	@GetMapping("/register")
	public String register() {
		
		return "register";
	}
	
	@GetMapping("/products")
	public String products(Model m,@RequestParam(value="category",defaultValue="") String category,
			@RequestParam (name="pageNo",defaultValue="0")Integer pageNo,
			@RequestParam(name="pageSize",defaultValue="3") Integer pageSize) {
		System.out.println("category= "+ category);
		
		List<Category> categories=catService.getAllActiveCategory();
		m.addAttribute("categories",categories);
		m.addAttribute("paramValue",category);
		
		
		/*
		 * List<Product> products=proService.getAllActiveProducts(category);
		 * m.addAttribute("products",products);
		 */
		//here we apply pagination concept 
		
		Page<Product>page=proService.getAllActiveProductPagination(pageNo, pageSize,category);
		List<Product> products=page.getContent();
         m.addAttribute("products",products);
         
         m.addAttribute("productsSize",products.size());
         
         m.addAttribute("pageNo",page.getNumber());
         m.addAttribute("pageSize",pageSize);
         m.addAttribute("totalElements",page.getTotalElements());
         m.addAttribute("totalPages",page.getTotalPages());
         m.addAttribute("isFirst",page.isFirst());
         m.addAttribute("isLast",page.isLast());
		
		return "product";
	}
	
	@GetMapping("/product/{id}")
	public String viewproducts(@PathVariable int id, Model m) {
		
		Product productById=proService.getProductById(id);
		m.addAttribute("p",productById);
		return "view_product";
	}
	
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		
		String imageName=file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		
		UserDtls saveUser=userService.saveUser(user);
		
		if(!ObjectUtils.isEmpty(saveUser)) {
			if(!file.isEmpty()) {
				
					//here we will get the class path
					File saveFile=new ClassPathResource("static/img").getFile();
					
					Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+"profile_img"+File.separator+file.getOriginalFilename());
					System.out.println(path);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			
			session.setAttribute("succMsg","Saved Successfully");
	
		}else {
			session.setAttribute("errorMsg","Something wrong on server");
		}
		return "redirect:/register";
	}
	

	//Forgot password logic
	@GetMapping("/forgot-password")
	public String showForgotPassword(Model m) {
		m.addAttribute("message","Please enter your email to reset your password");
		return "forgot_password";
		
	}
	
	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email,HttpSession session,HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		UserDtls userByEmail=userService.getUserByEmail(email);
		
		
		if(ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg","Invalid email") ;
			
		}else {
			
			String resetToken=UUID.randomUUID().toString();
			userService.updateUserResetToken(email,resetToken);
			
			
			//Generate URL:http://localhost:8080/reset-passwrod?token=sldfskfsdlfjsdfsfsdf
			
			String url=CommonUtil.generateUrl(request)+"reset-password?token="+resetToken;
			
			//System.out.println(url);
			
			
			Boolean sendEmail=commonUtil.sendMail(url,email);
			if(sendEmail) {
				session.setAttribute("succMsg", "Please check your email..Passwrod reset link is sent");
			}else {
				session.setAttribute("errorMsg", "Something wrong on server..mail not send");
			}
		
		}
		
		return "redirect:/forgot-password";
	}
	
	
	@GetMapping("/reset-password")
	public String showResetPassword(Model m,@RequestParam String token,HttpSession session) {
		m.addAttribute("message","enter your new password");
		
		UserDtls userByToken=userService.getUserByToken(token);
		
		if(ObjectUtils.isEmpty(userByToken)) {
			m.addAttribute("msg","Your link is invalid or expired");
			return "message";
		}
		
		m.addAttribute("token",token);
		return "reset_password";
	
	}
	
	
	
	
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token,@RequestParam String password,HttpSession session,Model m) {
		
		UserDtls userByToken=userService.getUserByToken(token);
		
		if(ObjectUtils.isEmpty(userByToken)) {
			m.addAttribute("errorMsg","Your link is invalid or expired");
			return "message";
		}else {
			
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			
			
			m.addAttribute("msg","Passwrod changed successfully");
			return "message";
			
		}
		
	
	}
	
	@GetMapping("/search-product")
	public String searchProduct(@RequestParam String ch,Model m) {
		
		List<Product>searchProduct=proService.searchProduct(ch);
		m.addAttribute("products",searchProduct);
		
		List<Category> categories=catService.getAllActiveCategory();
		
		
		m.addAttribute("categories",categories);
		
		
		return "product";
	}



}
