package com.myschool.dao.inter;

import com.myschool.bean.User;

import java.io.InputStream;


public interface PhotoDaoInter {
	
	void setPhoto(User user, InputStream is) throws Exception;
	
	InputStream getPhoto(User user);
}
