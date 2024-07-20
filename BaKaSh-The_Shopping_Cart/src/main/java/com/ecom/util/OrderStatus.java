package com.ecom.util;

public enum OrderStatus {
	
	IN_PROGRESS(1,"In Progress"),
	ORDER_RECEIVED(2,"Order received"),
	PRODUCT_PACKED(3,"Product Packed"),
	OUT_FOR_DELIVERY(4,"Out for delivery"),
	ORDER_DELIVERED(5,"Order delivered");
	
	private Integer id;
	private String name;
	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	

}
