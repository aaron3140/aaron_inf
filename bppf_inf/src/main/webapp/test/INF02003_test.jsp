<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02003.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02003">
			<input type="hidden" name="method" value="clientVersionManager">
			<table>
				<tr>
					<td><font color="red" size="5">客户端版本管理接口</font></td>
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
				
				<%--<tr>
					<td>
						SIGN：
						<input name="SIGN" type="text" size="20" maxlength="4096"
							value="SIGN">
					</td>
				</tr>
				
				<tr>
					<td>
						CER：
						<input name="CER" type="text" size="20" maxlength="4096"
							value="CER">
					</td>
				</tr>
				
				--%><tr><td height="50">请求参数：</td></tr>
				
				<tr>
					<td>
						<span style="color:red">*</span>手机ISMI号：
						<input name="IMSI" type="text" size="20" maxlength="50"
							value="${map.IMSI}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>当前版本号：
						<input name="CURVERSION" type="text" size="20" maxlength="50"
							value="${map.CURVERSION}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>手机平台：
						<select name="SYSTEM">
							<option value="android" selected="selected">android</option>					
							<option value="brew">brew</option>					
							<option value="wince">wince</option>					
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>手机平台版本号：
						<input name="SYSVERSION" type="text" size="20" maxlength="50"
							value="${map.SYSVERSION}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>手机型号：
						<input name="PHONE" type="text" size="20" maxlength="50"
							value="${map.PHONE}">
					</td>
				</tr>
				<tr>
					<td>
						手机号码：
						<input name="PRODUCTNO" type="text" size="20" maxlength="50"
							value="${map.PRODUCTNO}">
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
