<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_01_010.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="01_010">
			<input type="hidden" name="method" value="inf01010">
			<table>
				<tr>
					<td><font color="red" size="5">业务接入回调接口</font></td>
				</tr>
				
				<tr><td height="50">请求参数：</td></tr>

				<tr>
					<td>
						渠道号：
						<input name="C0_OrderId" type="text" size="15" maxlength="30"
							value="${map.C0_OrderId}">
					</td>
				</tr>
				<tr>
					<td>
						流水号：
						<input name="C1_SerNum" type="text" size="15" maxlength="30"
							value="${map.C1_SerNum}">
					</td>
				</tr>
				<tr>
					<td>
						外部渠道代码：
						<input name="C2_Code" type="text" size="15" maxlength="50"
							value="${map.C2_Code}">
					</td>
				</tr>
				<tr>
					<td>
						内部充值订单号：
						<input name="C3_InOrderNo" type="text" size="15" maxlength="50"
							value="${map.C3_InOrderNo}">
					</td>
				</tr>
				<tr>
					<td>
						充值手机号：
						<input name="C4_Mobile" type="text" size="15" maxlength="20"
							value="${map.C4_Mobile}">
					</td>
				</tr>
				<tr>
					<td>
						充值金额：
						<input name="C5_TxnAmt" type="text" size="15" maxlength="10"
							value="${map.C5_TxnAmt}">
					</td>
				</tr>
				<tr>
					<td>
						返回结果：
						<select name="C6_ReturnCode">
							<option value="success" selected="selected">成功</option>
							<option value="failed">失败</option>
							<option value="unknown">充值结果未明确</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						返回结果代码：
						<select name="C7_ResponseCode">
							<option value="000000" selected="selected">交易成功</option>
							<option value="AA0000">报文错误</option>
							<option value="AA0001">MAC验证错误</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						hmac：
						<input name="hmac" type="text" size="15" maxlength="10"
							value="${map.hmac}">
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
