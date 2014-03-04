<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" scope="request"
	value="${pageContext.request.contextPath}" />
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map = FileUtil.getPageparams("WEB_INF02028.txt");
	pageContext.setAttribute("map", map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("TRADETIME", sdf.format(new Date()));
	pageContext.setAttribute("ORDERNO", System.currentTimeMillis() + 123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<title>有线电视交易接口</title>
		<script type="text/javascript" src="${ctx}/js/jquery-1.7.1.min.js"></script>
	</head>
	<body>
		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16"
				value="INF02028">
			<input type="hidden" name="method" value="inf02028">
			<table>
				<tr>
					<td>
						<font color="red" size="5">有线电视交易接口</font>
					</td>
				</tr>
				<tr>
					<td height="50">
						校验参数：
					</td>
				</tr>
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
				<tr>
					<td height="50">
						请求参数：
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span> 订单号：
						<input name="ORDERNO" type="text" size="32" maxlength="32"
							value="${ORDERNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="32" maxlength="32"
							value="${map.CUSTCODE}">
						<span style="color: red">相当于企业账户编码，由翼支付平台提供。</span>
					</td>
				</tr>
				<tr>
					<td>
						客户终端号：
						<input name="TMNNUMNO" type="text" size="32" maxlength="12"
							value="${map.TMNNUMNO}">
						<span style="color: red">由翼支付系统为客户生成的12位终端号或8位外部终端号</span>
					</td>
				</tr>
				<tr>
					<td>
						支付方式：
						<select name="PAYTYPE" style="width: 100px;">
							<option value="0">
								其它
							</option>
							<option value="9" selected="selected">
								纯业务
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						业务类型：
						<select name="BUSTYPE" style="width: 100px;">
							<option value="0100">
								珠江数码月租费
							</option>
							<!--  
							<option value="0111">
								珠江数码预付费
							</option>
							<option value="0112">
								珠江宽频月租费
							</option>
							<option value="0113">
								珠江宽频预付费
							</option>
							<option value="0114">
								有线电视月租费
							</option>
							<option value="0115">
								有线电视预付费
							</option>
							<option value="0172">
								珠江宽频劲速套餐续费1000元包14个月
							</option>
							-->
							
							<option value="0118">
								高清互动电视费
							</option>
							<option value="0120">
								珠江宽频优惠套餐
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span>系统参考号：
						<input name="SYSTEMNO" type="text" size="32" maxlength="15"
							value="${map.SYSTEMNO}">
						<span style="color: red"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span>充值缴费金额：
						<input name="TXNAMOUNT" type="text" size="32" maxlength="14"
							value="${map.TXNAMOUNT}">
						<span style="color: red"></span>
					</td>
				</tr>


				<tr>
					<td>
						联系电话：
						<input name="CONTACTPHONE" type="text" size="32" maxlength="20"
							value="${map.CONTACTPHONE}">
						<span style="color: red">用于查询欠费账单的电话号码</span>
					</td>
				</tr>
				<tr>
					<td>
						联系地址：
						<input name="CONTACTADDR" type="text" size="32" maxlength="64"
							value="${map.CONTACTADDR}">
						<span style="color: red">用户的联系地址</span>
					</td>
				</tr>
				<tr>
					<td>
						企业账户用户名：
						<input name="OPERUSER" type="text" size="32" maxlength="64"
							value="${map.OPERUSER}">
						<span style="color: red">支付用户名</span>
					</td>
				</tr>
				<tr>
					<td>
						企业账户交易密码：
						<input name="OPERPASSWORD" type="text" size="32" maxlength="128"
							value="${map.OPERPASSWORD}">
						<span style="color: red">加密后的交易密码</span>
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="25" maxlength="200"
							value="${map.REMARK1}">
						<span style="color: red">预留</span>
					</td>
				</tr>

				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="25" maxlength="200"
							value="${map.REMARK2}">
						<span style="color: red">预留</span>
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
