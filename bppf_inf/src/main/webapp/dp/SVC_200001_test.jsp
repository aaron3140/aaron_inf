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
		String sequence = "666" + sdf.format(currentDate) + "456";
		String currentTime = sdf.format(currentDate);
	%>
	<body>

		<form name="payment" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="200001">
			<input type="hidden" name="method" value="payment">
			<table>
			<tr>
					<td><font color="red" size="5">支付接口</font></td>
				</tr>
				<tr>
					<td>
						AGENTCODE：
						<input name="AGENTCODE" type="text" size="16" maxlength="16"
							value="000019300000">
					</td>
				</tr>
				
				<tr>
					<td>
						AREACODE：
						<input name="AREACODE" type="text" size="10" maxlength="10"
							value="440000">
					</td>
				</tr>

				<tr>
					<td>
						TXNCHANNEL：
						<input name="TXNCHANNEL" type="text" size="16" maxlength="16"
							value="08">
					</td>
				</tr>

				<tr>
					<td>
						PAYTYPE：
						<select name="PAYTYPE">
										<option value="01" selected="selected">单卡支付</option>
										<option value="02">多卡支付</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						CARDTYPE：
						<select name="CARDTYPE">
										<option value="1" selected="selected">天翼支付卡</option>
										<option value="2">11888卡</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						CARDNO：
						<input name="CARDNO" type="text" size="40" maxlength="40"
							 value="110000000240">
					</td>
				</tr>
				<tr>
					<td>
						CARDPWD：
						<input name="CARDPWD" type="text" size="30"
							maxlength="30" value="123456">
					</td>
				</tr>
				<tr>
					<td>
						TXNAMOUNT：
						<input name="TXNAMOUNT" type="text" size="30"
							maxlength="30" value="100">
					</td>
				</tr>
				<tr>
					<td>
						GOODSNAME：
						<input name="GOODSNAME" type="text" size="30"
							maxlength="30" value="测试支付">
					</td>
				</tr>
				<tr>
					<td>
						GOODSCODE：
						<input name="GOODSCODE" type="text" size="30"
							maxlength="30" value="123456">
					</td>
				</tr>
				<tr>
					<td>
						TRADESEQ：
						<input name="TRADESEQ" type="text" size="30"
							maxlength="30" value="<%=sequence%>">
					</td>
				</tr>
				<tr>
					<td>
						TRADETIME：
						<input name="TRADETIME" type="text" size="30"
							maxlength="30" value="<%=currentTime%>">
					</td>
				</tr>
				<tr>
					<td>
						TEXT1：
						<input name="TEXT1" type="text" size="30"
							maxlength="30" value="TEXT1">
					</td>
				</tr>
				<tr>
					<td>
						TEXT2：
						<input name="TEXT2" type="text" size="30"
							maxlength="30" value="TEXT2">
					</td>
				</tr>
				<tr>
					<td>
						TEXT3：
						<input name="TEXT3" type="text" size="30"
							maxlength="30" value="TEXT3">
					</td>
				</tr>
				<tr>
					<td>
						TEXT4：
						<input name="TEXT4" type="text" size="30"
							maxlength="30" value="TEXT4">
					</td>
				</tr>
				<tr>
					<td>
						TEXT5：
						<input name="TEXT5" type="text" size="30"
							maxlength="30" value="TEXT5">
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
