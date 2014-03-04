<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02033.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("TRADETIME",sdf.format(new Date()));
	pageContext.setAttribute("ORDERSEQ",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02033">
			<input type="hidden" name="method" value="inf02033">
			<table>
				<tr>
					<td><font color="red" size="5">QQ下单接口</font></td>
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
						<span style="color:red">*</span>订单号:
							<input name="ORDERSEQ" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${ORDERSEQ}">
					</td>
				</tr>
<!-- 				
				<tr>
					<td>
						用户名:
							<input name="STAFFCODE" type="text" size="50" maxlength="32" id="custCodeValId"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						交易密码:
							<input name="PAYPASSWORD" type="text" size="50" maxlength="32" id="custCodeValId"
							value="${map.PAYPASSWORD}">
					</td>
				</tr> -->
				<tr>
					<td>
						<span style="color:red">*</span>客户编码:
							<input name="CUSTCODE" type="text" size="15" maxlength="32" id="custCodeValId"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>产品编码：
						<select name="PRODUCTCODE">
										<option value="0031" selected="selected">Q币</option>
										<option value="0032">QQ会员</option>
										<option value="0033">QQ紫钻</option>
										<option value="0034">QQ红钻</option>
										<option value="0035">QQ蓝钻</option>
										<option value="0036">QQ黄钻</option>
										<option value="0037">QQ绿钻</option>
										<option value="0038">QQ点</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						受理区域编码:
						<input name="ACCEPTAREACODE" type="text" size="25" maxlength="20"
							value="${map.ACCEPTAREACODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>账户号码:
						<input name="ACCTCODE" type="text" size="25" maxlength="20"
							value="${map.ACCTCODE}">(*QQ号码)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>受理时间：
						<input name="TRADETIME" type="text" size="25" maxlength="14"
							value="${TRADETIME}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>充值数量：
						<input name="RECHARGEAMOUNT" type="text" size="25" maxlength="14"
							value="${map.RECHARGEAMOUNT}">(*产品编码为0031和0038时单位为：个，其余单位为：月)
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="15" maxlength="32"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="15" maxlength="10"
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
