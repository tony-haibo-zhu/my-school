package com.myschool.bean;

public class User {
	
	public static final int USER_ADMIN = 1;
	
	public static final int USER_STUDENT = 2;
	
	public static final int USER_TEACHER = 3;
	
	private int id; //ID
	
	private String account; //账户
	
	private String password = "111111"; //密码：默认'111111'
	
	private String name; //用户姓名
	
	private int type = USER_STUDENT; // 账户类型：默认2为学生；1为管理员，2为学生，3为教师

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
