<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF12038.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("DATE",sdf.format(new Date()));
	pageContext.setAttribute("ORDERSEQ",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
	<script type="text/javascript" src="../js/json.js"></script>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF12038">
			<input type="hidden" name="method" value="inf12038">
			<table>
				<tr>
					<td><font color="red" size="5">预订[火车票]接口</font></td>
				</tr>
				
				<tr><td height="50">校验参数：</td></tr>
				<tr>
					<td>
						MERID：
						<input name="MERID" type="text" size="20" maxlength="50"
							value="${map.MERID}">
					</td>
				</tr>
				
				<tr>
					<td>
						CHANNELCODE：
						<input name="CHANNELCODE" type="text" size="20" maxlength="2"
							value="${map.CHANNELCODE}">
					</td>
				</tr>
				
				<tr>
					<td>
						TMNNUM：
						<input name="TMNNUM" type="text" size="20" maxlength="15"
							value="${map.TMNNUM}">
					</td>
				</tr>
				
			<tr><td height="50">请求参数：</td></tr>
				<tr>
					<td>
						<span style="color:red">*</span>订单号:
							<input name="ORDERSEQ" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${ORDERSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码:
							<input name="CUSTCODE" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>系统参考号：
						<input name="SYSTEMNO" type="text" size="15" maxlength="500"
							value="${map.SYSTEMNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>预留手机：
						<input name="PHONE" type="text" size="15" maxlength="15"
							value="${map.PHONE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>查询ID：
						<input name="SEARCHID" type="text" size="15" maxlength="128"
							value="${map.SEARCHID}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>车次ID：
						<input name="TRAINID" type="text" size="15" maxlength="512"
							value="${map.TRAINID}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>预定信息：
						<textarea name="BOOKINFO" cols="100" rows="3">${map.BOOKINFO}</textarea>
					</td>
				</tr>
				<!-- 
				<tr>
					<td>
						<span style="color:red">*</span>是否出票：
						<select name="ISOUT">
										<option value="0" selected="selected">直接出票</option>
										<option value="1">只预订不出票</option>
						</select>
					</td>
				</tr>
				 -->
				<tr>
					<td>
						<span style="color:red">*</span>申请时间：
						<input name="DATE" type="text" size="14" maxlength="14"
							value="${DATE}">
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="15" maxlength="32"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="15" maxlength="10"
							value="${map.REMARK2}">
					</td>
				</tr>
				<tr>
					<td>
						<input name="submit" value="提交" type="submit">
					</td>
				</tr>

			</table>
		</form>

	</body>
</html>
