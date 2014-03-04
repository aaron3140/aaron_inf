<%@page import="common.utils.FileUtil"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF03003.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	String dateStr = sdf.format(new Date());
	pageContext.setAttribute("acctDate",dateStr);
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
<script type="text/javascript" >
	
</script>

<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF03003">
			<input type="hidden" name="method" value="inf03003">
			<table>
				<tr>
					<td><font color="red" size="5">收款请求接口</font></td>
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
						<input name="TMNNUM" type="text" size="20" maxlength="20"
							value="${map.TMNNUM}">
					</td>
				</tr>
				
				<tr><td height="50">请求参数：</td></tr>

				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>用户名：
						<input name="STAFFCODE" type="text" size="15" maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>加密后密码：
						<input name="PASSWORD" type="text" size="15" maxlength="32"
							value="${map.PASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>付款商户编码：
						<input name="AGENTCODE" type="text" size="15" maxlength="50"
							value="${map.AGENTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						区域编码：
						<input name="AREACODE" type="text" size="15" maxlength="6"
							value="${map.AREACODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>请求收款金额：
						<input name="TXNAMOUNT" type="text" size="15" maxlength="12"
							value="${map.TXNAMOUNT}">(以分为单位)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>收款商户编码：
						<input name="PAYEECODE" type="text" size="15" maxlength="12"
							value="${map.PAYEECODE}">
					</td>
				</tr>
				<tr>
					<td>
						收款商户名称：
						<input name="PAYEENAME" type="text" size="15" maxlength="12"
							value="${map.PAYEENAME}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>交易时间：
						<input name="TRADETIME" type="text" size="15" maxlength="14"
							value="${acctDate}">(yyyyMMddHHmmss格式)
					</td>
				</tr>
				<tr>
					<td>
						备注字段1：
						<input name="REMARK1" type="text" size="15" maxlength="200"
							value="${map.REMARK1}">
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
