<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="200003">
			<input type="hidden" name="method" value="paymentInfo">
			<table>
				<tr>
					<td><font color="red" size="5">支付明细查询接口</font></td>
				</tr>
				<tr>
					<td>
						CARDNO：
						<input name="CARDNO" type="text" size="40" maxlength="40"
							value="8811000000000011">
					</td>
				</tr>
				<tr>
					<td>
						TXNTYPE：
						<select name="TXNTYPE">
										<option value="" selected="selected">--所有类型--</option>
										<option value="000001">支付</option>
										<option value="000002">扣款延期</option>
						</select>
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
						STARTTIME：
						<input name="STARTTIME" type="text" size="16" maxlength="16"
							value="20110611111111">
					</td>
				</tr>
				
				<tr>
					<td>
						ENDTIME：
						<input name="ENDTIME" type="text" size="30"
							maxlength="30" value="20121111111111">
					</td>
				</tr>
				
				<tr>
					<td>
						STARTRECORD：
						<input name="STARTRECORD" type="text" size="14"
							maxlength="14" value="0">
					</td>
				</tr>
				<tr>
					<td>
						MAXRECORD：
						<input name="MAXRECORD" type="text" size="14"
							maxlength="14" value="5">
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
