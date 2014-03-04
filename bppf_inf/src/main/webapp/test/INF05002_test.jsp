<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF05002.txt");
	pageContext.setAttribute("map",map);
	pageContext.setAttribute("ORDERNO", System.currentTimeMillis() + 123456);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	String dateStr = sdf.format(new Date());
	pageContext.setAttribute("TRADETIME",dateStr);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
	</head>
	 
	<body>

		<form name="trade" method="post"
			action="${sessionScope.path}/httpipos">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF05002">
			<input type="hidden" name="method" value="inf05002">
			<table>
				<tr>
					<td><font color="red" size="5">支付插件交易接口</font></td>
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
				
			<tr><td height="50">请求参数：</td></tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>用户名：
						<input name="STAFFCODE" type="text"  maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>密码：
						<input name="PASSWORD" type="text"  maxlength="50"
							value="${map.PASSWORD}">(加密后交易密码)
					</td>
				</tr>
				<tr>
					<td>
						资金来源：
						<select name="FUNDSOURCE">
							<option value="" selected="selected">不选</option>
							<option value="0" >交费易账户</option>
							<option value="1">授权银行卡</option>
						</select>(不选默认为交费易账户)
					</td>
				</tr>
				<tr>
					<td>
						手机验证码：
						<input name="VERIFYCODE" type="text"  maxlength=3154"
							value="${map.VERIFYCODE}">
					</td>
				</tr>
				<tr>
		
					<td></br>以下所有参数为插件调用者传送：</br>
						<span style="color:red" type="BT001">*</span>订单号：
						<input name="ORDERSEQ" type="text" size="20" maxlength="25"
							value="${ORDERNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>付款商户编码：
						<input name="AGENTCODE" type="text" size="54" maxlength="54"
							value="${map.AGENTCODE}">
					</td>
				</tr>
		
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>区域编码：
						<input name="AREACODE" type="text" size="20" maxlength="6"
							value="${map.AREACODE}">
						<input name="ACTIONCODE" type="hidden" size="20" maxlength="6" 
							value="1030">
					</td>
				</tr>
				
				<!-- <tr>
					<td>
						ACTIONCODE：
						<select name="ACTIONCODE">
							<option value="1030" selected="selected">直接交易</option>
							<option value="1040">担保交易</option>
							<option value="1041">担保交易确认</option>
							<option value="1042">担保交易取消</option>
							<option value="1050">预授权</option>
							<option value="1051">预授权确认</option>
							<option value="1052">预授权取消</option>
						</select>
					</td>
				</tr> -->
				
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>交易金额：
						<input name="TXNAMOUNT" type="text" size="20" maxlength="12"
							value="${map.TXNAMOUNT}">(单位：分)
					</td>
				</tr>
				
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>收款商户编码：
						<input name="PAYEECODE" type="text" size="20" maxlength="50"
							value="${map.PAYEECODE}">
					</td>
				</tr>
				
				<tr>
					<td>
						外系统商品编码：
						<input name="GOODSCODE" type="text" size="20" maxlength="15"
							value="${map.GOODSCODE}">（使用翼支付企业账户平台进行购买的商品编码,用于统计作用）
					</td>
				</tr>
				
				<tr>
					<td>
						外系统商品名称：
						<input name="GOODSNAME" type="text" size="20" maxlength="50"
							value="${map.GOODSNAME}">（使用翼支付企业账户系统平台进行购买的商品名称）
					</td>
				</tr>
				
				
				
				<tr>
					<td>
						交易流水号：
						<input name="TRANSSEQ" type="text" size="20" maxlength="15"
							value="${map.TRANSSEQ}">
					</td>
				</tr>
				
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>交易时间：
						<input name="TRADETIME" type="text" size="20" maxlength="14"
							value="${map.TRADETIME}">（格式：yyyyMMddHHmmss）
					</td>
				</tr>
				
				<tr>
					<td>
						备注字段1：
						<input name="MARK1" type="text" size="20" maxlength="200"
							value="${map.MARK1}">
					</td>
				</tr>
				
				<tr>
					<td>
						备注字段2：
						<input name="MARK2" type="text" size="20" maxlength="200"
							value="${map.MARK2}">
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
