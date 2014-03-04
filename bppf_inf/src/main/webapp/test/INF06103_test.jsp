<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06103.txt");
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
			var elPos = $(".payTypePos");
			var elDf = $(".payTypeDaiShouFu");
			var elXyk = $(".cardFlagXinYongKa");
			elPos.hide();
			elDf.hide();
			elXyk.hide();
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06103">
			<input type="hidden" name="method" value="inf06103">
			<table>
				<tr>
					<td><font color="red" size="5">个人账户充值接口 </font></td>
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
				<!-- 
				<tr>
					<td>
						外部终端号：
						<input name="OUTTMNNUMNO" type="text" size="15" maxlength="8"
							value="${map.OUTTMNNUMNO}">
					</td>
				</tr>
				 -->
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>个人账户号码：
						<input name="ACCTCODE" type="text" size="15" maxlength="12"
							value="${map.ACCTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red"></span>系统参考号:
							<input name="SYSTEMNO" type="text" size="12" maxlength="15" 
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
						<span style="color:red" type="BT001">*</span>受理时间：
						<input name="ACCEPTDATE" type="text" size="15" maxlength="14"  id="ACCEPTDATE"
							value="${acctDate}"> (格式：YYYYMMDDhhmmss)
					</td>
				</tr>
				<tr class="payTypeDaiShouFu">
					<td>
						银行账户户名：
						<input name="ACCNAME" type="text" size="15" maxlength="20"
							value="${map.ACCNAME}">（当支付方式为代收付时为必填）
					</td>
				</tr>
					<tr class="payTypedsfpos">
					<td>
						银行账号：
						<input name="BANKACCT" type="text" size="15" maxlength="64"
							value="${map.BANKACCT}">（支付方式为POS时必填写）
					</td>
				</tr>
					<tr class="payTypePos">
					<td>
						支付卡密码：
						<input name="PAYPASSWORD" type="text" size="15" maxlength="128"
							value="${map.PAYPASSWORD}">（支付方式为POS时必填写）
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
					<tr>
					<td>
						支付方式：
							<select name="PAYTYPE" id="payType" onchange="onPayTypeChange()">
							<option value="0" selected="selected">企业账户</option>
							<option value="1">POS</option>
							<%--<option value="2">代收付</option>
						--%></select>
					</td>
				</tr>
					<tr class="cardFlagXinYongKa">
					<td>
						信用卡有效期：
						<input name="CREDITVALIDTIME" type="text" size="15" maxlength="6"
							value="${map.CREDITVALIDTIME}">（MMYY，卡折为信用卡时必填）
					</td>
				</tr>
					<tr class="cardFlagXinYongKa">
					<td>
						信用卡校验码：
						<input name="CREDITVALIDCODE" type="text" size="15" maxlength="6"
							value="${map.CREDITVALIDCODE}">（CVN2，卡折为信用卡时必填）
					</td>
				</tr>
			
				<tr class="payTypePos">
					<td>
						<span style="color:red" type="BT001">*</span>PSAM卡号：
						<input name="PSAMCARDNO" type="text" size="15" maxlength="16"
							value="${map.PSAMCARDNO}">（支付方式为POS时必填写）
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						<span style="color:red" type="BT001">*</span>二磁道：
						<input name="TRACKTWO" type="text" size="15" maxlength="79"
							value="${map.TRACKTWO}">（当支付方式为POS时为必填）
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						三磁道：
						<input name="TRACKTHREE" type="text" size="15" maxlength="108"
							value="${map.TRACKTHREE}">
					</td>
				</tr>
				<tr>
					<td>
						国际网络号：
						<input name="NETWORKNO" type="text" size="15" maxlength="3"
							value="${map.NETWORKNO}">（IC卡：008 磁卡：003 不填写默认003磁卡）
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>联系电话：
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
