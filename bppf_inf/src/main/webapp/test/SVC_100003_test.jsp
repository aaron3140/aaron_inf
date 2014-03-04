<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="100003">
			<input type="hidden" name="method" value="paymentInfo">
			<table>
				<tr>
					<td><font color="red" size="5">交易接口</font></td>
				</tr>
				<tr>
					<td>
						商户编码：
						<input name="AGENTCODE" type="text" size="15" maxlength="15"
							value="881100000000001">
					</td>
				</tr>
				<tr>
					<td>
						交易渠道：
						<input name="TXNCHANNEL" type="text" size="15" maxlength="2"
							value="01">
					</td>
				</tr>
				<tr>
					<td>
						操作编码：
						<input name="ACTIONCODE" type="text" size="15" maxlength="4"
							value="4548">
					</td>
				</tr>
				<tr>
					<td>
						操作人：
						<input name="STAFFCODE" type="text" size="15" maxlength="2"
							value="张三">
					</td>
				</tr>
				<tr>
					<td>
						支付密码：
						<input name="PAYPWD" type="text" size="15" maxlength="512"
							value="248452454145454">
					</td>
				</tr>
				<tr>
					<td>
						交易金额：
						<input name="TXNAMOUNT" type="text" size="15" maxlength="12"
							value="154554543465">
					</td>
				</tr>
				<tr>
					<td>
						收款商户编码：
						<input name="PAYEECODE" type="text" size="15" maxlength="20"
							value="56987423518647593215">
					</td>
				</tr>
				<tr>
					<td>
						外系统商品编码：
						<input name="GOODSCODE" type="text" size="15" maxlength="15"
							value="698423576854126">
					</td>
				</tr>
				<tr>
					<td>
						外系统商品名称：
						<input name="GOODSNAME" type="text" size="15" maxlength="50"
							value="343668654">
					</td>
				</tr>
				<tr>
					<td>
						交易流水号：
						<input name="TRADESEQ" type="text" size="15" maxlength="20"
							value="445766324">
					</td>
				</tr>
				<tr>
					<td>
						订单号：
						<input name="ORDERID" type="text" size="15" maxlength="20"
							value="4674565476543">
					</td>
				</tr>
				<tr>
					<td>
						交易时间：
						<input name="TRADETIME" type="text" size="15" maxlength="14"
							value="20120131141535">
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
