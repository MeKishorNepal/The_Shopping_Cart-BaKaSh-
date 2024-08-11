
$(function(){
	
	/*user register validation start here*/
	var $userRegister=$("#userRegister");
	$userRegister.validate({
		
		rules:{
			name:{
				required:true,
				lettersonly:true	
			},
			email:{
				required:true,
				space:true,
				email:true
				
			},
			mobileNumber:{
				required:true,
				space:true,
				numericOnly:true,
				minlength:10,
				maxlength:12
			},
			password:{
				required:true,
				space:true,
				minlength:5,
				maxlength:15,
				strongPassword:true
			},
			cpassword:{
				required:true,
				space:true,	
				equalTo:'#pass'
			},
			address:{
				required:true,
				all:true
			},
			img:{
				required:true
			}
			
			
			
		},
		messages:{
			name:{
				required:'Name required',
				lettersonly:'invalid Name'
			},
			email:{
				required:'email name must be requried',
				space:'space not allowed',
				email:'Invalid email'
			},
			mobileNumber:{
				required:'mobile Number must be required',
				space:'space not allowed',
				numericOnly:'invalid mobile number',
				minlength:'min 10 digit',
				maxlength:'max 12 digit'
			},
			password:{
				required:'passwrod must be required',
				space:'spacce not allowed',
				minlength:'weak password',
				strongPassword:'add upper,lower and special letters'
			},
			cpassword:{
				required:'confirm password must be required',
				space:'space not allowed',
				equalTo:'password mismatch'
				
			},
			address:{
				required:'address must be required',
				all:'invalid'
			},
			img:{
				required:'add Profile Picture'
			}
			
			
			
		}
		
	});
	/*Reset password validation*/

var $resetPass=$("#resetPassword");
	$resetPass.validate({
		
		rules:{
			password:{
				required:true,
				space:true,
				minlength:5,
				maxlength:15,
				strongPassword:true
			},
			cpassword:{
				required:true,
				space:true,	
				equalTo:'#pass'
			}
			},
			messages:{
				password:{
				required:'passwrod must be required',
				space:'spacce not allowed',
				minlength:'weak password',
				strongPassword:'add upper,lower and special letters'
			},
			cpassword:{
				required:'confirm password must be required',
				space:'space not allowed',
				equalTo:'password mismatch'
				
			 }
			}
			
			});
			
				/*orders validation*/

var $order=$("#orders");
	$order.validate({
		
		rules:{
			firstName:{
				required:true,
				lettersonly:true	
			},
			lastName:{
				required:true,
				lettersonly:true	
			},
			email:{
				required:true,
				space:true,
				email:true
				
			},
			mobileNumber:{
				required:true,
				space:true,
				numericOnly:true,
				minlength:10,
				maxlength:12
			},address:{
				required:true,
				all:true
			},
			paymentType:{
				required:true
			}
			},
			messages:{
			firstName:{
				required:'First Name required',
				lettersonly:'invalid Name'
			},
			lastName:{
				required:'Last Name required',
				lettersonly:'invalid Name'
			},
			email:{
				required:'email name must be requried',
				space:'space not allowed',
				email:'Invalid email'
			},
			mobileNumber:{
				required:'mobile Number must be required',
				space:'space not allowed',
				numericOnly:'invalid mobile number',
				minlength:'min 10 digit',
				maxlength:'max 12 digit'
			},
			
			address:{
				required:'address must be required',
				all:'invalid'
			},
			paymentType:{
				required:'select payment type'
			}
			}
			
			});
			
			
			
		/*	Add Admin page validation*/
			var $addAdmin=$("#adminPageValidation");
	$addAdmin.validate({
		
		rules:{
			name:{
				required:true,
				lettersonly:true	
			},
			
			email:{
				required:true,
				space:true,
				email:true
				
			},
			mobileNumber:{
				required:true,
				space:true,
				numericOnly:true,
				minlength:10,
				maxlength:12
			},address:{
				required:true,
				all:true
			},
			password:{
				required:true,
				space:true,
				minlength:5,
				maxlength:15,
				strongPassword:true
			},
			cpassword:{
				required:true,
				space:true,	
				equalTo:'#pass'
			}
			},
			
	
			messages:{
			name:{
				required:' Name required',
				lettersonly:'invalid Name'
			},
			
			email:{
				required:'email name must be requried',
				space:'space not allowed',
				email:'Invalid email'
			},
			mobileNumber:{
				required:'mobile Number must be required',
				space:'space not allowed',
				numericOnly:'invalid mobile number',
				minlength:'min 10 digit',
				maxlength:'max 12 digit'
			},
			
			address:{
				required:'address must be required',
				all:'invalid'
			},
			password:{
				required:'passwrod must be required',
				space:'spacce not allowed',
				minlength:'weak password',
				strongPassword:'add upper,lower and special letters'
			},
			cpassword:{
				required:'confirm password must be required',
				space:'space not allowed',
				equalTo:'password mismatch'
				
			 }
			
			}
			
			}); 
			
			
			
			
			/*Add Product Validation */
			
			var $addProduct=$("#addProductValidation");
	$addProduct.validate({
		
		rules:{
			title:{
				required:true,
				lettersonly:true	
			},
			description:{
				required:true,
				wordCount:[5,8]
				
			},
			category:{
				required:true,
				
			},
			price:{
				required:true,
				space:true,
				numericOnly:true
			},
			stock:{
				required:true,
				space:true,
				numericOnly:true
			},
			file:{
				required:true,
			},
		
		},
		messages:{
			title:{
				required:'title needed',
				lettersonly:'invalid title'
			},
			description: {
				required: 'Write short description',
				wordCount:'Description must be between 5 and 8 words'
			},

			category:{
				required:'category needed',
			},
			price:{
				required:'price needed',
				space:'spacce not allowed',
				numericOnly:'Numbers Only'
				
			},
			stock:{
			     required:'stock needed',
				space:'spacce not allowed',
				numericOnly:'Numbers Only'
				
			},
			file:{
				required:'product image needed',
			
			}
		}
		
	});
			
	
	
})


	

jQuery.validator.addMethod('lettersonly',function(value, element){
	return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
});


jQuery.validator.addMethod('space',function(value, element){
	return /^[^-\s]+$/.test(value);
});


jQuery.validator.addMethod('all',function(value, element){
	return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
});


jQuery.validator.addMethod('numericOnly',function(value, element){
	return /^[0-9]+$/.test(value);
});

jQuery.validator.addMethod('email', function(value, element) {
    return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9._%+-]*@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(value);
});



jQuery.validator.addMethod('strongPassword', function(value, element) {
    return this.optional(element) || value.length >= 8 && /[a-z]/.test(value) && /[A-Z]/.test(value) && /[0-9]/.test(value) && /[\W]/.test(value);
}, 'Your password is not strong enough.');



$.validator.addMethod("wordCount", function(value, element, params) {
    let words = value.trim().split(/\s+/);
    return words.length >= params[0] && words.length <= params[1];
}, "Description must be between {0} and {1} words.");


