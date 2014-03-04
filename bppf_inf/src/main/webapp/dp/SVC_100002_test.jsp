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

		<form name="cardOrder" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="100002">
			<input type="hidden" name="method" value="cardOrder">
			<table>
				<tr>
					<td><font color="red" size="5">卡预定接口</font></td>
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
						CARDTYPE：
						<select name="CARDTYPE">
										<option value="1" selected="selected">天翼支付卡</option>
										<option value="2">11888卡</option>
						</select>
					</td>
				</tr>

				<tr>
					<td>
						SUBCARDTYPE：
						<select name="SUBCARDTYPE">
										<option value="" selected="selected"></option>
										<option value="100001">普通磁条卡</option>
										<option value="100002">普通账号卡</option>
										<option value="100003">普通电子卡</option>
										<option value="200001">商家联名磁条卡</option>
										<option value="200002">商家联名账号卡</option>
										<option value="200003">商家联名电子卡</option>
										<option value="300002">11888充值账号卡</option>
										<option value="300003">11888充值电子卡</option>
										<option value="400001">测试磁条卡</option>
										<option value="400002">测试账号卡</option>
										<option value="400003">测试电子卡</option>
						</select>
					</td>
				</tr>
<tr>
					<td>
						CARDPREFIX：
						<input name="CARDPREFIX" type="text" size="16" maxlength="16"
							value="11">
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
