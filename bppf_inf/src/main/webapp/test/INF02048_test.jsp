<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02048.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("TRADETIME",sdf.format(new Date()));
	pageContext.setAttribute("ORDERSEQ",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httpipos" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02048">
			<input type="hidden" name="method" value="inf02048">
			<table>
				<tr>
					<td><font color="red" size="5">理财赎回接口</font></td>
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
						<span style="color:red">*</span>外部订单号：
						<input name="ORDERSEQ" type="text" size="32" maxlength="32"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>用户名：
							<input name="STAFFCODE" type="text" size="50" maxlength="50" id="staffCodeValId"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码：
							<input name="CUSTCODE" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>支付密码：
							<input name="PASSWORD" type="text" size="32" maxlength="128" id="passWordValId"
							value="${map.PASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>赎回类型：
						<select name="RANSOMTYEPE">
							<option value="0" selected="selected">
								授权银行卡
							</option>
							<option value="1">
								交费易账户
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>赎回金额：
							<input name="TOTALAMOUNT" type="text" size="12" maxlength="12" id="totalAmountValId"
							value="${map.TOTALAMOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>受理时间：
						<input name="TRADETIME" type="text" size="14" maxlength="14"
							value="${map.TRADETIME}">
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
