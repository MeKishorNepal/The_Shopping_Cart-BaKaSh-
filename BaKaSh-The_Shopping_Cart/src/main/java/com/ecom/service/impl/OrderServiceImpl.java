package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepo;
import com.ecom.repository.ProductOrderRepo;
import com.ecom.service.OrderService;
import com.ecom.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private ProductOrderRepo orderRepo;
	
	@Autowired
	private CartRepo cartRepo;

	@Override
	public void saveOrder(Integer userid,OrderRequest orderRequest) {
		
		List<Cart>carts=cartRepo.findByUserId(userid);
		
		for(Cart cart:carts) {
			ProductOrder order=new ProductOrder();
			
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(new Date());
			
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());
			
			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());  //here now we use that model which is not save in database
			
			OrderAddress address=new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			
			
			order.setOrderAddress(address);
			
			orderRepo.save(order);
			
		}
		
	}

}
