<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06007.txt");
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06007">
			<input type="hidden" name="method" value="inf06007">
			<table>
				<tr>
					<td><font color="red" size="5">卡户管理接口 </font></td>
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
						<span style="color:red" type="BT001">*</span>订单号：
						<input name="ORDERNO" type="text" size="30" maxlength="25"
							value="${ORDERNO}">
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
						<span style="color:red" type="BT001">*</span>子卡客户编码：
						<input name="CHILDCUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CHILDCUSTCODE}">
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
						<span style="color:red" type="BT001">*</span>操作密码：
						<input name="PAYPASSWORD" type="text" size="8" maxlength="128"
							value="${map.PAYPASSWORD}">加密后的密码
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>操作类型编码：
							<select name="OPERTYPE">
							<option value="100" selected="selected">设置日交易限额</option>
							<option value="200">冻结卡户</option>
							<option value="201">解冻卡户</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						</span>日交易限额：
						<input name="DAYLIMIT" type="text" size="14" maxlength="12"
							value="${map.DAYLIMIT}">限额：以分为单位当操作类型为[设置日交易限额]时必填
					</td>
				</tr>
				<tr>
					<td>
						</span>受理时间：
						<input name="TRADETIME" type="text" size="14" maxlength="14"
							value="${acctDate}">发起方的受理时间yyyyMMddHHmmss格式
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
