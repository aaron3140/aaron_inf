<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02008.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("TRADETIME",sdf.format(new Date()));
	pageContext.setAttribute("ORDERNO",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httpipos" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02008">
			<input type="hidden" name="method" value="inf02008">
			<table>
				<tr>
					<td><font color="red" size="5">密码管理接口</font></td>
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
						<span style="color:red">*</span>客户编码:
							<input name="CUSTCODE" type="text" size="15" maxlength="32" id="custCodeValId"
							value="${map.CUSTCODE}">
					</td>
				</tr>
			
				<tr>
					<td>
						<span style="color:red">*</span>用户名：
						<input name="STAFFCODE" type="text" size="50" maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						手机验证码：
						<input name="VERIFYCODE" type="text" size="32" maxlength="32"
							value="${map.VERIFYCODE}">
					</td>
				</tr>
				<tr>
					<td>
						操作类型：
						<select name="OPERTYPE">
										<option value="00" selected="selected">修改</option>
										<option value="01">重置</option>(默认是 修改)
						</select>
					</td>
				</tr>
				<tr>
					<td>
						当前密码：
						<input name="PASSWORD" type="text" size="32" maxlength="32"
							value="${map.PASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						新密码：
						<input name="NEWPASSWORD" type="text" size="32" maxlength="32"
							value="${map.NEWPASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						确认密码：
						<input name="AFFTPASSWORD" type="text" size="32" maxlength="32"
							value="${map.AFFTPASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						密码类型：
						<select name="PASSWORDTYPE">
										<option value="0002" selected="selected">登录密码</option>
										<option value="0001">支付密码</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="15" maxlength="32"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="15" maxlength="10"
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
