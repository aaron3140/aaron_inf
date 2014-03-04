<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02010.txt");
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02010">
			<input type="hidden" name="method" value="inf02010">
			<table>
				<tr>
					<td><font color="red" size="5">Q币充值接口</font></td>
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
					<td><span style="color:red">*</span>
						订单号：
						<input name="ORDERNO" type="text" size="25" maxlength="25"
							value="${ORDERNO}">
					</td>
				</tr>
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
						交易密码：
						<input name="PAYPASSWORD" type="text" size="64" maxlength="32"
							value="${map.PAYPASSWORD}"><span style="color:red">加密后的交易密码,累积限额内免输入</span>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>QQ号码：
						<input name="QQ" type="text" size="25" maxlength="20"
							value="${map.QQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>Q币数量：
						<input name="RECHARGEAMOUNT" type="text" size="25" maxlength="12"
							value="${map.RECHARGEAMOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>交易金额：
						<input name="TXNAMOUNT" type="text" size="25" maxlength="12"
							value="${map.TXNAMOUNT}">以分为单位
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>受理时间：
						<input name="TRADETIME" type="text" size="25" maxlength="14"
							value="${TRADETIME}">
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
