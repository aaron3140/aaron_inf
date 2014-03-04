<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_01_003.txt");
	pageContext.setAttribute("map",map);
	pageContext.setAttribute("ORDERNO", System.currentTimeMillis() + 123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
	</head>
	
	<body>

		<form name="trade" method="post"
			action="${sessionScope.path}/httppost">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="01_003">
			<input type="hidden" name="method" value="trade">
			<table>
				<tr>
					<td><font color="red" size="5">交易接口</font></td>
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
						<input name="SIGN" type="text" size="20" maxlength="50"
							value="SIGN">
					</td>
				</tr>
				
				<tr>
					<td>
						CER：
						<input name="CER" type="text" size="20" maxlength="50"
							value="CER">
					</td>
				</tr>
				
				--%><tr><td height="50">请求参数：</td></tr>
				<tr>
					<td>
						AGENTCODE：
						<input name="AGENTCODE" type="text" size="54" maxlength="54"
							value="${map.AGENTCODE}">
					</td>
				</tr>

				<tr>
					<td>
						AREACODE：
						<input name="AREACODE" type="text" size="20" maxlength="6"
							value="${map.AREACODE}">
					</td>
				</tr>
				
				<tr>
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
				</tr>
				
				<tr>
					<td>
						TXNAMOUNT：
						<input name="TXNAMOUNT" type="text" size="20" maxlength="12"
							value="${map.TXNAMOUNT}">
					</td>
				</tr>
				
				<tr>
					<td>
						PAYEECODE：
						<input name="PAYEECODE" type="text" size="20" maxlength="50"
							value="${map.PAYEECODE}">
					</td>
				</tr>
				
				<tr>
					<td>
						GOODSCODE：
						<input name="GOODSCODE" type="text" size="20" maxlength="15"
							value="${map.GOODSCODE}">
					</td>
				</tr>
				
				<tr>
					<td>
						GOODSNAME：
						<input name="GOODSNAME" type="text" size="20" maxlength="50"
							value="${map.GOODSNAME}">
					</td>
				</tr>
				
				<tr>
					<td>
						ORDERSEQ：
						<input name="ORDERSEQ" type="text" size="20" maxlength="25"
							value="${ORDERNO}">
					</td>
				</tr>
				
				<tr>
					<td>
						TRANSSEQ：
						<input name="TRANSSEQ" type="text" size="20" maxlength="15"
							value="${map.TRANSSEQ}">
					</td>
				</tr>
				
				<tr>
					<td>
						TRADETIME：
						<input name="TRADETIME" type="text" size="20" maxlength="14"
							value="${map.TRADETIME}">
					</td>
				</tr>
				
				<tr>
					<td>
						MARK1：
						<input name="MARK1" type="text" size="20" maxlength="200"
							value="${map.MARK1}">
					</td>
				</tr>
				
				<tr>
					<td>
						MARK2：
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
