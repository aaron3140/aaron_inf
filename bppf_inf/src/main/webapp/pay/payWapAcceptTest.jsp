<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

	</head>
	<%
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date currentDate = new Date();
		String sequence = "666" + sdf.format(currentDate) + "123";
		String currentTime = sdf.format(currentDate);
	%>
	<body>
		<form name="payWapForm" method="post"
			action="${sessionScope.path}/payWap.do">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16"
				value="100010">
			<input type="hidden" name="method" value="modifyPwd">
			<table>
				<tr>
					<td>
						<font color="red" size="5">预付卡订单支付请求接口</font>
					</td>
				</tr>
				<tr>
					<td>
						AGENTCODE：
						<input name="AGENTCODE" type="text" size="30" maxlength="30"
							value="000019300000">
					</td>
				</tr>
				<tr>
					<td>
						AREACODE：
						<input name="AREACODE" type="text" size="30" maxlength="30"
							value="440000">
					</td>
				</tr>
				<tr>
					<td>
						TXNCHANNEL：
						<input name="TXNCHANNEL" type="text" size="30" maxlength="30"
							value="08">
					</td>
				</tr>

				<tr>
					<td>
						TXNAMOUNT：
						<input name="TXNAMOUNT" type="text" size="30" maxlength="30"
							value="100">
					</td>
				</tr>

				<tr>
					<td>
						MERCHANTURL：
						<input name="MERCHANTURL" type="text" size="100" maxlength="100"
							value="http://116.228.55.221/webpay/tianyiPrepaidBgReceive.do">
					</td>
				</tr>
				<tr>
					<td>
						BACKMERCHANTURL：
						<input name="BACKMERCHANTURL" type="text" size="100" maxlength="100"
							value="http://116.228.55.221/webpay/tianyiPrepaidBgReceive.do">
					</td>
				</tr>
				<tr>
					<td>
						GOODSCODE：
						<input name="GOODSCODE" type="text" size="30" maxlength="30"
							value="333">
					</td>
				</tr>
				<tr>
					<td>
						GOODSNAME：
						<input name="GOODSNAME" type="text" size="30" maxlength="30"
							value="单车">
					</td>
				</tr>
				<tr>
					<td>
						TRADESEQ：
						<input name="TRADESEQ" type="text" size="30" maxlength="30"
							value="<%=sequence%>">
					</td>
				</tr>
				<tr>
					<td>
						REQUESTSEQ：
						<input name="REQUESTSEQ" type="text" size="30" maxlength="30"
							value="<%=sequence%>">
					</td>
				</tr>
				<tr>
					<td>
						TRADETIME：
						<input name="TRADETIME" type="text" size="30" maxlength="30"
							value="<%=currentTime%>">
					</td>
				</tr>
				<tr>
					<td>
						ENCODETYPE：
						<input name="ENCODETYPE" type="text" size="30" maxlength="30"
							value="0">
					</td>
				</tr>
				<tr>
					<td>
						MAC：
						<input name="MAC" type="text" size="30" maxlength="" value="0">
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
