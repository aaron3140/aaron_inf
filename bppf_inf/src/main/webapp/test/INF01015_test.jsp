<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF01015.txt");
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

<html>
	<head>
	</head>
	
	<body onload="setVal();">

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF01015">
			<input type="hidden" name="method" value="inf01015">
			<table>
				<tr>
					<td><font color="red" size="5">实时冲正接口</font></td>
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
				
				<%--<tr>
					<td>
						SIGN：
						<input name="SIGN" type="text" size="20" maxlength="4096"
							value="SIGN">
					</td>
				</tr>
				
				<tr>
					<td>
						CER：
						<input name="CER" type="text" size="20" maxlength="4096"
							value="CER">
					</td>
				</tr>
				
				--%><tr><td height="50">请求参数：</td></tr>
				<tr>
					<td>
						<span style="color:red">*</span>订单号：
						<input name="ORDERSEQ" type="text" size="20" maxlength="20"
							value="${map.ORDERSEQ}">  
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="50" maxlength="50"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>原订单号：
						<input name="APTRANSSEQ" type="text" size="20" maxlength="20"
							value="${map.APTRANSSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>原keep号：
						<input name="APPKEEP" type="text" size="30" maxlength="30"
							value="${map.APPKEEP}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>银行账号：
						<input name="BANKACCT" type="text" size="30" maxlength="30"
							value="${map.BANKACCT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>冲正金额：
						<input name="TXNAMOUNT" type="text" size="15" maxlength="200"
							value="${map.TXNAMOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="15" maxlength="200"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="15" maxlength="200"
							value="${map.REMARK2}">
					</td>
				</tr><!--
				<tr>
					<td>
						预留域3：
						<input name="REMARK3" type="text" size="15" maxlength="200"
							value="${map.REMARK4}">
					</td>
				</tr>
				<tr>
					<td>
						预留域4：
						<input name="REMARK4" type="text" size="15" maxlength="200"
							value="${map.REMARK4}">
					</td>
				</tr>
				--><tr>
					<td>
						<input name="submit" value="提交" type="submit">
					</td>
				</tr>

			</table>
		</form>

	</body>
</html>
