package websvc.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.dao.BaseDao;
import common.utils.MapUtil;
import common.utils.SpringContextHelper;
import common.utils.SubmitForm;
import framework.config.ConfigReader;


public class ItfGuaranteetask implements Runnable {
	private static final String logType="【担保定时发送回应参数】";
	private static boolean isRunning=false;
	
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public void run() {
		if (isRunning){
			return;
		}
		isRunning=true;
		
		try {
			String sql="select * ";
			sql+="from t_itf_guaranteetask a ";
			sql+="where a.stat='S0A' and a.next_runtime<=sysdate and a.end_time>=sysdate ";
			sql+="order by a.runtimes asc";
			
			List<Map> list = DAO.queryForList(sql);
			
			try {
				if (list!=null&&list.size()>0) {
					System.out.println(logType+"开始。");
					System.out.println(logType+"任务数："+list.size());
					SubmitForm sform=new SubmitForm();
					for (Map task:list) {
						try {
							String url=MapUtil.s(task,"URL");
							String param=MapUtil.s(task,"PARAM");
							String expectedValue=MapUtil.s(task,"EXPECTED_VALUE");
							Integer runtimes=MapUtil.i(task,"RUNTIMES");
							Long taskId=MapUtil.l(task,"TASK_ID");
							
							System.out.println(logType+"任务ID："+taskId+"第"+(runtimes+1)+"次运行开始。");
							
							sql="update t_itf_guaranteetask a set a.stat='S0W' ";
							sql+="where a.stat='S0A' and a.task_id="+taskId;
							int flag = DAO.update(sql);
							
							if (flag<1) {
								System.out.println(logType+"将任务ID："+taskId+"状态更新为运行中出错。");
							}
							
							sform.setStrUrl(url);
							sform.submitForm(param);
							if (sform.getResponseStr().trim().equals(expectedValue)) {
								sql="update t_itf_guaranteetask a ";
								sql+="set a.stat='S0C',a.last_runtime=sysdate,a.runtimes=a.runtimes+1,a.next_runtime=null ";
								sql+="where a.stat='S0W' and a.task_id="+taskId;
							} else {
								sql="update t_itf_guaranteetask a ";
								sql+="set a.stat='S0A',a.last_runtime=sysdate,a.runtimes=a.runtimes+1,";
								sql+="a.next_runtime=a.next_runtime+1/48 ";
								sql+="where a.stat='S0W' and a.task_id="+taskId;
							}
							
							flag = DAO.update(sql);
							if (flag<1) {
								System.out.println(logType+"任务ID："+taskId+"运行完毕后状态更新出错。");
							}
							
							if((runtimes+1)>=5){
								sql="update t_itf_guaranteetask a ";
								sql+="set a.stat='S0Z'";
								sql+="where a.task_id="+taskId;
							}
							flag = DAO.update(sql);
							if (flag<1) {
								System.out.println(logType+"任务ID："+taskId+"运行次数超过5次不再执行。");
							}
							
							System.out.println(logType+"任务ID："+taskId+"第"+(runtimes+1)+"次运行结束。");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
						}
					}
					
					sql="update t_itf_guaranteetask a ";
					sql+="set a.stat='S0Z',a.next_runtime=null ";
					sql+="where a.stat='S0A' and a.next_runtime>a.end_time";
					DAO.update(sql);
					System.out.println(logType+"结束。");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		isRunning=false;
	}
	
	@SuppressWarnings({"unchecked","rawtypes"})
	private List<Map> getListMap(Connection con,String sql) throws Exception {
		Statement stmt=null;
		ResultSet rs=null;
		try{
			List<Map> list=new ArrayList<Map>();
			Map map=null;
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			while(rs.next()){
				String strColName="";
				Object Value="";
				ResultSetMetaData rsmd=rs.getMetaData();
				map=new HashMap();
				for (int i=0;i<rsmd.getColumnCount();i++) {
					strColName=rsmd.getColumnName(i+1);
					Value=(rs.getObject(i+1));
					map.put(strColName.toUpperCase(),Value);
				}
				list.add(map);
			}
			return list;
		}
		catch(Exception e){
			System.out.println("getListMap使用"+sql+"查询出错");
			throw e;
		}
		finally{
			closeStatement(stmt);
			closeResultSet(rs);
		}
	}
	
	private void closeStatement(Statement stmt){
		if (stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private void closeResultSet (ResultSet rs){
		if (rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String args[]) {
//		String flag = ConfigReader.readConfig("/profile/timerTask.properties").getProperty("ItfGuaranteetask.run");
//		System.out.println(flag);
	}
}
