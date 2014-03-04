package common.utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import common.dao.MyDao;
import common.entity.TSymDataformat;

public class ParamValid {

	public String Valid(Document doc,String objName) throws Exception {
		
		
		String sql="select * from t_sym_dataformat where object_type='WEBSERVICE' and object_name='"+objName+"'";
		List<TSymDataformat> list=MyDao.list(sql,TSymDataformat.class);
		
		if (list!=null&&list.size()>0) {
			for (TSymDataformat format:list) {
				String fieldName=format.getFieldName();
				String value=getNodeText(doc,fieldName);
				if (value==null||value.trim().length()<1) {
					if (format.getIsRequired()==1) {
						return ("字段:"+fieldName+"不能为空");
					}
					continue;
				}
				
				if (format.getIsRequired()==1) {
					if (value.length()<1) {
						return ("字段:"+fieldName+"不能为空");
					}
				}
				if (format.getMaxLength()>0) {
					if (value.length()>format.getMaxLength()) {
						return ("字段:"+fieldName+"长度超过"+format.getMaxLength());
					}
				}
				if (format.getMinLength()>0) {
					if (value.length()<format.getMinLength()) {
						return ("字段:"+fieldName+"长度小于"+format.getMinLength());
					}
				}
				if (!isEmpty(format.getRegular())) {
					for (String r:format.getRegular().split(";")){
						Pattern pattern = Pattern.compile(r);
						boolean flag = pattern.matcher(value).matches();
						if (!flag) {
							return ("字段:"+fieldName+"格式不正确");
						}
					}
				}
				if (!isEmpty(format.getEq())) {
					if (!value.equals(format.getEq())) {
						return ("字段:"+fieldName+"不等于"+format.getEq());
					}
				}
				if (!isEmpty(format.getLe())) {
					boolean flag=false;
					if (!isEmpty(format.getDataType())&&format.getDataType().equals("I")) {
						if (!(ValueUtil.l(value)<=ValueUtil.l(format.getLe()))) {
							flag=true;
						}
					} else {
						if (!(value.compareTo(format.getDataType())<=0)) {
							flag=true;
						}
					}
					if (flag) {
						return ("字段:"+fieldName+"不小于等于"+format.getLe());
					}
				}
				if (!isEmpty(format.getDataFormat())) {
					if (!isEmpty(format.getDataType())&&format.getDataType().equals("D")) {
						if (!(validDate(value,format.getDataFormat()))) {
							return ("字段:"+fieldName+"日期格式不符合要求");
						}
					}
				}
			}
		}
		
		return "0";
	}
	
	protected String getAttr(Element ele, String attrName) {
		return ele.valueOf("@" + attrName);
	}

	protected String getAttrM(Element ele, String attrName) throws Exception {
		String s = getAttr(ele, attrName);
		if (Charset.isEmpty(s))
			throw new Exception(attrName + "不能为空");
		return s;
	}

	protected String getNodeText(Document doc, String nodeName) {
		Element e = (Element) doc.selectSingleNode("//" + nodeName);
		return (e == null) ? "" : e.getTextTrim();
	}

	protected Node getNodeM(Document doc, String nodeName) throws Exception {
		Node n = doc.selectSingleNode("//" + nodeName);

		if (n == null) {
			throw new Exception(nodeName + "不能为空");
		}

		return n;
	}

	protected String getNodeTextM(Document doc, String nodeName)
			throws Exception {
		Element e = (Element) getNodeM(doc, nodeName);

		if ("".equals(e.getTextTrim())) {
			throw new Exception(nodeName + "不能为空");
		}

		return e.getTextTrim();
	}
	
	private static boolean isEmpty(Object o) {
		if (o==null) {
			return true;
		}
		if (o instanceof String) {
			if (((String) o).trim().length()<1) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean validDate(String d,String format) {
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		
		try {
			if (sdf.format(sdf.parse(d)).equals(d)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
