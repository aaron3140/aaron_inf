<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02002.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02002">
			<input type="hidden" name="method" value="accountBankInfo">
			<table>
				<tr>
					<td><font color="red" size="5">客户银行信息查询接口</font></td>
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
						客户编码：
						<input name="CUSTCODE" type="text" size="20" maxlength="50"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>用户名:
							<input name="STAFFCODE" type="text" size="32" maxlength="32" id="staffCode"
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
