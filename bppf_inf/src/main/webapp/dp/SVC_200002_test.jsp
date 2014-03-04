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
		String sequence = "666" + sdf.format(currentDate) + "567";
		String currentTime = sdf.format(currentDate);
	%>
	<body>

		<form name="paymentReversal" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="200002">
			<input type="hidden" name="method" value="paymentReversal">
			<table>
				<tr>
					<td><font color="red" size="5">支付冲正接口</font></td>
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
						CARDNO：
						<input name="CARDNO" type="text" size="40" maxlength="40"
							value="16748">
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
						OLDTRADESEQ：
						<input name="OLDTRADESEQ" type="text" size="30" maxlength="30"
							value="0112121211">
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
						TRADETIME：
						<input name="TRADETIME" type="text" size="30"
							maxlength="30" value="<%=currentTime%>">
					</td>
				</tr>
				
				
				<tr>
					<td>
						OLDTRADETIME：
						<input name="OLDTRADETIME" type="text" size="16" maxlength="16"
							value="20110711111111">
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
