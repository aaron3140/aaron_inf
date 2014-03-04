<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06101.txt");
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06101">
			<input type="hidden" name="method" value="inf06101">
			<table>
				<tr>
					<td><font color="red" size="5">信用卡还款接口 </font></td>
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
						<span style="color:red" type="BT001">*</span>终端流水号：
						<input name="ORDERSEQ" type="text" size="15" maxlength="32"
							value="${orderSeq}">
					</td>
				</tr>
				<tr>
					<td>
						客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">(注：当渠道为60时，可为空)
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
						<span style="color:red" type="BT001">*</span>还款金额：
						<input name="TXNAMOUNT" type="text" size="15" maxlength="14"
							value="${map.TXNAMOUNT}">(以分为单位)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>信用卡账户：
						<input name="TARGETACCOUNT" type="text" size="15" maxlength="19"
							value="${map.TARGETACCOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>支付密码：
						<input name="PAYPASSWORD" type="text" size="15" maxlength="64"
							value="${map.PAYPASSWORD}">（1.（POS支付方式时）为付款账户的密码（经加密） 2.(代付支付方式时)为操作员的支付密码（经加密） 3.如没有支付密码送 123456）
					</td>
				</tr>
					<tr class="payTypeDaiShouFu">
					<td>
						银行账号：
						<input name="BANKACCT" type="text" size="15" maxlength="64"
							value="${map.BANKACCT}">(当支付方式为代收付时为选填)
					</td>
				</tr>
				<tr>
					<td>
							用户名：
							<input name="STAFFCODE" type="text" size="15" maxlength="50"
							value="${map.STAFFCODE}">（支付用户名 渠道20时为必送；其它渠道可不送）
					</td>
				</tr>
				<tr>
					<td>
						支付方式：
							<select name="PAYTYPE" id="payType" onchange="onPayTypeChange()">
							<option value="1" selected="selected">POS(有磁有密)</option>
							<option value="2">代付（无磁无密）</option>
							</select>（不填默认为POS方式  1）
					</td>
				</tr>
				
					<tr class="payTypeDaiFu">
					<td>
						银行账户户名：
						<input name="ACCNAME" type="text" size="128" maxlength="128"
							value="${map.ACCNAME}">(当支付方式为代付时为必填)
					</td>
				</tr>
					<tr>
					<td>
						开户行名称：
						<input name="BANKINFO" type="text" size="128" maxlength="128"
							value="${map.BANKINFO}">(当支付方式为代付时为选填,如“建设银行广州市天河区珠江新城分行”)
					</td>
				</tr>
					<tr>
					<td>
						区域编码：
						<input name="BANKAREA" type="text" size="15" maxlength="20"
							value="${map.BANKAREA}">(当支付方式为代付时为选填，指开户行的地区)
					</td>
				</tr>
					<tr>
					<td>
						银行编码：
						<input name="BANKCODE" type="text" size="15" maxlength="6"
							value="${map.BANKCODE}">(选填)
					</td>
				</tr>
				
				<tr class="payTypeDaiFu">
					<td>
						对公/对私标识：
							<select name="PRIVATEFLAG">
							<option value="0" selected="selected">对公</option>
							<option value="1">对私</option>
						</select>（支付方式为代收，代付时必填）
					</td>
				</tr>
					<tr class="payTypeDaiFu">
					<td>
						<span style="color:red" type="BT001">*</span>卡折标识：
							<select name="CARDFLAG" id="cardFlag" onchange="onCardFlagChange()">
							<option value="1" selected="selected">借记卡</option>
							<option value="2">信用卡（贷记卡）</option>
							<option value="4">存折</option>
							<option value="8">公司账户</option>
						</select>（支付方式为代收，代付时选填）
					</td>
				</tr>
				<tr class="cardFlagXinYongKa">
					<td>
						信用卡有效期：
						<input name="CREDITVALIDTIME" type="text" size="15" maxlength="6"
							value="${map.CREDITVALIDTIME}">（MMYY卡折为信用卡时选填）
					</td>
				</tr>
					<tr class="cardFlagXinYongKa">
					<td>
						信用卡校验码：
						<input name="CREDITVALIDCODE" type="text" size="15" maxlength="6"
							value="${map.CREDITVALIDCODE}">（CVN2卡折为信用卡时选填）
					</td>
				</tr>
				<tr>
					<td>
						证件类型：
						<select name="CERTTYPE">
							<option value="00" selected="selected">身份证</option>
							<option value="01">护照</option>						
							<option value="02">军人证</option>
							<option value="03">户口本</option>														
							<option value="06">港澳通行证</option>
							<option value="08">学生证</option>
							<option value="09">工作证</option>
							<option value="10">工商执照</option>
							<option value="11">警官证</option>
							<option value="12">事业单位编码</option>
							<option value="13">房产证</option>
							<option value="51">组织机构代码</option>
							<option value="99">其他证件</option>
						</select>（支付方式为代收，代付时选填）
					</td>
				</tr>			
				<tr>
					<td>
						证件号码：
						<input name="CERTNO" type="text" size="32" maxlength="32"
							value="${map.CERTNO}">（银行开户预留证件号码。支付方式为代收，代付时选填）
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
				<tr class="payTypePos">
					<td>
						支付卡账户：
						<input name="PAYACCOUNT" type="text" size="15" maxlength="30"
							value="${map.PAYACCOUNT}">（支付方式为POS时必填）
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						<span style="color:red" type="BT001">*</span>PSAM卡号：
						<input name="PSAMCARDNO" type="text" size="15" maxlength="16"
							value="${map.PSAMCARDNO}">（支付方式为POS时必填）
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						<span style="color:red" type="BT001">*</span>二磁道：
						<input name="TRACKTWO" type="text" size="15" maxlength="79"
							value="${map.TRACKTWO}">（支付方式为POS时必填）
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						三磁道：
						<input name="TRACKTHREE" type="text" size="15" maxlength="108"
							value="${map.TRACKTHREE}">（支付方式为POS时<span style="color:red" type="BT001">选填</span>）
					</td>
				</tr>
				<tr class="payTypePos">
					<td>
						国际网络号：
						<input name="NETWORKNO" type="text" size="15" maxlength="3"
							value="${map.NETWORKNO}">（支付方式为POS时必填 IC卡：008 磁卡：003）
					</td>
				</tr>
				<tr >
					<td>
						<span style="color:red">*</span>外部客户标识：
						<input name="OUTCUSTSIGN" type="text" size="200" maxlength="200"
							value="${map.OUTCUSTSIGN}">
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
