package com.myschool.dao.inter;

import com.myschool.bean.Clazz;
import com.myschool.bean.Grade;
import com.myschool.bean.Teacher;

import java.util.List;

public interface TeacherDaoInter extends BaseDaoInter {
	
	List<Teacher> getTeacherList(String sql, Object[] param, Grade grade, Clazz clazz);
	
}
