<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06104.txt");
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
.cardFlagXinYongKa{
	margin: 0;
}
</style>

<html>
	<head>
	<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
	
	<script type="text/javascript">
	
		function onlaod(){
			//alert("alert()");
			//onCaseTypeChange();
			//onPayTypeChange();
			//onCaseTypeChange();
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
		function onCardFlagChange(){
			var v = $("#cardFlag").find("option:selected").val();
			var elXyk = $(".cardFlagXinYongKa");
				if("2"==v){
					elXyk.show();
				}else{
					elXyk.hide();
				}
		}
	//销账单号类型
		function onCaseTypeChange(){
			//alert($("#caseType").find("option:selected").text());
			var cashTypeVal = $("#cashType").find("option:selected").val();
			if("0"==cashTypeVal){
				$("#cashOrder").show();
			}else if("1"==cashTypeVal){
				$("#cashOrder").hide();
			}
			
		}

		//支付方式
		function onPayTypeChange(){
			var payTypeVal = $("#payType").find("option:selected").val();
			var elQiYe = $(".payTypeQiYe");
			var elPos = $(".payTypePos");
			var elDf = $(".payTypeDaiShouFu");
			var elXyk = $(".cardFlagXinYongKa");
			var elDsfPos = $(".payTypedsfpos");
			if("0"==payTypeVal){
				elQiYe.show();
				elPos.hide();
				elDf.hide();
				elXyk.hide();
				elDsfPos.hide();
			}else if("1"==payTypeVal){
				elQiYe.hide();
				elPos.show();
				elDf.hide();
				elXyk.hide();
				elDsfPos.show();
			}else if("2"==payTypeVal){
				elQiYe.hide();
				elPos.hide();
				elDf.show();
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06104">
			<input type="hidden" name="method" value="inf06104">
			<table>
				<tr>
					<td><font color="red" size="5">车船税账单缴费 </font></td>
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
							<select name="CHANNELCODE" id="CHANNELCODE" onchange="onChannelChange()">
								<option value="80" selected="selected">80</option>
								<option value="60">60</option>
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
						<input name="ORDERSEQ" type="text" size="25" maxlength="32"
							value="${orderSeq}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">(注：当渠道为60时，可为空)
					</td>
				</tr>
				<tr>
					<td>
						客户终端号：
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
						<span style="color:red" type="BT001">*</span>系统参考号：
						<input name="SYSTEMNO" type="text" size="15" maxlength="15"
							value="${map.SYSTEMNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>交易金额：
						<input name="TXNAMOUNT" type="text" size="15" maxlength="14"
							value="${map.TXNAMOUNT}">(以分为单位)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>车牌号：
						<input name="PLATENO" type="text" size="15" maxlength="14"
							value="${map.PLATENO}">
					</td>
				</tr>
				<tr>
					<td>
						支付方式：
						<select name="PAYTYPE" id="payType" onchange="onPayTypeChange()" >
							<option value="3" selected="selected">授权银卡(企业账户)</option>
							<!-- 
							<option value="1">POS（有磁有密）</option>
							<option value="2">代收付(无磁无密)</option>
							
							<option value="0">企业账户</option>
							 -->
						
						</select>
					</td>
				</tr>
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
<%--				<tr class="payTypeDaiShouFu">
					<td>
						银行账户户名：
						<input name="ACCNAME" type="text" size="15" maxlength="20"
							value="${map.ACCNAME}">(当支付方式为代收付时为必填)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>银行账号：
						<input name="BANKACCT" type="text" size="15" maxlength="64"
							value="${map.BANKACCT}">(当支付方式为代收付或POS时为必填)
					</td>
				</tr>
				<tr class="payTypeDaiShouFu">
					<td>
						<span style="color:red" type="BT001">*</span>对公/对私标识：
							<select name="PRIVATEFLAG">
							<option value="0" selected="selected">对公</option>
							<option value="1">对私</option>
						</select>（支付方式为代付收时必填写）
					</td>
				</tr>
				<tr class="payTypeDaiShouFu">
					<td>
						<span style="color:red" type="BT001">*</span>卡折标识：
							<select name="CARDFLAG" id="cardFlag" onchange="onCardFlagChange()">
							<option value="1" selected="selected">借记卡</option>
							<option value="2">信用卡（贷记卡）</option>
							<option value="4">存折</option>
							<option value="8">公司账户</option>
						</select>（支付方式为代付收时必填写）
					</td>
				</tr>
				<tr class="cardFlagXinYongKa">
					<td>
						信用卡有效期：
						<input name="CREDITVALIDTIME" type="text" size="15" maxlength="6"
							value="${map.CREDITVALIDTIME}">（MMYY卡折为信用卡时必填）
					</td>
				</tr>
				<tr class="cardFlagXinYongKa">
					<td>
						信用卡校验码：
						<input name="CREDITVALIDCODE" type="text" size="15" maxlength="6"
							value="${map.CREDITVALIDCODE}">（CVN2卡折为信用卡时必填）
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						<span style="color:red" type="BT001">*</span>支付卡密码：
						<input name="PAYPASSWORD" type="text" size="15" maxlength="16"
							value="${map.PAYPASSWORD}">
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						<span style="color:red" type="BT001">*</span>PSAM卡号：
						<input name="PSAMCARDNO" type="text" size="15" maxlength="16"
							value="${map.PSAMCARDNO}">
					</td>
				</tr>
				<tr class="payTypePos">
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
				</tr>--%>
				<tr>
					<td>
						国际网络号：
						<input name="NETWORKNO" type="text" size="15" maxlength="3"
							value="${map.NETWORKNO}">（IC卡：008 磁卡：003 不填写默认003磁卡）
					</td>
				</tr>
				<tr>
					<td>
						联系电话：
						<input name="CONTACTPHONE" type="text" size="20" maxlength="20"
							value="${map.CONTACTPHONE}">
					</td>
				</tr>
				<tr>
					<td>
						联系地址：
						<input name="CONTACTADDR" type="text" size="20" maxlength="64"
							value="${map.CONTACTADDR}">
					</td>
				</tr>
				<tr class="payTypeQiYe">
					<td>
						企业账户操作员：
						<input name="OPERUSER" type="text" size="20" maxlength="20"
							value="${map.OPERUSER}">（支付方式为企业账户时填写）
					</td>
				</tr>
				<tr class="payTypeQiYe">
					<td>
						企业账户支付密码：
						<input name="OPERPASSWORD" type="text" size="20" maxlength="64"
							value="${map.OPERPASSWORD}">（支付方式为企业账户时填写）
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
