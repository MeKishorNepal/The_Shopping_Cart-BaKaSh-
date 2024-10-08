package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	/*
	 * private UserService userS = null;
	 * 
	 * public UserController( UserService s) { this.userS=s; }
	 */
	
	  @Autowired private UserService userService;
	
	
	@Autowired
	private CategoryService catService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	PasswordEncoder passwordEncoder; 
	
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
	public String home() {
	return 	"user/home";
			
	}
	
	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid,@RequestParam Integer uid, HttpSession session) {
		Cart saveCart=cartService.saveCart(pid, uid);
		if(ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg","Product add to cart failed");
		}else {
			session.setAttribute("succMsg","Product added to cart ");
		}
		return "redirect:/product/"+pid;
	}
	
	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
	
		UserDtls user=commonUtil.getLoggedInUserDetails(p);
		List<Cart> carts=cartService.getCartByUser(user.getId());
		m.addAttribute("carts",carts);
		
		if(carts.size()>0) {
		Double totalOrderPrice=carts.get(carts.size()-1).getTotalOrderPrice();
		m.addAttribute("totalOrderPrice",totalOrderPrice);
		}
		
		return "user/cart";
	}

    @GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy,@RequestParam Integer cid) {
		
    	cartService.updateQuantity(sy,cid);
    	return "redirect:/user/cart";
	}
	
	
	
	

	
	
	@GetMapping("/orders")
	public String orderPage(Principal p,Model m) {
		UserDtls user=commonUtil.getLoggedInUserDetails(p);
		List<Cart> carts=cartService.getCartByUser(user.getId());
		m.addAttribute("carts",carts);
		
		if(carts.size()>0) {
		Double OrderPrice=carts.get(carts.size()-1).getTotalOrderPrice();
		Double totalOrderPrice=carts.get(carts.size()-1).getTotalOrderPrice()+250+100;
		m.addAttribute("orderPrice",OrderPrice);
		m.addAttribute("totalOrderPrice",totalOrderPrice);
		}
		return "/user/order";
	}
	
	@PostMapping("/save_orders")
	public String saveOrder(@ModelAttribute OrderRequest request,Principal p) throws Exception {
		
		//System.out.println(request);
		UserDtls user=commonUtil.getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), request);
		
		
		
		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String successPage() {
		return "/user/success";
	}
	
	@GetMapping("/userOrders")
	public String myOrder(Model m,Principal p) {
		  UserDtls loginUser=commonUtil.getLoggedInUserDetails(p);
		
		List<ProductOrder> orders=orderService.getOrdersByUser(loginUser.getId());
		
		m.addAttribute("orders",orders);
		return "/user/my_orders";
	}
	
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id,@RequestParam Integer st,HttpSession session) {
		
		OrderStatus[] values=OrderStatus.values();
		String status=null;
		
		for(OrderStatus orderSt:values) {
			if(orderSt.getId().equals(st)) {
				status=orderSt.getName();
			}	
		}
		ProductOrder updateOrder=orderService.updateOrderStatus(id, status);
		
		//When customer cancel the order then cancel message send in mail
		try {
		commonUtil.sendMailForProductOrder(updateOrder, status);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		if(!ObjectUtils.isEmpty(updateOrder)) {
		session.setAttribute("succMsg", "Status Updated");
		}
	else {
		session.setAttribute("errorMsg","Status not updated");
	}
		return "redirect:/user/userOrders";
	}
	
	
	
	@GetMapping("/profile")
	public String profile() {
		
		
		return "/user/profile";
	}
	
	
	@PostMapping("/update-profile")
	public String updateUserProfile(@ModelAttribute UserDtls user,@RequestParam MultipartFile image,HttpSession session) {
		
		UserDtls updateUserProfile = userService.updateUserProfile(user, image);
		
		if(!ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("succMsg","Profile update Successfull");
			
		}else {
			session.setAttribute("succMsg","Profile is not update");
		}
		
		return "redirect:/user/profile";
	}
	
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword,@RequestParam String currentPassword,Principal p,HttpSession session) {
		
		UserDtls loggedInUserDetails=commonUtil.getLoggedInUserDetails(p);
		
		Boolean matches=passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
		
		if(matches) {
			String encodePassword=passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser=userService.updateUser(loggedInUserDetails);
			if(ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg","Password not change try again..");
				
			}else {
				session.setAttribute("succMsg","Password change successfully");
			}
			
		}else {
			session.setAttribute("errorMsg","Current Password is Incorrect");
		}
		return "redirect:/user/profile";
	}
	
	
	
	
	
	
}
