<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02016.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<script type="text/javascript">
	
	function changeVal(){
		var selVal = document.getElementById("selValId").value;
		var custCode = "${map.CUSTCODE}";
		var objectCode = "${map.OBJECTCODE}";
		
		document.getElementById("CUSTCODEHIDDENID").value = custCode;
		document.getElementById("OBJECTCODEHIDDENID").value = objectCode;
		
		if(selVal == 'OBJECTCODE'){
//			document.getElementById("objectCodeValId").value = objectCode;
//			document.getElementById("custCodeValId").value = "";
//			document.getElementById("objectCodeDivId").style.display = "block";
//			document.getElementById("custCodeDivId").style.display = "none";
			document.getElementById("val").name = "OBJECTCODE";
			document.getElementById("val2").name = "CUSTCODE";
		}
		else{
//			document.getElementById("custCodeValId").value = custCode;
//			document.getElementById("objectCodeValId").value = "";
//			document.getElementById("custCodeDivId").style.display = "block";
//			document.getElementById("objectCodeDivId").style.display = "none";
			document.getElementById("val").name = "CUSTCODE";
			document.getElementById("val2").name = "OBJECTCODE";
		}
		
	}

	function setVal(){
		var custCode = "${map.CUSTCODE}";
		var objectCode = "${map.OBJECTCODE}";
		if(custCode != ''){
			document.getElementById("val").value = custCode;
			document.getElementById("val").name = "CUSTCODE";
			document.getElementById("val2").name = "OBJECTCODE";
			document.getElementById("selValId").selectedIndex  = "0";
		}else{
			document.getElementById("val").value = objectCode;
			document.getElementById("val").name = "OBJECTCODE";
			document.getElementById("val2").name = "CUSTCODE";
			document.getElementById("selValId").selectedIndex  = "1";
		}
		document.getElementById("CUSTCODEHIDDENID").value = custCode;
		document.getElementById("OBJECTCODEHIDDENID").value = objectCode;
	}

function preSubmit(){

	var o = document.getElementById("ACTIONCODE");
	
	var r="";
	
	for(i=0;i<o.length;i++){ 
		if(o.options[i].selected){
		
			r = r+o.options[i].value+"|";
		}
	}
	
	document.getElementById("ACTIONCODE").options[0].value=r;
	alert(document.getElementById("ACTIONCODE").options[0].value);


}
</script>

<html>
	<head>
	</head>
	
	<body onload="">

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="" >
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02016">
			<input type="hidden" name="method" value="inf02016">
			<table>
				<tr>
					<td><font color="red" size="5">账户交易记录查询接口</font></td>
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
						客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">
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
					<td> <span style="color:red">*</span>账户类型：
						<select name="ACCTTYPE">
							<option value="0001" selected="selected">企业账户</option>
							<option value="0007">IPOS账户</option>
							<option value="0110">酬金账户</option>
						</select>
					</td>
				<tr>
					<td>
						资金管理模式:
						<select name="BANKMODE">
							<option value="BT1001"  selected="selected">普通卡</option>
							<option value="BT1002">子母卡</option>
							<option value="BT1013">资金池母卡</option>
							<option value="BT1014">资金池子卡</option>
						</select>(不送默认为 BT1001普通卡)
					</td>
				</tr>
				</tr>



				<tr>
					<td> 收支标记 ：
						<select name="INCOME">
							<option value="" selected="selected">不选</option>
							<option value="+" >收</option>
							<option value="-" >支</option>
						</select>
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
						<span style="color:red"></span>查询起始序号：
						<input name="STARTNUM" type="text" size="15" maxlength="14"
							value="${map.STARTNUM}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red"></span>查询结束序号：
						<input name="ENDNUM" type="text" size="15" maxlength="14"
							value="${map.ENDNUM}">
					</td>
				</tr>
				<tr>
					<td>
						交易代码：
						<select name="TRANSCODE">
							<option value="" selected="selected">不选</option>
							<option value="0100">充值</option>
							<option value="0200">消费</option>
							<option value="0000">冲正</option>
							<option value="0203">托收扣费</option>
							<option value="0101">人工入账</option>
							<option value="0102">酬金结算</option>
							<option value="0201">人工提现</option>
							<option value="0202">透支</option>
							<option value="0300">跨账户转账出</option>
							<option value="0310">跨账户转账入</option>
							<option value="0301">跨卡转账出</option>
							<option value="0311">跨卡转账入</option>
							<option value="0400">预授权</option>
							<option value="0401">预授权确认</option>
							<option value="0402">解除预授权</option>
							<option value="0410">预授权确认入账</option>
							<option value="0501">担保申请</option>
							<option value="0511">中间账户入账</option>
							<option value="0502">担保确认</option>
							<option value="0512">中间账户出账</option>
							<option value="0503">担保退款</option>
							<option value="0513">中间账户出账</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<input type="submit" onclick="" value="提交" />
					</td>
				</tr>

			</table>
		</form>

	</body>
</html>
