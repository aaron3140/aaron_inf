<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06201.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	String dateStr = sdf.format(new Date());
	pageContext.setAttribute("acctDate",dateStr);
	Date currentDate = new Date();
	String sequence = "666" + sdf.format(currentDate) + "123";
	String currentTime = sdf.format(currentDate);
	%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post" id="paymentInfo" 
			action="${sessionScope.path}/httppost" >
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06201">
			<input type="hidden" name="method" value="inf06201">
			<table>
				<tr>
					<td><font color="red" size="5">[东莞公交]签到接口 </font></td>
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
						<span style="color:red" >*</span>客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">(注：当渠道为60时，可为空)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>受理时间：
						<input name="TRADETIME" type="text" size="30"
							maxlength="30" value="<%=currentTime%>">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>客户终端号：
						<input name="TMNNUMNO" type="text" size="15" maxlength="12"
							value="${map.TMNNUMNO}">(由翼支付系统为客户生成的12位终端号（非60渠道）或8位外部终端号(60渠道))
					</td>
				</tr>
				<tr>
					<td>
						    <span style="color:red" >*</span> 操作员：
							<input name="STAFFCODE" type="text" size="32" maxlength="32" id="staffCode"
							value="${map.STAFFCODE}">
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
