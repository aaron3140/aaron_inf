<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02035.txt");
	pageContext.setAttribute("map",map);
	
	pageContext.setAttribute("ORDERNO",System.currentTimeMillis()+123456);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  </head>
  
  <body>
    <form name="paymentInfo" method="post"
			action="${sessionScope.path}/httpipos">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02035">
			<input type="hidden" name="method" value="inf02035">
			<table>
				<tr>
					<td><font color="red" size="5">全国固话宽带充值接口</font></td>
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
						订单号：
						<input name="ORDERSEQ" type="text" size="15" maxlength="25"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						客户编码：
						<input name="CUSTCODE" type="text" size="32" maxlength="32"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						用户名：
						<input name="STAFFCODE" type="text" size="50" maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						支付密码：
						<input name="PAYPASSWORD" type="text" size="32" maxlength="32"
							value="${map.PAYPASSWORD}">
					</td>
				</tr>
				<tr>
					<td>
						系统参考号:
						<input name="SYSTEMNO" type="text" size="15" maxlength="15"
							value="${map.SYSTEMNO}">
					</td>
				</tr>
				<tr>
					<td>
						业务编码：
						<select name="BUSICODE" id="BUSICODE">
							<option value="03010200">全国电信宽带</option>
							<option value="03010100">全国电信固话</option>				
						</select>
					</td>
				</tr>
				<tr>
					<td>
						受理区域编码：
						<input name="ACCEPTAREACODE" type="text" size="6" maxlength="6"
							value="${map.ACCEPTAREACODE}">
					</td>
				</tr>
				<tr>
					<td>
						账户号码：
						<input name="ACCTCODE" type="text" size="64" maxlength="64"
							value="${map.ACCTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						受理时间：
						<input name="TRADETIME" type="text" size="14" maxlength="14"
							value="${map.TRADETIME}">
					</td>
				</tr>
				<tr>
					<td>
						交易金额：
						<input name="TXNAMOUNT" type="text" size="14" maxlength="14"
							value="${map.TXNAMOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="15" maxlength="200"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="15" maxlength="200"
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
