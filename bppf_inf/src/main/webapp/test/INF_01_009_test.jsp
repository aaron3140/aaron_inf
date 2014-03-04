<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_01_009.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="01_009">
			<input type="hidden" name="method" value="inf01009">
			<table>
				<tr>
					<td><font color="red" size="5">充值缴费类下单接口</font></td>
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
						系统参考号：
						<input name="EVENTSEQ" type="text" size="15" maxlength="30"
							value="${map.EVENTSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						充值缴费对象标识：
						<input name="OBJCODE" type="text" size="15" maxlength="30"
							value="${map.OBJCODE}">
					</td>
				</tr>
				<tr>
					<td>
						充值缴费对象类型：
						<select name="OBJTYPE">
							<option value="0">其它</option>
							<option value="1" selected="selected">手机号</option>
							<option value="2">QQ号</option>
							<option value="3">身份证号</option>
							<option value="4">翼支付账户</option>
							<option value="5">户号</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						充值缴费金额：
						<input name="PAYAMOUNT" type="text" size="15" maxlength="10"
							value="${map.PAYAMOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						充值缴费时间：
						<input name="PAYTIME" type="text" size="15" maxlength="14"
							value="${map.PAYTIME}">
					</td>
				</tr>
				<tr>
					<td>
						充值缴费单位编码：
						<input name="ORGCODE" type="text" size="15" maxlength="30"
							value="${map.ORGCODE}">
					</td>
				</tr>
				<tr>
					<td>
						回调地址：
						<input name="CALLBACKURL" type="text" size="15" maxlength="200"
							value="${map.CALLBACKURL}">
					</td>
				</tr>
				<tr>
					<td>
						订单扩展属性：
						<input name="EXTITEM1" type="text" size="15" maxlength="25"
							value="${map.EXTITEM1}">
						<input name="EXTITEM2" type="text" size="15" maxlength="25"
							value="${map.EXTITEM2}">
						<input name="EXTITEM3" type="text" size="15" maxlength="25"
							value="${map.EXTITEM3}">
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
