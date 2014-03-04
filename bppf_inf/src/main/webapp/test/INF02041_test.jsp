<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map = FileUtil.getPageparams("WEB_INF02041.txt");
	pageContext.setAttribute("map", map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<style type="text/css">
.fancy {
	color: red
}
</style>
</head>

<body>

	<form name="paymentInfo" method="post"
		action="${sessionScope.path}/httpipos">
		<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16"
			value="INF02041"> <input type="hidden" name="method"
			value="inf02041">
		<table>
			<tr>
				<td><font color="red" size="5">消息管理接口</font></td>
			</tr>

			<tr>
				<td height="50">校验参数：</td>
			</tr>
			<tr>
				<td>MERID： <input name="MERID" type="text" size="20"
					maxlength="50" value="${map.MERID}">
				</td>
			</tr>

			<tr>
				<td>CHANNELCODE： <input name="CHANNELCODE" type="text"
					size="20" maxlength="2" value="${map.CHANNELCODE}">
				</td>
			</tr>

			<tr>
				<td>TMNNUM： <input name="TMNNUM" type="text" size="20"
					maxlength="15" value="${map.TMNNUM}">
				</td>
			</tr>

			<tr>
				<td height="50">请求参数：</td>
			</tr>
			<tr class="per">
				<td required="required"><span class="fancy">*</span>客户编码: <input
					name="CUSTCODE" type="text" size="50" maxlength="32"
					required=required value="${map.CUSTCODE}"></td>
			</tr>
			<tr class="per">
				<td><span class="fancy">*</span>用&nbsp;户&nbsp;名: <input
					name="STAFFCODE" type="text" size="50" maxlength="50"
					required=required value="${map.STAFFCODE}"></td>
			</tr>

			<tr>
				<td><span class="fancy">*</span>订&nbsp;单&nbsp;号: <input
					name="ORDERNO" type="text" size="50" maxlength="25"
					required=required value="${map.ORDERNO}"></td>
			</tr>

			<tr>
				<td><span class="fancy">*</span>信息类型: <select id="ISSUETYPE"
					name="ISSUETYPE" required=required>
						<option value="01">公告</option>
						<option value="02">消息</option>
				</select></td>
			</tr>
			<tr>
				<td><span class="fancy">*</span>管理类型： <select id="OPERTYPE"
					name="OPERTYPE" required=required>
						<option value="0">已阅</option>
						<option value="1">删除</option>
				</select></td>
			</tr>
			<tr>
				<td><span class="fancy">*</span>信息标识： <input name="ISSUEID"
					type="text" size="15" maxlength="15" required=required
					value="${map.ISSUEID}"></td>
			</tr>
			<tr>
				<td><input name="submit" value="提交" type="submit"></td>
			</tr>

		</table>
	</form>

</body>
</html>
