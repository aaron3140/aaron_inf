<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06008.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	String dateStr = sdf.format(new Date());
	pageContext.setAttribute("acctDate",dateStr);
	pageContext.setAttribute("ORDERNO",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<style type="text/css">
.BT001{
	display: block;
}
.BT002{
	display: none;
}
</style>
<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" >

	
</script>

<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post" id="paymentInfo" 
			action="${sessionScope.path}/httppost" >
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06008">
			<input type="hidden" name="method" value="inf06008">
			<table>
				<tr>
					<td><font color="red" size="5">子卡列表查询接口 </font></td>
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
						<span style="color:red" type="BT001">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>用户名：
						<input name="STAFFCODE" type="text" size="12" maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
						<tr>
					<td>
						子卡客户编码：
						<input name="CHILDCUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CHILDCUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>账户类型：
							<select name="ACCTTYPE">
							<option value="0001" selected="selected">企业账户</option>
							<option value="0007">IPOS账户</option>
							<option value="0110">酬金账户</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">以下两个参数都不传时默认查出前10条</span>
					</td>
				</tr>
				<tr>
					<td>
						查询起始序号：
						<input name="STARTNUM" type="text" size="6" maxlength="4"
							value="${map.STARTNUM}">从第几条开始查询，如从1开始
					</td>
				</tr>
				<tr>
					<td>
						查询结束序号：
						<input name="ENDNUM" type="text" size="6" maxlength="4"
							value="${map.ENDNUM}">查询的结束条数，如50。
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
