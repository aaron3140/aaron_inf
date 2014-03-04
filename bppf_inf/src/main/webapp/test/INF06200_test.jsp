<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06200.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	String dateStr = sdf.format(new Date());
	pageContext.setAttribute("acctDate",dateStr);
	pageContext.setAttribute("orderSeq",System.currentTimeMillis());
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<style type="text/css">
.BT001{
	display: block;
}
.BT002{
	display: none;
}
.payTypeQiYe{
	margin: 0;
}
.payTypePos{
	margin: 0;
}
.payTypeDaiShouFu{
	margin: 0;
}
.payTypeDaiFu{
	margin: 0;
}
.cardFlagXinYongKa{
	margin: 0;
}
</style>

<html>
	<head>
	<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript">
	
		function onlaod(){
			onPayTypeChange();
			onCardFlagChange();
		}
		function onCardFlagChange(){
			var v = $("#cardFlag").find("option:selected").val();
			var elXyk = $(".cardFlagXinYongKa");
				if("2"==v){
					elXyk.show();
				}else{
					elXyk.hide();
				}
		}

		//支付方式
		function onPayTypeChange(){
			var payTypeVal = $("#payType").find("option:selected").val();
			var elQiYe = $(".payTypeQiYe");
			var elPos = $(".payTypePos");
			var elDsf = $(".payTypeDaiShouFu");
			var elXyk = $(".cardFlagXinYongKa");
			var elDsfPos = $(".payTypedsfpos");
			if("1"==payTypeVal){
				elQiYe.hide();
				elPos.show();
				elDsf.hide();
				elXyk.hide();
				elDsfPos.show();
			}else if("2"==payTypeVal){
				elQiYe.hide();
				elPos.hide();
				elDsf.hide();
				elDsfPos.show();
				var v = $("#cardFlag").find("option:selected").val();
				if("2"==v){
					elXyk.show();
				}
			}
			
		}
	
		
	</script>
	</head>
	
	<body onload="onlaod()">
		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06200">
			<input type="hidden" name="method" value="inf06200">
			<table>
				<tr>
					<td><font color="red" size="5">全国多媒体付款接口 </font></td>
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
						<span style="color:red" type="BT001">*</span>订单号：
						<input name="ORDERSEQ" type="text" size="15" maxlength="32"
							value="${orderSeq}">
					</td>
				</tr>
				<tr>
					<td>
						客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>客户终端号：
						<input name="TMNNUMNO" type="text" size="15" maxlength="12"
							value="${map.TMNNUMNO}">(注：当渠道为80时，可为终端号，可为外部终端号。当渠道为60时，只能为外部终端号)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>受理时间：
						<input name="ACCEPTDATE" type="text" size="15" maxlength="14"  id="ACCEPTDATE"
							value="${acctDate}"> (格式：YYYYMMDDhhmmss)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>付款金额：
						<input name="TXNAMOUNT" type="text" size="15" maxlength="14"
							value="${map.TXNAMOUNT}">(以分为单位)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>业务对象：
						<input name="BUSOBJECT" type="text" size="30" maxlength="128"
							value="${map.BUSOBJECT}">(一般送缴费编号)
					</td>
				</tr>
					<tr>
					<td>
						<span style="color:red" type="BT001">*</span>银行账号：
						<input name="BANKACCT" type="text" size="15" maxlength="64"
							value="${map.BANKACCT}">(扣款的银行账号)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>对公/对私标识：
							<select name="PRIVATEFLAG">
							<option value="0" selected="selected">对公</option>
							<option value="1">对私</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>卡折标识：
							<select name="CARDFLAG" id="cardFlag" >
							<option value="1" selected="selected">借记卡</option>
							<option value="2">信用卡（贷记卡）</option>
							<option value="4">存折</option>
							<option value="8">公司账户</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>支付密码：
						<input name="PAYPASSWORD" type="text" size="15" maxlength="64"
							value="${map.PAYPASSWORD}">(为付款账户的密码（经加密）)
					</td>
				</tr>
				<tr >
					<td>
						<span style="color:red" type="BT001">*</span>PSAM卡号：
						<input name="PSAMCARDNO" type="text" size="15" maxlength="16"
							value="${map.PSAMCARDNO}">
					</td>
				</tr>
				<tr >
					<td>
						<span style="color:red" type="BT001">*</span>二磁道：
						<input name="TRACKTWO" type="text" size="15" maxlength="79"
							value="${map.TRACKTWO}">
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						三磁道：
						<input name="TRACKTHREE" type="text" size="15" maxlength="108"
							value="${map.TRACKTHREE}">
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						<span style="color:red" type="BT001">*</span>国际网络号：
						<input name="NETWORKNO" type="text" size="15" maxlength="3"
							value="${map.NETWORKNO}"> IC卡：008 磁卡：003）
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK1" type="text" size="20" maxlength="200"
							value="${map.REMARK1}">
					</td>
				</tr>
				
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="20" maxlength="200"
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
