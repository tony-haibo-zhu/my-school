package com.myschool.dao.inter;

import com.myschool.bean.Student;

import java.util.List;

public interface StudentDaoInter extends BaseDaoInter {
	
	List<Student> getStudentList(String sql, List<Object> param);
	
}
