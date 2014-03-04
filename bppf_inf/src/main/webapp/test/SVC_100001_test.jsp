<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="100001">
			<input type="hidden" name="method" value="accountInfo">
			<table>
				<tr>
					<td><font color="red" size="5">账户信息查询接口</font></td>
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
						商户所在区域：
						<select name="AREACODE">
										<option value="" selected="selected">--所有区域--</option>
										<option value="000001">广东</option>
										<option value="000002">上海</option>
										<option value="000002">北京</option>
										<option value="000002">重庆</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						操作人：
						<input name="STAFFCODE" type="text" size="15" maxlength="20"
							value="张三">
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
