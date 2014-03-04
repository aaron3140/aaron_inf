<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02040.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	<style type="text/css">
		.fancy	{
			color:red
		}
	</style>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httpipos" >
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02040">
			<input type="hidden" name="method" value="inf02040">
			<table>
				<tr>
					<td><font color="red" size="5">消息未读条数查询接口</font></td>
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
						<input name="CHANNELCODE" type="text" size="20"  maxlength="2"
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
				<tr class="per" >
					<td required="required">
						<span  class="fancy">*</span>客户编码:
							<input name="CUSTCODE" type="text" size="50" maxlength="32" required=required
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr class="per" >
					<td>
						<span class="fancy">*</span>用&nbsp;户&nbsp;名:
							<input name="STAFFCODE" type="text" size="50" maxlength="50" required=required
							value="${map.STAFFCODE}">
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
