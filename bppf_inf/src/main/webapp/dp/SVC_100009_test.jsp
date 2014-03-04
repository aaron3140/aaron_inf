<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
	</head>
	
	<body>

		<form name="lossReporting" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="100009">
			<input type="hidden" name="method" value="lossReporting">
			<table>
				<tr>
					<td><font color="red" size="5">挂失接口</font></td>
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
							value="210000000200">
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
						REPLOSSWAY：
						<select name="REPLOSSWAY">
										<option value="1" selected="selected">电话</option>
										<option value="2">书面</option>
					    </select>
					</td>
				</tr>
				<tr>
					<td>
						REPLOSSTYPE：
						<select name="REPLOSSTYPE">
										<option value="1" selected="selected">临时挂失</option>
										<option value="2">正式挂失</option>
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
