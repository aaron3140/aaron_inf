<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF05004.txt");
	pageContext.setAttribute("map",map);
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

<html>
	<head>
	</head>
	
	<body >

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF05004">
			<input type="hidden" name="method" value="inf05004">
			<table>
				<tr>
					<td><font color="red" size="5">付款接口</font></td>
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
						<span style="color:red">*</span>用户名
						<input name="STAFFCODE"  size="50" maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>密码
						<input name="PASSWORD"  size="50" maxlength="50"
							value="${map.PASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>商户编码
						<input name="CUSTCODE"  size="50" maxlength="50"
							value="${map.CUSTCODE}">
					</td>
				</tr>
			
				<tr>
					<td>
						<span style="color:red">*</span>预受理单号
						<input name="PREORDERSEQ"  size="30" maxlength="30"
							value="${map.PREORDERSEQ}">
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
