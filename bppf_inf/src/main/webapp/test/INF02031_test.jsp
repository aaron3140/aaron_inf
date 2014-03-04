<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02031.txt");
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02031">
			<input type="hidden" name="method" value="inf02031">
			<table>
				<tr>
					<td><font color="red" size="5">短信交易凭证接口</font></td>
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
							<input name="CUSTCODE" type="text" size="18" maxlength="18" required=required
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr class="per" >
					<td>
						<span class="fancy">*</span>用户名:
							<input name="STAFFCODE" type="text" size="18" maxlength="18" required=required
							value="${map.STAFFCODE}">
					</td>
				</tr>
				
				<tr>
					<td>
						<span class="fancy">*</span>交易流水号:
							<input name="TRANSSEQ" type="text" size="18" maxlength="15" required=required
							value="${map.TRANSSEQ}">（即订单号）
					</td>
				</tr>

				<tr>
					<td>
						顾客手机号码:
							<input name="PHONE" type="text" size="12" maxlength="12" 
							value="${map.PHONE}">
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="200" maxlength="200"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="200" maxlength="200"
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
