package com.ecom.controller;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private CategoryService catService;
	
	
	@Autowired
	private ProductService proService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	

	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

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
		
	}
	
	
	@GetMapping("/")
	public String index() {
		
		return "admin/index";
	}
	
	@GetMapping("/loadProduct")
	public String addproduct(Model m) {
		List<Category> categories=catService.getAllCategory();
		m.addAttribute("categories",categories);
		return "admin/add_product";
	}
	
	@GetMapping("/category")
	public String category( Model m,
			@RequestParam(name="pageNo",defaultValue="0")Integer pageNo,
			@RequestParam(name="pageSize",defaultValue="5")Integer pageSize) {
		
		//m.addAttribute("category",catService.getAllCategory());
		Page<Category>page=catService.getAllCategoryPagination(pageNo, pageSize);
		
		List<Category> category=page.getContent();
        m.addAttribute("category",category);
      
        m.addAttribute("pageNo",page.getNumber());
        m.addAttribute("pageSize",pageSize);
        m.addAttribute("totalElements",page.getTotalElements());
        m.addAttribute("totalPages",page.getTotalPages());
        m.addAttribute("isFirst",page.isFirst());
        m.addAttribute("isLast",page.isLast());
        
		return "admin/category";
	}
	
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
		
		String imageName= file != null?file.getOriginalFilename(): "default.jpg";
		category.setImageName(imageName);
		
		/*
		 * System.out.println(imageName); System.out.println(category.getName());
		 */
		
		Boolean existCategory=catService.existCategory(category.getName());
		  
		if(existCategory) {
			session.setAttribute("errorMsg", "Category already exists");
			
		}
		else {
		Category c=catService.saveCategory(category);
		
		if(ObjectUtils.isEmpty(c)) {
		    session.setAttribute("errorMsg", "Not Saved! Internal server error");
	
		}else {
			
			//here we will get the class path
			File saveFile=new ClassPathResource("static/img").getFile();
			
			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
			System.out.println(path);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			
			 session.setAttribute("succMsg", "Saved Successfully");
		}
		
		}
		return "redirect:/admin/category";
		
	}
	
	
	@GetMapping("/deleteCategory/{id}")
	public String deleteCateogory(@PathVariable int id ,HttpSession session) {
		
		
		Boolean deleteCategory=catService.deleteCategory(id);
		if(deleteCategory) {
			session.setAttribute("succMsg", "category delete success");
			
		}else {
			
			session.setAttribute("errorMsg", "Something wrong on server");
			
		}
		return "redirect:/admin/category";
	}
	
	
	
	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		
		m.addAttribute("category",catService.getCategoryById(id));
	
		
		
		return "/admin/edit_category";
	}
	
	
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file,HttpSession session)throws Exception {
		
		Category oldCats=catService.getCategoryById(category.getId());
		
		String imageName=file.isEmpty() ? oldCats.getImageName():file.getOriginalFilename();
		
		if(!ObjectUtils.isEmpty(oldCats)) {
			
			oldCats.setName(category.getName());
			oldCats.setIsActive(category.getIsActive());
			oldCats.setImageName(imageName);
		
		}
		
		Category updateCats=catService.saveCategory(oldCats);
		
		if(!ObjectUtils.isEmpty(updateCats)) {
			
			if(!file.isEmpty())  {
				//here we will get the class path
				File saveFile=new ClassPathResource("static/img").getFile();
				
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				
				
			}
			
			
			
			session.setAttribute("succMsg", "Category updated success");
			
		}else {
			session.setAttribute("errorMsg", "Something error on server");
		}
		
		
		return "redirect:/admin/loadEditCategory/"+category.getId();
	}
	
	
	
	@PostMapping("/saveProduct")
	public String saveProducts(@ModelAttribute Product product,@RequestParam("file") MultipartFile image,HttpSession session) throws IOException {
		
		String imageName=image.isEmpty()?"default.jpg":image.getOriginalFilename();
		product.setImage(imageName);
		
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		
		Product saveproduct=proService.saveProduct(product);
		
		if(!ObjectUtils.isEmpty(saveproduct)) {
			
			//here we will get the class path
			File saveFilePath=new ClassPathResource("static/img").getFile();
			
			Path Imagepath=Paths.get(saveFilePath.getAbsolutePath()+File.separator+"product_img"+File.separator+image.getOriginalFilename());
			System.out.println(Imagepath);
			Files.copy(image.getInputStream(),Imagepath, StandardCopyOption.REPLACE_EXISTING);
			
			
			
			session.setAttribute("succMsg", "Product save successfully");
			
			
		}else {
			session.setAttribute("errorMsg", "Something error on server.");
		}
		
		
		
		return "redirect:/admin/loadProduct";
	}
		
		
	
    @GetMapping("/productView")
	public String loadProductView(Model m,@RequestParam(defaultValue="") String ch,
			@RequestParam(name="pageNo",defaultValue="0")Integer pageNo,
			@RequestParam(name="pageSize",defaultValue="5")Integer pageSize) {
    	
		/*
		 * here we add our logic means when admin search product then we get all
		 * searchProduct and if when admin nothing search then we get all products
		 */
		/*
		 * List<Product>products=null;
		 * 
		 * if(ch !=null && ch.length()>0) { products=proService.searchProduct(ch);
		 * m.addAttribute("products",products); }else {
		 * 
		 * products=proService.getAllProducts(); } m.addAttribute("products",products);
		 */
    	
    	
    	Page<Product>page=null;
		 
		  if(ch !=null && ch.length()>0) {
		     page=proService.searchProductPagination(pageNo,pageSize,ch);
		  m.addAttribute("products",page); 
		  }else {
		  
		    page=proService.getAllProductsPagination(pageNo,pageSize); 
		  } 
		  m.addAttribute("products",page.getContent());
    	
    	
		
		  m.addAttribute("pageNo",page.getNumber());
		  m.addAttribute("pageSize",pageSize);
		  m.addAttribute("totalElements",page.getTotalElements());
		  m.addAttribute("totalPages",page.getTotalPages());
		  m.addAttribute("isFirst",page.isFirst());
		  m.addAttribute("isLast",page.isLast());
		 
    	
		return "admin/Products_view";
				
	}
    
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
    	Boolean deleteProduct=proService.deleteProduct(id);
    	
    	if(deleteProduct) {
    		session.setAttribute("succMsg","Deleted Success");
    		
    	}else {
    		session.setAttribute("errorMsg", "Something error on server");
    	}
    	return "redirect:/admin/productView";
    	
    	
    }
    
    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model m) {
    	
    	m.addAttribute("product",proService.getProductById(id));
    	m.addAttribute("categories",catService.getAllCategory());
    	return "/admin/Edit_Product";
    }
    
    
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image,HttpSession session ) {
    	
    	//here first we check that user add the discount precentage between 0 to 100
    	//and if add -ve value then show error
    	if(product.getDiscount()<0 || product.getDiscount()>100) {
    		session.setAttribute("errorMsg","Invalid Discount");
    	}else {
    	
    	
    	Product updateProduct=proService.updateProduct(product, image);
    	
    	if(!ObjectUtils.isEmpty(updateProduct)) {
    		
    		session.setAttribute("succMsg", "update Success");
    		
    	}else {
    		
    		session.setAttribute("errorMsg", "Something is invalid");
    	}
    	}
    	return "redirect:/admin/editProduct/"+product.getId();
    }

    
    
    @GetMapping("/users")
    public String getAllUsers(Model m,@RequestParam Integer type) {
    	
    	List<UserDtls>users=null;
    	
    	if(type==1) {
    		users= userService.getUsers("ROLE_USER");
    	}else {
    		users= userService.getUsers("ROLE_ADMIN");
    	}
    	
    	m.addAttribute("userType",type);
    	m.addAttribute("users",users);
    	
    	return "/admin/users";
    	
    }
    
    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status,@RequestParam Integer id,@RequestParam Integer type,HttpSession session) {
    	
    	Boolean f=userService.updateAccountStatus(id,status);
    	if(f) {
    		session.setAttribute("succMsg", "Account Status Updated");
    
    	}else {
    		session.setAttribute("errorMsg", "Something wrong on server");
    	    
    	}
    	return "redirect:/admin/users?type="+type;
    	
    }
    
    @GetMapping("/orders")
    public String  getAllOrders(Model m,
    		@RequestParam(name="pageNo",defaultValue="0")Integer pageNo,
			@RequestParam(name="pageSize",defaultValue="2")Integer pageSize) {
    	
		/*
		 * List<ProductOrder> allOrders=orderService.getAllOrders();
		 * m.addAttribute("orders",allOrders); m.addAttribute("srch",false);
		 */
       	Page<ProductOrder> page=orderService.getAllOrdersPagination(pageNo,pageSize);
       	m.addAttribute("orders",page.getContent());
    	m.addAttribute("srch",false);
		
		 
		  m.addAttribute("pageNo",page.getNumber());
		  m.addAttribute("pageSize",pageSize);
		  m.addAttribute("totalElements",page.getTotalElements());
		  m.addAttribute("totalPages",page.getTotalPages());
		  m.addAttribute("isFirst",page.isFirst());
		  m.addAttribute("isLast",page.isLast());
		 
		 
    	return "/admin/orders";
    }
    
    
    @PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id,@RequestParam Integer st,HttpSession session) {
		
		OrderStatus[] values=OrderStatus.values();
		String status=null;
		
		for(OrderStatus orderSt:values) {
			if(orderSt.getId().equals(st)) {
				status=orderSt.getName();
			}	
		}
		ProductOrder updateOrder=orderService.updateOrderStatus(id, status);
		
		//when admin update status then send mail
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
		return "redirect:/admin/orders";
	}
    
    
	@GetMapping("/search-orderProduct")
	public String searchProduct(@RequestParam String orderId,Model m,HttpSession session,
			@RequestParam(name="pageNo",defaultValue="0")Integer pageNo,
			@RequestParam(name="pageSize",defaultValue="5")Integer pageSize) {
		
		
		if(orderId !=null && orderId.length()>0) {
			ProductOrder order=orderService.getOrderByOrderId(orderId.trim());
			
			if(ObjectUtils.isEmpty(order)){
				session.setAttribute("errorMsg","Incorrect orderid");
				m.addAttribute("orderDtls",null);
				
			}else {
				m.addAttribute("orderDtls",order);
			}
			
			m.addAttribute("srch",true);
			
			
		}else {
			
			/*
			 * List<ProductOrder> allOrders=orderService.getAllOrders();
			 * m.addAttribute("orders",allOrders); m.addAttribute("srch",false);
			 */
			
			Page<ProductOrder> page=orderService.getAllOrdersPagination(pageNo,pageSize);
	    	m.addAttribute("orders",page);
	    	m.addAttribute("srch",false);
	    	
	    	  m.addAttribute("pageNo",page.getNumber());
			  m.addAttribute("pageSize",pageSize);
			  m.addAttribute("totalElements",page.getTotalElements());
			  m.addAttribute("totalPages",page.getTotalPages());
			  m.addAttribute("isFirst",page.isFirst());
			  m.addAttribute("isLast",page.isLast());
	    	
	    	
		}
		
		return "/admin/orders";
		
	}
	
	/* we will do this operation in view product page directly
	 * @GetMapping("/search-product") public String searchProduct(@RequestParam
	 * String ch,Model m) {
	 * 
	 * List<Product>searchProduct=proService.searchProduct(ch);
	 * m.addAttribute("products",searchProduct);
	 * 
	 * 
	 * 
	 * 
	 * return ""; }
	 */
	
	@GetMapping("/add-admin")
    public String loadAdminAdd() {
			
			
			return "/admin/add_admin";
		}
		/*
		 * //from here we done this all in userController and here again we are going to
		 * doing same like module so that we use that code and just modify some part
		 */
	
	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		
		String imageName=file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		
		UserDtls saveAdmin=userService.saveAdmin(user);
		
		if(!ObjectUtils.isEmpty(saveAdmin)) {
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
		return "redirect:/admin/add-admin";
	}
	
	@GetMapping("/profile")
	public String profile() {
		return "/admin/profile";
	}

	
	@PostMapping("/update-profile")
	public String updateUserProfile(@ModelAttribute UserDtls user,@RequestParam("img") MultipartFile img,HttpSession session) {
		
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		
		if(!ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("succMsg","Profile update Successfull");
			
		}else {
			session.setAttribute("succMsg","Profile is not update");
		}
		
		return "redirect:/admin/profile";
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
		return "redirect:/admin/profile";
	}
	



}
