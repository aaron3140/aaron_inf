<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_01_002.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="01_002">
			<input type="hidden" name="method" value="transactionInfo">
			<table>
				<tr>
					<td><font color="red" size="5">交易查询接口</font></td>
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
						<span style="color:red">*</span>客户编码：
						<input name="AGENTCODE" type="text" size="15" maxlength="50"
							value="${map.AGENTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>查询时间：
						<input name="SEARCHTIME" type="text" size="15" maxlength="14"
							value="${map.SEARCHTIME}">
					</td>
				</tr>
				<tr>
					<td>
						交易起始时间：
						<input name="STARTDATE" type="text" size="15" maxlength="14"
							value="${map.STARTDATE}">
					</td>
				</tr>
				<tr>
					<td>
						交易结束时间：
						<input name="ENDDATE" type="text" size="15" maxlength="14"
							value="${map.ENDDATE}">
					</td>
				</tr>
				<tr>
					<td>
						订单编码：
						<input name="ORDERSEQ" type="text" size="15" maxlength="25"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						交易流水号：
						<input name="TRANSSEQ" type="text" size="15" maxlength="15"
							value="${map.TRANSSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						订单类型：
						<select name="ORDERTYPE">
										<option value="1">银行卡充值</option>
										<option value="2">网银充值</option>
										<option value="3" selected="selected">付款</option>
										<option value="4">担保交易</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						订单状态编码：
						<select name="ORDERSTAT">
										<option value="1" selected="selected">完成</option>
										<option value="2">失败</option>
										<option value="3">等待支付</option>
										<option value="4">异常</option>
										<option value="5">已取消</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						交易区域：
						<input name="AREACODE" type="text" size="15" maxlength="10"
							value="${map.MERID}">
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
