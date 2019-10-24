package com.myschool.service;

import com.myschool.bean.Course;
import com.myschool.bean.CourseItem;
import com.myschool.bean.Exam;
import com.myschool.bean.Page;
import com.myschool.bean.Student;
import com.myschool.bean.Teacher;
import com.myschool.dao.impl.ExamDaoImpl;
import com.myschool.dao.impl.StudentDaoImpl;
import com.myschool.dao.inter.ExamDaoInter;
import com.myschool.tools.MysqlTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExamService {
	
	private ExamDaoInter dao;
	
	public ExamService(){
		dao = new ExamDaoImpl();
	}
	
	/**
	 * 获取考试信息
	 * @param exam 参数
	 * @param page 分页
	 * @return
	 */
	public String getExamList(Exam exam, Page page) {
		//sql语句
		StringBuffer sb = new StringBuffer("SELECT * FROM exam ");
		//参数
		List<Object> param = new LinkedList<>();
		//判断条件
		if(exam != null){ 
			if(exam.getGradeid() != 0){//条件：年级
				int gradeid = exam.getGradeid();
				param.add(gradeid);
				sb.append("AND gradeid=? ");
			}
			if(exam.getClazzid() != 0){
				int clazzid = exam.getClazzid();
				param.add(clazzid);
				sb.append("AND clazzid=? ");
			}
		}
		//添加排序
		sb.append("ORDER BY id DESC ");
		//分页
		if(page != null){
			param.add(page.getStart());
			param.add(page.getSize());
			sb.append("LIMIT ?,?");
		}
		String sql = sb.toString().replaceFirst("AND", "WHERE");
		//获取数据
		List<Exam> list = dao.getExamList(sql, param);
		//获取总记录数
		long total = getCount(exam);
		//定义Map
		Map<String, Object> jsonMap = new HashMap<String, Object>();  
		//total键 存放总记录数，必须的
        jsonMap.put("total", total);
        //rows键 存放每页记录 list 
        jsonMap.put("rows", list); 
        //格式化Map,以json格式返回数据
        String result = JSONObject.fromObject(jsonMap).toString();
        //返回
		return result;
	}
	
	/**
	 * 获取记录数
	 * @param exam
	 * @return
	 */
	private long getCount(Exam exam){
		//sql语句
		StringBuffer sb = new StringBuffer("SELECT COUNT(*) FROM exam ");
		//参数
		List<Object> param = new LinkedList<>();
		//判断条件
		if(exam != null){ 
			if(exam.getGrade() != null){//条件：年级
				int gradeid = exam.getGradeid();
				param.add(gradeid);
				sb.append("AND gradeid=? ");
			}
			if(exam.getClazz() != null){
				int clazzid = exam.getClazzid();
				param.add(clazzid);
				sb.append("AND clazzid=? ");
			}
		}
		String sql = sb.toString().replaceFirst("AND", "WHERE");
		
		long count = dao.count(sql, param).intValue();
		
		return count;
	}
	
	/**
	 * 添加考试
	 * @param exam
	 * @throws Exception
	 */
	public void addExam(Exam exam) throws Exception {
		Connection conn = MysqlTool.getConnection();
		try {
			//开启事务
			MysqlTool.startTransaction();
			
			//添加考试信息
			int examid = dao.insertReturnKeysTransaction(conn, 
					"INSERT INTO exam(name, time, remark, type, gradeid, clazzid, courseid) value(?,?,?,?,?,?,?)", 
					new Object[]{
						exam.getName(), 
						exam.getTime(),
						exam.getRemark(),
						exam.getType(),
						exam.getGradeid(),
						exam.getClazzid(),
						exam.getCourseid()
					});
			
			//添加学生成绩表
			String sql = "INSERT INTO escore(examid,clazzid,studentid,gradeid,courseid) value(?,?,?,?,?)";
			
			if(exam.getType() == Exam.EXAM_GRADE_TYPE){ //年级统考
				
				//查询该年级的课程
				List<Object> couObjList = dao.getList(Course.class,
						"SELECT courseid id FROM grade_course WHERE gradeid=?", 
						new Object[]{exam.getGradeid()});
				
				//查询该年级下的学生
				List<Object> stuList = dao.getList(Student.class,
						"SELECT id, clazzid FROM student WHERE gradeid=?",
						new Object[]{exam.getGradeid()});
				
				//转换类型
				List<Course> couList = new LinkedList<>();
				for(Object obj : couObjList){
					Course course = (Course) obj;
					couList.add(course);
				}
				//批量参数
				Object[][] param = new Object[stuList.size()*couList.size()][5];
				int index = 0;
				for (Object aStuList : stuList) {
					Student student = (Student) aStuList;
					for (Course aCouList : couList) {
						param[index][0] = examid;
						param[index][1] = student.getClazzid();
						param[index][2] = student.getId();
						param[index][3] = exam.getGradeid();
						param[index][4] = aCouList.getId();

						index++;
					}
				}
				//批量添加学生考试表
				dao.insertBatchTransaction(conn, sql, param);
				
			} else{  //平时考试
				
				//查询该班级下的学生
				List<Object> stuList = dao.getList(Student.class, 
						"SELECT id FROM student WHERE clazzid=?",
						new Object[]{exam.getClazzid()});
				
				//批量参数
				Object[][] param = new Object[stuList.size()][5];
				for(int i = 0;i < stuList.size();i++){
					Student student = (Student) stuList.get(i);
					param[i][0] = examid;
					param[i][1] = exam.getClazzid();
					param[i][2] = student.getId();
					param[i][3] = exam.getGradeid();
					param[i][4] = exam.getCourseid();
				}
				//批量添加学生考试表
				dao.insertBatchTransaction(conn, sql, param);
			}
			
			//提交事务
			MysqlTool.commit();
		} catch (Exception e) {
			//回滚事务
			MysqlTool.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			MysqlTool.closeConnection();
		}
	}
	
	/**
	 * 删除考试
	 * @param ids 
	 * @throws Exception 
	 */
	public void deleteExam(int id) throws Exception{
		//获取连接
		Connection conn = MysqlTool.getConnection();
		//开启事务
		MysqlTool.startTransaction();
		try {
			//删除成绩表
			dao.deleteTransaction(conn, "DELETE FROM escore WHERE examid=?", new Object[]{id});
			//删除考试
			dao.deleteTransaction(conn, "DELETE FROM exam WHERE id =?", new Object[]{id});
			
			//提交事务
			MysqlTool.commit();
		} catch (Exception e) {
			//回滚事务
			MysqlTool.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			MysqlTool.closeConnection();
		}
	}

	/**
	 * 获取某老师的考试
	 * @param id
	 * @return
	 */
	public String teacherExamList(String number) {
		//获取教师信息
		Teacher teacher = new TeacherService().getTeacher(number);
		
		List<CourseItem> itemList = teacher.getCourseList();
		if(itemList.size() == 0){
			return "";
		}
		StringBuilder g = new StringBuilder();
		StringBuilder c = new StringBuilder();
		for(CourseItem item : itemList){
			g.append(",").append(item.getGradeid());
			c.append(",").append(item.getCourseid());
		}

		//sql语句
		String sql = "SELECT * FROM exam WHERE (gradeid IN (" + g.toString().replaceFirst(",", "") +
				") AND type=1) OR (courseid IN (" +
				c.toString().replaceFirst(",", "") +
				") AND type=2)";
		//获取数据
		List<Exam> list = dao.getExamList(sql, null);
		
        //格式化Map,以json格式返回数据
		//返回
		return JSONArray.fromObject(list).toString();
	}
	
	/**
	 * 获取某个学生考试列表
	 * @param number
	 * @return
	 */
	public String studentExamList(String number) {
		
		//获取学生详细信息
		Student student = new StudentDaoImpl().getStudentList("SELECT * FROM student WHERE number="+number, null).get(0);
		
		String sql = "SELECT * FROM exam WHERE (gradeid=? AND type=1) OR (clazzid=? AND type=2)";
		
		List<Object> param = new LinkedList<>();
		param.add(student.getGradeid());
		param.add(student.getClazzid());
		
		//获取数据
		List<Exam> list = dao.getExamList(sql, param);
		
		//格式化Map,以json格式返回数据

		return JSONArray.fromObject(list).toString();
	}
	
}
