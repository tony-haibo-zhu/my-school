package com.myschool.dao.inter;

import com.myschool.bean.Exam;

import java.util.List;


public interface ExamDaoInter extends BaseDaoInter {
	
	List<Exam> getExamList(String sql, List<Object> param);
	
}
