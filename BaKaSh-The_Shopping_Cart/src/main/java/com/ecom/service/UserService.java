package com.ecom.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.UserDtls;

public interface UserService {
	
	public UserDtls saveUser(UserDtls user);
	
	public UserDtls getUserByEmail(String email);
	
	public List<UserDtls> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);

	void increaseFailedAttempt(UserDtls user);

	void userAccountLock(UserDtls user);

	boolean unlockAccountTimeExpired(UserDtls user);

	void resetAttempt(int userId);

	public void updateUserResetToken(String email, String resetToken);

	public UserDtls getUserByToken(String token);
	
	public UserDtls updateUser(UserDtls user);
	
	public UserDtls updateUserProfile(UserDtls user,MultipartFile image);
	
	public UserDtls saveAdmin(UserDtls user);
}
