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
		String sequence = "666" + sdf.format(currentDate) + "345";
		String currentTime = sdf.format(currentDate);
	%>
	<body>

		<form name="cardOrderConfirm" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="100004">
			<input type="hidden" name="method" value="cardOrderConfirm">
			<table>
				<tr>
					<td><font color="red" size="5">卡确认购买接口</font></td>
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
						OLDTRADESEQ：
						<input name="OLDTRADESEQ" type="text" size="30" maxlength="30"
							value="0112121211">
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
						USERINFO
					</td>
				</tr>
				<tr>
					<td>
						USERTYPE：
						<select name="USERTYPE">
										<option value="" selected="selected">--请选择--</option>
										<option value="00">个人</option>
										<option value="01">单位</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						ORGNAME：
						<input name="ORGNAME" type="text" size="30"
							maxlength="30" value="哈哈">
					</td>
				</tr>
				<tr>
					<td>
						USERNAME：
						<input name="USERNAME" type="text" size="30"
							maxlength="30" value="接口测试">
					</td>
				</tr>
				<tr>
					<td>
						ID_TYPE：
						<select name="ID_TYPE">
										<option value="" selected="selected">--请选择--</option>
										<option value="01">身份证</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						ID_NO：
						<input name="ID_NO" type="text" size="30"
							maxlength="30" value="123456">
					</td>
				</tr>
				<tr>
					<td>
						ADDRESS：
						<input name="ADDRESS" type="text" size="30"
							maxlength="30" value="1">
					</td>
				</tr>
				<tr>
					<td>
						PHONE：
						<input name="PHONE" type="text" size="30"
							maxlength="30"  value="66666666">
					</td>
				</tr>
                <tr>
					<td>
						TEXT1：
						<input name="USERTEXT1" type="text" size="30"
							maxlength="30"  value="TEXT1">
					</td>
				</tr>
				<tr>
					<td>
						TEXT2：
						<input name="USERTEXT2" type="text" size="30"
							maxlength="30"  value="TEXT2">
					</td>
				</tr>
				<tr>
					<td>
						TEXT3：
						<input name="USERTEXT3" type="text" size="30"
							maxlength="30"  value="TEXT3">
					</td>
				</tr>
				<tr>
					<td>
						TEXT4：
						<input name="USERTEXT4" type="text" size="30"
							maxlength="30"  value="TEXT4">
					</td>
				</tr>
				<tr>
					<td>
						TEXT5：
						<input name="USERTEXT5" type="text" size="30"
							maxlength="30"  value="TEXT5">
					</td>
				</tr>

                <tr>
					<td>
						ACCTINFO
					</td>
				</tr>
				<tr>
					<td>
						ACCTNAME：
						<input name="ACCTNAME" type="text" size="30"
							maxlength="30"   value="农业银行">
					</td>
				</tr>
				<tr>
					<td>
						ACCTNO：
						<input name="ACCTNO" type="text" size="30"
							maxlength="30"  value="123456789">
					</td>
				</tr>
				<tr>
					<td>
						MONEY：
						<input name="MONEY" type="text" size="30"
							maxlength="30"  value="1000">
					</td>
				</tr>
				<tr>
					<td>
						TRADETIME：
						<input name="ACCTTRADETIME" type="text" size="30"
							maxlength="30"  value="<%=currentTime%>">
					</td>
				</tr>
                <tr>
					<td>
						TEXT1：
						<input name="ACCTTEXT1" type="text" size="30"
							maxlength="30"  value="TEXT1">
					</td>
				</tr>
				<tr>
					<td>
						TEXT2：
						<input name="ACCTTEXT2" type="text" size="30"
							maxlength="30"  value="TEXT2">
					</td>
				</tr>
				<tr>
					<td>
						TEXT3：
						<input name="ACCTTEXT3" type="text" size="30"
							maxlength="30"  value="TEXT3">
					</td>
				</tr>
				<tr>
					<td>
						TEXT4：
						<input name="ACCTTEXT4" type="text" size="30"
							maxlength="30"  value="TEXT4">
					</td>
				</tr>
				<tr>
					<td>
						TEXT5：
						<input name="ACCTTEXT5" type="text" size="30"
							maxlength="30"  value="TEXT5">
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
