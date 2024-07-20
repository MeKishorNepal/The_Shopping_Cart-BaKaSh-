package com.ecom.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRequest {
	private String firstName;
	private String lastName;
	private String email;
	private String mobileNo;
	private String address;
	private String paymentType;

}
