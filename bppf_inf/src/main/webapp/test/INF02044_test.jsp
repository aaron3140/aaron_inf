<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02044.txt");
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02044">
			<input type="hidden" name="method" value="inf02044">
			<table>
				<tr>
					<td><font color="red" size="5">产品溢价查询接口</font></td>
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
						用户名:
							<input name="STAFFCODE" type="text" size="50" maxlength="32" id="custCodeValId"
							value="${map.STAFFCODE}">
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
						<span style="color:red">*</span>面值金额：
						<select name="FACEAMOUNT">
										<option value="5000" selected="selected">50</option>
										<option value="1000">10</option>
										<option value="2000">20</option>
										<option value="3000">30</option>
										<option value="10000">100</option>
										<option value="20000">200</option>
										<option value="30000">300</option>
										<option value="50000">500</option>
										<option value="100000">1000</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						充值手机号码:
							<input name="PHONE" type="text" size="15" maxlength="32" id="custCodeValId"
							value="${map.PHONE}">话费充值业务时必填，表示待充值的手机号码
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>业务编码：
						<select name="ACTIONCODE">
										<option value="03010008" selected="selected">全国电信直充</option>
										<option value="05010005">全国移动直充</option>
										<option value="04010003">全国联通直充</option>
										<option value="09010001">电子售卡</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						产品编码：
						<select name="PRODCODE">
										<!-- 
										<option value="0003" >电信话费充值</option>
										<option value="0005">移动话费充值</option>
										<option value="0004">联通话费充值</option>
										 -->
										<option value="1001" selected="selected">电信充值付费卡</option>
										<option value="1002">联通一卡充</option>
										<option value="2003">天下通卡</option>
										<option value="2004">翼充卡</option>
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
