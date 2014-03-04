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
		String sequence = "666" + sdf.format(currentDate) + "678";
		String currentTime = sdf.format(currentDate);
	%>
	<body>

		<form name="buyPhysicalCard" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="100011">
			<input type="hidden" name="method" value="buyPhysicalCard">
			<table>
				<tr>
					<td><font color="red" size="5">在线售卡(实体卡)-提交预订单接口</font></td>
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
						COMPANY：
						<input name="COMPANY" type="text" size="30" maxlength="30"
							value="M78">
					</td>
				</tr>
				<tr>
					<td>
						CONTACT：
						<input name="CONTACT" type="text" size="30" maxlength="30"
							value="CONTACT">
					</td>
				</tr>
				<tr>
					<td>
						TEL：
						<input name="TEL" type="text" size="30" maxlength="30"
							value="1311111111">
					</td>
				</tr>
				<tr>
					<td>
						ADD：
						<input name="ADD" type="text" size="30" maxlength="30"
							value="M78星云第9系第9行星第9卫第51区">
					</td>
				</tr>
				<tr>
					<td>
						EMAIL：
						<input name="EMAIL" type="text" size="30" maxlength="30"
							value="M78@163.com">
					</td>
				</tr>

				<tr>
					<td>
						CARDAMT：
						<select name="CARDAMT">
										<option value="1" selected="selected">10元</option>
										<option value="2">20元</option>
										<option value="3">50元</option>
										<option value="4">100元</option>
										<option value="5">200元</option>
										<option value="6">500元</option>
										<option value="7">1000元</option>
										<option value="8">2000元</option>
										<option value="9">5000元</option>
						</select>
					</td>
				</tr>

				<tr>
					<td>
						ORDERNUM：
						<input name="ORDERNUM" type="text" size="30"
							maxlength="30" value="5">
					</td>
				</tr>
				<tr>
					<td>
						INVOICE：
						<input name="INVOICE" type="text" size="30" maxlength="30"
							value="1789417">
					</td>
				</tr>
				<tr>
					<td>
						PAYTYPE：
						<select name="PAYTYPE">
										<option value="1" selected="selected">现金</option>
										<option value="2">支票</option>
										<option value="3">银行转账</option>
										<option value="2">翼支付账户收款</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						ISFIRSTFLAG：
						<select name="ISFIRSTFLAG">
										<option value="1" selected="selected">是</option>
										<option value="2">否</option>
						</select>
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
