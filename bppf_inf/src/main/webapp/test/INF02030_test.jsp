<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02024.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	String dateStr = sdf.format(new Date());
	pageContext.setAttribute("acctDate",dateStr);
	pageContext.setAttribute("ORDERNO",System.currentTimeMillis()+123456);
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
<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" >

	
</script>

<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post" id="paymentInfo" 
			action="${sessionScope.path}/httppost" >
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02030">
			<input type="hidden" name="method" value="inf02030">
			<table>
				<tr>
					<td><font color="red" size="5">账户绑卡通知接口 </font></td>
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
							>(绑卡时返回的订单号)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>用户名：
						<input name="STAFFCODE" type="text" size="12" maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
							<tr>
					<td>
						操作类型：
						<select name="OPERTYPE">
							<option value="" selected="selected">不选</option>
							<option value="0" >未绑卡->绑卡中</option>
							<option value="1">绑卡中->绑卡失败</option>						
						</select>（不送默认0：未绑卡->绑卡中）
					</td>
				</tr>
				<tr>
					<td>
						开户银行编码：
						<input name="BANKCODE" type="text" size="6" maxlength="6"
							value="${map.BANKCODE}">
					</td>
				</tr>
				<tr>
					<td>
						开户行名称：
						<input name="OPENBANK" type="text" size="128" maxlength="128"
							value="${map.OPENBANK}">(如“建设银行广东省广州市天河珠江新城分行”)
					</td>
				</tr>
				<tr>
					<td>
						区域编码：
						<input name="AREACODE" type="text" size="6" maxlength="6"
							value="${map.AREACODE}">(银行所在地区域编码)
					</td>
				</tr>
				<tr>
					<td>
						银行账号：
						<input name="BANKACCT" type="text" size="30" maxlength="30"
							value="${map.BANKACCT}">
					</td>
				</tr>
				<tr>
					<td>
						开户姓名：
						<input name="TRANSACCNAME" type="text" size="128" maxlength="128"
							value="${map.TRANSACCNAME}">
					</td>
				</tr>
				<tr>
					<td>
						身份证号：
						<input name="CERNO" type="text" size="18" maxlength="18"
							value="${map.CERNO}">(开户时的身份证号)
					</td>
				</tr>
				<tr>
					<td>
						开户手机：
						<input name="PHONE" type="text" size="11" maxlength="11"
							value="${map.PHONE}">(开户时的身份证号)
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
