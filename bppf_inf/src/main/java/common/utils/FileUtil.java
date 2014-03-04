package common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.config.PayWapConfig;

public class FileUtil {
	
//	public static String PAGE_PARAM_PATH="/home/bppf_inf/pageparam/";
//	public static String PAGE_PARAM_PATH="./../pageparam/";
	public static String PAGE_PARAM_PATH=PayWapConfig.getPAGE_PARAM_PATH();
	
	/**
	 * 读取文件，并保存到List每行为一单元 
	 */
	public static List<String> readFile(String filePath) {
		BufferedReader br=null;
		String str="";
		List<String> list=new ArrayList<String>();
		try {
			File file = new File(filePath);
			if(!file.exists()) file.createNewFile();
			InputStreamReader isr = new InputStreamReader(    
			        new FileInputStream(filePath),"GBK");    
			  br = new BufferedReader(isr);   
			while ((str=br.readLine())!=null) {
				list.add(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br!=null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	
	public static void writeFile(String filePath,String str) {
		try {   
            File file = new File(filePath);
            OutputStreamWriter osw = new OutputStreamWriter(new  FileOutputStream(file), "GBK");  
            osw.write(str);
            osw.close(); 
        } catch (IOException e) {
            e.printStackTrace();   
        }   
	}
	
	public static String createPath(String basePath,String subPath) {
		String fullPath=basePath+"/"+subPath.replace(".", "/");
		if (!new File(fullPath).exists()) { 
			new File(fullPath).mkdirs();
		}
		return fullPath;
	}
	
	public static Map getPageparams(String websvrcode) {
		List<String> list=readFile(PAGE_PARAM_PATH+websvrcode);
		Map map=new HashMap();
		if (list!=null&&list.size()>0) {
			for(int i= 0;i<list.size();i++){
				String str = (String)list.get(i);
				if(str!=null){
					int j = str.indexOf("=");
					if(j>0)
					map.put(str.substring(0,j),str.substring(j+1));
				}
			}
				
			}
		
		return map;
	}
	
	public static void main(String args[]) {
		String pro = System.getenv("CONFIG_HOME");
		System.out.println(pro);
	}
}
