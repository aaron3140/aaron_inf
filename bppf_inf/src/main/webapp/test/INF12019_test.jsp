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
	java.util.Map map = FileUtil.getPageparams("WEB_INF12019.txt");
	pageContext.setAttribute("map", map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("TRADETIME", sdf.format(new Date()));
	pageContext.setAttribute("ORDERNO", System.currentTimeMillis() + 123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<title>游戏充值接口</title>
		<script type="text/javascript" src="${ctx}/js/jquery-1.7.1.min.js"></script>

		<script type="text/javascript">
		 //魔兽世界
		 function moneyMS(){
		     $("#money").html("<select name='RECHARGEAMOUNT' style='width: 100px;'><option value='30'>30</option><option value='60'>60</option><option value='90'>90</option><option value='150'>150</option><option value='180'>180</option></select>");
		     $("#zhanghao1").html("</span>战网账号：<input name='BATTLEACCT' type='text' size='25' maxlength='64'value='${map.BATTLEACCT}'><span style='color: red'>当游戏编码为1001魔兽世界时必填为战网账号</span>");
		     $("#zhanghao2").html("<span style='color: red'></span>游戏账号：<input name='GAMEACCT' type='text' size='25' maxlength='64'value='${map.GAMEACCT}'><span style='color: red'>需要充值的游戏账号,当游戏编码为1001魔兽世界并且只有一个游戏账号时可不填，将默认充到这个游戏账号中</span>");
		 }
		 
		  //街头蓝球 蒸汽幻想/拍拍部落 希望/问道
		 function moneyJTPPWD(){
		     $("#money").html("<select name='RECHARGEAMOUNT' style='width: 100px;'><option value='10'>10</option><option value='20'>20</option><option value='50'>50</option><option value='100'>100</option></select>");
		     $("#zhanghao1").html("");
		     $("#zhanghao2").html("<span style='color: red'></span>游戏账号：<input name='GAMEACCT' type='text' size='25' maxlength='64'value='${map.GAMEACCT}'><span style='color: red'>需要充值的游戏账号,当游戏编码为1001魔兽世界并且只有一个游戏账号时可不填，将默认充到这个游戏账号中</span>");
		 }
		 
		 //冰川一卡通（远征ＯＬ） 
		 function moneyBC(){
		     $("#money").html("<select name='RECHARGEAMOUNT' style='width: 100px;'><option value='10'>10</option></select>");
		     $("#zhanghao1").html("");
		     $("#zhanghao2").html("<span style='color: red'></span>游戏账号：<input name='GAMEACCT' type='text' size='25' maxlength='64'value='${map.GAMEACCT}'><span style='color: red'>需要充值的游戏账号,当游戏编码为1001魔兽世界并且只有一个游戏账号时可不填，将默认充到这个游戏账号中</span>");
		 }
		
		</script>
	</head>
	<body>
		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16"
				value="INF12019">
			<input type="hidden" name="method" value="inf12019">
			<table>
				<tr>
					<td>
						<font color="red" size="5">游戏充值接口</font>
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
						<input name="ORDERNO" type="text" size="25" maxlength="25"
							value="${ORDERNO}">
					</td>
				</tr>

				<tr>
					<td>
						<span style="color: red">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="25" maxlength="32"
							value="${map.CUSTCODE}">
						<span style="color: red">相当于商户编码，由翼支付平台提供。</span>
					</td>
				</tr>
				<!-- 
				<tr>
					<td>
						<span style="color: red">*</span>用户名：
						<input name="STAFFCODE" type="text" size="25" maxlength="50"
							value="${map.STAFFCODE}">
						<span style="color: red">支付用户名</span>
					</td>
				</tr>
				<tr>
					<td>
						交易密码：
						<input name="PAYPASSWORD" type="text" size="25" maxlength="32"
							value="${map.PAYPASSWORD}">
						<span style="color: red">加密后的交易密码,累积限额内免输入</span>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span>支付方式：
						<select name="PAYTYPE" style="width: 100px;">
							<option value="0">
								其它
							</option>
							<option value="9">
								纯业务
							</option>
						</select>
					</td>
				</tr>
				 -->
				<tr>
					<td>
						<span style="color: red">*</span>游戏编码：
						<input type="radio" onclick="moneyMS();" name="GAMECODE"
							value="1001" checked="checked" />
						魔兽世界（战网一卡通）
						<input type="radio" onclick="moneyJTPPWD();" name="GAMECODE"
							value="2001" />
						街头蓝球
						<input type="radio" onclick="moneyJTPPWD();" name="GAMECODE"
							value="2002" />
						蒸汽幻想/拍拍部落
						<input type="radio" onclick="moneyJTPPWD();" name="GAMECODE"
							value="2003" />
						希望/问道
						<input type="radio" onclick="moneyBC();" name="GAMECODE"
							value="2004" />
						冰川一卡通（远征ＯＬ）
					</td>
				</tr>
				<tr>
					<td>
						<div id="zhanghao1">
							<span style="color: red"></span>战网账号：
							<input name="BATTLEACCT" type="text" size="25" maxlength="64"
								value="${map.BATTLEACCT}">
							<span style="color: red">当游戏编码为1001魔兽世界时必填为战网账号</span>
						</div>

					</td>
				</tr>
				<tr>
					<td>
						<div id="zhanghao2">
							<span style="color: red"></span>游戏账号：
							<input name="GAMEACCT" type="text" size="25" maxlength="64"
								value="${map.GAMEACCT}">
							<span style="color: red">需要充值的游戏账号,当游戏编码为1001魔兽世界并且只有一个游戏账号时可不填，将默认充到这个游戏账号中</span>
						</div>
					</td>
				</tr>

				<tr>
					<td>
						<table>
							<tr>
								<td>
									<span style="color: red">*</span>充入面值：
								</td>
								<td>
									<div id="money">
										<select name="RECHARGEAMOUNT" style="width: 100px;">
											<option value="30">
												30
											</option>
											<option value="60">
												60
											</option>
											<option value="90">
												90
											</option>
											<option value="150">
												150
											</option>
											<option value="180">
												180
											</option>
										</select>
									</div>
									<span style="color: red"></span>
								</td>
							</tr>
						</table>
					</td>
				</tr>

				<tr>
					<td>
						<span style="color: red">*</span>订单金额：
						<input name="ORDERAMOUNT" type="text" size="25" maxlength="12"
							value="${map.ORDERAMOUNT}">
						<span style="color: red">以分为单位 指订单总金额</span>
					</td>
				</tr>

				<tr>
					<td>
						<span style="color: red">*</span>受理时间：
						<input name="TRADETIME" type="text" size="25" maxlength="14"
							value="${TRADETIME}">
						<span style="color: red">发起方的受理时间yyyyMMddHHmmss格式</span>
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
