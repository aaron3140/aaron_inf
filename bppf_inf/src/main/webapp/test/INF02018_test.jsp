<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02018.txt");
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02018">
			<input type="hidden" name="method" value="inf02018">
			<table>
				<tr>
					<td><font color="red" size="5">电子售卡接口</font></td>
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
						卡类型编码：
						<select name="CARDTYPECODE">
										<option value="1001" selected="selected">电信充值付费卡</option>
										<option value="1002">联通一卡充</option>
										<option value="2003">天下通卡</option>
										<option value="2004">翼充卡</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						金额:
						<select name="CARDAMOUNT">
										<option value="1000" selected="selected">10</option>
										<option value="2000" >20</option>
										<option value="3000" >30</option>
										<option value="5000">50</option>
										<option value="10000">100</option>
										<option value="20000">200</option>
										<option value="30000">300</option>  
										<option value="50000">500</option>
										<option value="100000">1000</option>
						</select><span style="color:red">*电信：10,20,30,50,100,200,300,500,1000  翼充卡：10,20,30,50,100,200,300,500 联通:30,50,100  天下通游戏卡：10,30,50,100</span>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码:
							<input name="CUSTCODE" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>用户名:
							<input name="STAFFCODE" type="text" size="32" maxlength="32" id="staffCode"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						交易密码:
							<input name="PAYPASSWORD" type="text" size="32" maxlength="32" id="staffCode"
							value="${map.PAYPASSWORD}">
							<span style="color:red">加密后的交易密码,累积限额内免输入</span>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span>支付方式：
						<select name="PAYTYPE" style="width: 100px;">
							<option value="0">
								其它
							</option>
							<option value="9">
								纯业务
							</option>
						</select>
					</td>
				</tr>
			
				<tr>
					<td>
						<span style="color:red">*</span>受理时间:
							<input name="TRADETIME" type="text" size="14" maxlength="14" id="tradeTime"
							value="${TRADETIME}">
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
