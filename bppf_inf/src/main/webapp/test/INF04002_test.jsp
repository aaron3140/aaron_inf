<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" scope="request"
	value="${pageContext.request.contextPath}" />
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map = FileUtil.getPageparams("WEB_INF04002.txt");
	pageContext.setAttribute("map", map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("TRADETIME", sdf.format(new Date()));
	pageContext.setAttribute("ORDERNO", System.currentTimeMillis() + 123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<title>语音操作查询密码验证接口</title>
	</head>
	 <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript">
	function show(){
	    var password =document.getElementById('password').value;
	    if(password==""){
	       alert("密码不能为空！");
	       return false;
	    }
	}
	</script>
	<body>
		<form name="paymentInfo" method="post" id="paymentInfo" 
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16"
				value="INF04002">
			<input type="hidden" name="method" value="inf04002">
			<table>
				<tr>
					<td>
						<font color="red" size="5">语音操作查询密码验证接口</font>
					</td>
				</tr>
				<tr>
					<td height="50">
						校验参数：
					</td>
				</tr>
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

				<tr>
					<td height="50">
						请求参数：
					</td>
				</tr>


				<tr>
					<td>
						<span style="color: red">*</span> 订单号：
						<input name="ORDERNO" type="text" size="25" maxlength="25"
							value="${map.ORDERNO}">
					</td>
				</tr>

				<tr>
					<td>
						<span style="color: red">*</span> 客户编码：
						<input name="CUSTCODE" type="text" size="25" maxlength="32"
							value="${map.CUSTCODE}">
						<span style="color: red"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span> 密码类型：
						<input type="radio" checked="checked" value="00" name="PASSWORDTPYE" />
						操作密码
						<input type="radio" value="01" name="PASSWORDTPYE"
							 />
						查询密码
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span> 密码：
						<input name="PASSWORD" id="password" type="text" size="25" maxlength="32"
							value="${map.PASSWORD}">
						<span style="color: red">加密后的查询密码</span>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span> 管理员工号：
						<input name="STAFFID" type="text" size="25" maxlength="32"
							value="${map.STAFFID}">
						<span style="color: red"></span>
					</td>
				</tr>

				<tr>
					<td>
						<input name="submit" onclick="return show();" value="提交" type=submit >
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>