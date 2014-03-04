<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
	</head>
	
	<body>

		<form name="trade" method="post" action="${sessionScope.path}/GWPayAccept.do">
			<table>
				<tr>
					<td><font color="red" size="5">网关支付</font></td>
				</tr>
				<tr>
					<td>
						支付接口的名称：
						<input name="INTERFACENAME" type="text" size="20" maxlength="30"
							value="BESTPAY_B2C">
					</td>
				</tr>

				<tr>
					<td>
						支付接口的版本号：
						<input name="INTERFACEVERSION" type="text" size="20" maxlength="15"
							value="1.0.0.0">
					</td>
				</tr>
				
				<tr>
					<td>
						回传URL：
						<input name="CALLBACKURL" type="text" size="20" maxlength="255"
							value="http://">
					</td>
				</tr>
				
				<tr>
					<td>
						商户号：
						<input name="MERID" type="text" size="20" maxlength="20"
							value="201202291324250001">
					</td>
				</tr>
				
				<tr>
					<td>
						订单号：
						<input name="ORDERSEQ" type="text" size="20" maxlength="15"
							value="123456789123456">
					</td>
				</tr>
				
				<tr>
					<td>
						流水号 ：
						<input name="ORDERTRANSEQ" type="text" size="20" maxlength="15"
							value="123456789111111">
					</td>
				</tr>
				
				<tr>
					<td>
						币种编码：
						<input name="CURTYPE" type="text" size="20" maxlength="10"
							value="RMB">
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
						订单时间：
						<input name="ORDERDATE" type="text" size="20" maxlength="14"
							value="20120229132425">
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
						附加信息：
						<input name="ATTACH" type="text" size="20" maxlength="128"
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
						保留值1：
						<input name="REMARK1" type="text" size="20" maxlength="256"
							value="">
					</td>
				</tr>
				
				<tr>
					<td>
						保留值2：
						<input name="REMARK2" type="text" size="20" maxlength="128"
							value="">
					</td>
				</tr>
				
				<tr>
					<td>
						MAC校验域：
						<input name="MAC" type="text" size="20" maxlength="128"
							value="31313141321321414213123131">
					</td>
				</tr>
				
				<tr>
					<td>
						签名域：
						<input name="SIGNMSG" type="text" size="20" maxlength="256"
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
