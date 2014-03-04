<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map = FileUtil.getPageparams("WEB_INF02042.txt");
	pageContext.setAttribute("map", map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("ISSUEDATE", sdf.format(new Date()));
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
			value="INF02042"> <input type="hidden" name="method"
			value="inf02042">
		<table>
			<tr>
				<td><font color="red" size="5">消息列表接口</font></td>
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
				<td><span class="fancy">*</span>发布渠道: <select id="ISSUECHANNEL"
					name="ISSUECHANNEL" required=required>
						<option value="01">门户</option>
						<option value="02">手机客户端</option>
						<option value="03">短信</option>
				</select></td>
			</tr>

			<tr>
				<td><span class="fancy">*</span>信息类型: <select id="ISSUETYPE" name="ISSUETYPE">
						<option value="02">消息</option>
						<option value="01">公告</option>
				</select>
				</td>
			</tr>
			<tr>
				<td>发布范围： <select id="ISSUESCOPE" name="ISSUESCOPE">
						<option value="" >请选择</option>
						<option value="01">按地区发布</option>
						<option value="02">按产品发布</option>
						<option value="03">按机构发布</option>
						<option value="04">按商户发布</option>
				</select>
				</td>
			</tr>
			<tr>
				<td>发布起始时间： <input name="ISSUEDATESTART" type="text" size="15"
					maxlength="15" value="${map.ISSUEDATESTART}">
				</td>
			</tr>
			<tr>
				<td>发布截至时间： <input name="ISSUEDATEEND" type="text" value="2017-09-07 00:00:00" size="15"
					maxlength="15" value="${map.ISSUEDATEEND}">
				</td>
			</tr>

			<tr>
				<td>开始页： <input name="start" type="text" size="15"
					maxlength="15" value="${map.start}">
				</td>
			</tr>
			<tr>
				<td><span class="fancy">*</span>页码： <input name="page"
					type="text" size="15" maxlength="15" required=required
					value="${map.page}"></td>
			</tr>

			<tr>
				<td><input name="submit" value="提交" type="submit"></td>
			</tr>

		</table>
	</form>

</body>
</html>
