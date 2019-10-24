package com.myschool.dao.inter;

import com.myschool.bean.Exam;

import java.util.List;
import java.util.Map;


public interface ScoreDaoInter extends BaseDaoInter {
	
	List<Map<String, Object>> getScoreList(Exam exam);
	
}
