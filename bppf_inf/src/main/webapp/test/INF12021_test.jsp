<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF12021.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("TRADETIME",sdf.format(new Date()));
	pageContext.setAttribute("ORDERNO",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">


<html>
<head>
<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
<script type="text/javascript">

function onlaod(){
	
	onChannelChange();
}

	function onChannelChange(){
	
	var CHANNELCODE = $("#CHANNELCODE").find("option:selected").val();

	var channel = $(".channel");

	if("60"==CHANNELCODE){

		channel.show();

	}else{

		channel.hide();
	}
	
}

</script>
</head>
	
	<body >

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF12021">
			<input type="hidden" name="method" value="inf12021">
			<table>
				<tr>
					<td><font color="red" size="5">3G流量卡充值接口</font></td>
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
							<select name="CHANNELCODE" id="CHANNELCODE" >
								<option value="80" selected="selected">80</option>
							</select>
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
					<td><span style="color:red">*</span>
						订单号：
						<input name="ORDERNO" type="text" size="25" maxlength="25"
							value="${ORDERNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码:
							<input name="CUSTCODE" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<!-- 
				<tr>
					<td>
						客户终端号：
						<input name="TMNNUMNO" type="text" size="15" maxlength="12"
							value="${map.TMNNUMNO}">(渠道号为60时必填（8位外部终端号）非60渠道时可不填)
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
				<tr>
					<td>
						<span style="color:red">*</span>用户名:
							<input name="STAFFCODE" type="text" size="32" maxlength="32" id="staffCode"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						交易密码:
							<input name="PAYPASSWORD" type="text" size="32" maxlength="32" id="staffCode"
							value="${map.PAYPASSWORD}"> <span style="color:red">渠道号为60(IPOS)送BCD密码；其它渠道为必送,累积限额内免输入</span>
					</td>
				</tr>
				 -->
				<tr>
					<td>
						充值类型：
						<select name="RECHARGETYPE">
										<option value="001" selected="selected">直充</option>
										<!-- 
										<option value="002">常规卡</option>
										<option value="003">促销卡</option>
										 -->
						</select>
					</td>
				</tr>
				<!-- 
				<tr class="channel">
					<td>
						PSAM卡号：
						<input name="PSAMCARDNO" type="text" size="15" maxlength="16"
							value="${map.PSAMCARDNO}">当渠道为60时 必填
					</td>
				</tr>
				<tr class="channel">
					<td>
						E卡号：
						<input name="ECARDNO" type="text" size="15" maxlength="16"
							value="${map.ECARDNO}">当渠道为60时 必填
					</td>
				</tr>
				<tr class="channel">
					<td>
						密码类型：
							<select name="PASSFLAG">
							<option value="2" selected="selected">交易密码</option>
							<option value="1">转账密码或老板密码</option>
						</select>当渠道为60时 必填
					</td>
				</tr>
				 -->
				<tr>
					<td>
						是否验证手机号：
						<select name="VERIFY">
										<option value="00" selected="selected">需要验证</option>
										<option value="01">不需要验证</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>充入手机号码:
							<input name="PHONE" type="text" size="11" maxlength="11" id="staffCode"
							value="${map.PHONE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>充入流量:
							<input name="RECHARGEFLOW" type="text" size="12" maxlength="12" id="staffCode"
							value="${map.RECHARGEFLOW}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>订单金额:
							<input name="TXNAMOUNT" type="text" size="12" maxlength="12" id="staffCode"
							value="${map.TXNAMOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						受理区域编码:
							<input name="ACCEPTAREACODE" type="text" size="6" maxlength="6" id="staffCode"
							value="${map.ACCEPTAREACODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>受理时间:
							<input name="TRADETIME" type="text" size="14" maxlength="14" id="tradeTime"
							value="${TRADETIME}">
					</td>
				</tr>
			
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="200" maxlength="200"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="200" maxlength="200"
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
