<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_01_004.txt");
	pageContext.setAttribute("map",map);
	
	pageContext.setAttribute("ORDERNO",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="01_004">
			<input type="hidden" name="method" value="accountManage">
			<table>
				<tr>
					<td><font color="red" size="5">账户管理接口</font></td>
				</tr>
				
				<tr><td height="50">校验参数：</td></tr>
				<tr>
					<td>
						MERID：
						<input name="MERID" type="text" size="20" maxlength="50"
							value="${map.MERID}">
					</td>
				</tr>
				
				<tr>
					<td>
						CHANNELCODE：
						<input name="CHANNELCODE" type="text" size="20" maxlength="2"
							value="${map.CHANNELCODE}">
					</td>
				</tr>
				
				<tr>
					<td>
						TMNNUM：
						<input name="TMNNUM" type="text" size="20" maxlength="15"
							value="${map.TMNNUM}">
					</td>
				</tr>
				
				<%--<tr>
					<td>
						SIGN：
						<input name="SIGN" type="text" size="20" maxlength="4096"
							value="SIGN">
					</td>
				</tr>
				
				<tr>
					<td>
						CER：
						<input name="CER" type="text" size="20" maxlength="4096"
							value="CER">
					</td>
				</tr>
				
				--%><tr><td height="50">请求参数：</td></tr>
				<tr>
					<td>
						订单号：
						<input name="ORDERSEQ" type="text" size="15" maxlength="25"
							value="${ORDERNO}">
					</td>
				</tr>
				<tr>
					<td>
						用户：
						<input name="STAFFCODE" type="text" size="15" maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						支付密码：
						<input name="PASSWORD" type="text" size="15" maxlength="50"
							value="${map.PASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						商户编码：
						<input name="AGENTCODE" type="text" size="15" maxlength="50"
							value="${map.AGENTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						操作类型：
						<select name="OPERTYPE">
										<option value="1" selected="selected">充值</option>
										<option value="2">提现</option>
						</select>
					</td>
				</tr>
				<!-- 
				<tr>
					<td>
						<span style="color:red">*</span>转账标识：
						<select name="TRANSFERFLAG">
							<option value="00" selected="selected">否</option>
							<option value="01">是</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						收款方客户编码：
						<input name="COLLECUSTCODE" type="text" size="20" maxlength="54"
							value="${map.COLLECUSTCODE}">
					</td>
				</tr>
				 -->
				<tr>
					<td>
						账户类型：
						<select name="ACCTTYPE">
										<option value="0001" selected="selected">企业账户</option>
										<option value="0007">IPOS账户</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						金额：
						<input name="TXNAMOUNT" type="text" size="15" maxlength="12"
							value="${map.TXNAMOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="15" maxlength="12"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="15" maxlength="12"
							value="${map.REMARK2}">
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
