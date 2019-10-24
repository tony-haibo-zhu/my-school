package com.myschool.dao.inter;

import com.myschool.bean.Clazz;
import com.myschool.bean.Page;

import java.util.List;


public interface ClazzDaoInter extends BaseDaoInter {
	
	List<Clazz> getClazzDetailList(String gradeid, Page page);
	
}
