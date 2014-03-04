<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
	</head>
	
	<body>

		<form name="trade" method="post" action="${sessionScope.path}/gatewayToPay.do">
			<table>
				<tr>
					<td><font color="red" size="5">网关支付</font></td>
				</tr>
				<tr>
					<td>
						SP身份标识：
						<input name="MERCHANTID" type="text" size="20" maxlength="30"
							value="1122334455">
					</td>
				</tr>

				<tr>
					<td>
						子SP身份标识：
						<input name="SUBMERCHANTID" type="text" size="20" maxlength="30"
							value="">
					</td>
				</tr>
				
				<tr>
					<td>
						订单号：
						<input name="ORDERSEQ" type="text" size="20" maxlength="30"
							value="1234567890">
					</td>
				</tr>
				
				<tr>
					<td>
						交易流水号：
						<input name="ORDERREQTRANSEQ" type="text" size="20" maxlength="30"
							value="201202291324250001">
					</td>
				</tr>
				
				<tr>
					<td>
						订单日期：
						<input name="ORDERDATE" type="text" size="20" maxlength="8"
							value="20120229">
					</td>
				</tr>
				
				<tr>
					<td>
						订单总金额：
						<input name="ORDERAMOUNT" type="text" size="20" maxlength="10"
							value="1">
					</td>
				</tr>
				
				<tr>
					<td>
						产品金额：
						<input name="PRODUCTAMOUNT" type="text" size="20" maxlength="10"
							value="1">
					</td>
				</tr>
				
				<tr>
					<td>
						附加金额：
						<input name="ATTACHAMOUNT" type="text" size="20" maxlength="10"
							value="0">
					</td>
				</tr>
				
				<tr>
					<td>
						币种：
						<input name="CURTYPE" type="text" size="20" maxlength="10"
							value="RMB">
					</td>
				</tr>
				
				<tr>
					<td>
						加密方式：
						<input name="ENCODETYPE" type="text" size="20" maxlength="1"
							value="1">
					</td>
				</tr>
				
				<tr>
					<td>
						前台返回地址：
						<input name="MERCHANTURL" type="text" size="20" maxlength="255"
							value="http://">
					</td>
				</tr>
				
				<tr>
					<td>
						后台返回地址：
						<input name="BACKMERCHANTURL" type="text" size="20" maxlength="255"
							value="http://">
					</td>
				</tr>
				
				<tr>
					<td>
						附加信息：
						<input name="ATTACH" type="text" size="20" maxlength="128"
							value="">
					</td>
				</tr>
				
				<tr>
					<td>
						业务类型代码：
						<input name="BUSICODE" type="text" size="20" maxlength="10"
							value="0001">
					</td>
				</tr>
				
				<tr>
					<td>
						终端号码：
						<input name="TMNUM" type="text" size="20" maxlength="40"
							value="">
					</td>
				</tr>
				
				<tr>
					<td>
						客户标识：
						<input name="CUSTOMERID" type="text" size="20" maxlength="32"
							value="">
					</td>
				</tr>
				
				<tr>
					<td>
						产品标识：
						<input name="PRODUCTID" type="text" size="20" maxlength="10"
							value="">
					</td>
				</tr>
				
				<tr>
					<td>
						产品描述：
						<input name="PRODUCTDESC" type="text" size="20" maxlength="128"
							value="">
					</td>
				</tr>
				
				<tr>
					<td>
						MAC校验域：
						<input name="MAC" type="text" size="20" maxlength="256"
							value="31313141321321414213123131">
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
