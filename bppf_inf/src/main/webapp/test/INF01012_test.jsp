<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF01012.txt");
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
<script type="text/javascript" >
	
</script>

<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF01012">
			<input type="hidden" name="method" value="inf01012">
			<table>
				<tr>
					<td><font color="red" size="5">鉴权验证接口 </font></td>
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
						<input name="TMNNUM" type="text" size="12" maxlength="12"
							value="${map.TMNNUM}">
					</td>
				</tr>
				
				<tr><td height="50">请求参数：</td></tr>

				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>用户称：
						<input name="STAFFCODE" type="text" size="15" maxlength="14"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>加密后密码：
						<input name="PASSWORD" type="text" size="15" maxlength="14"
							value="${map.PASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>鉴权类型：
						<input name="VERIFYTYPE" type="text" size="15" maxlength="14"
							value="${map.VERIFYTYPE}">
					</td>
				</tr>
				<tr>
					<td>
						校验方式：
						<select name="VERIFYLEVEL">
										<option value="" selected="selected">不选</option>
										<option value="000">登录校验</option>
										<option value="001">一般校验</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						手机验证码：
						<input name="VERIFYCODE" type="text" size="15" maxlength="14"
							value="${map.VERIFYCODE}">
					</td>
				</tr>
				<tr>
					<td>
						手机IMEI：
						<input name="IMEI" type="text" size="15" maxlength="14"
							value="${map.IMEI}">
					</td>
				</tr>
				<tr>
					<td>
						手机IMSI：
						<input name="IMSI" type="text" size="15" maxlength="14"
							value="${map.IMSI}">
					</td>
				</tr>
				<tr>
					<td>
						WIFI的MAC地址：
						<input name="WIFIMAC" type="text" size="15" maxlength="14"
							value="${map.WIFIMAC}">
					</td>
				</tr>
				<tr>
					<td>
						蓝牙的MAC地址：
						<input name="BLUEMAC" type="text" size="15" maxlength="14"
							value="${map.BLUEMAC}">
					</td>
				</tr>
				<tr>
					<td>
						预留1：
						<input name="REMARK1" type="text" size="15" maxlength="14"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留2：
						<input name="REMARK2" type="text" size="15" maxlength="14"
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
