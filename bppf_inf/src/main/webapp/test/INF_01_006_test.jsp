<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map = FileUtil.getPageparams("WEB_01_006.txt");
	pageContext.setAttribute("map", map);
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

function preSubmitPr(){
	var o = document.getElementById("PRODUCTCODE").value;
	if(o==""){
	   return true;
	}
    var arry=o.split("_")
    var b=true;
    for(var k=0;k<arry.length;k++){  
        var t= arry[k];
        if(isNaN(t))
        {
          alert(o+"格式有误,格式如:0020_01420300");
          b=false;
        }else {
           if(t.length==4 || t.length==8){
             
           }else{
               b=false;
               alert(o+"格式有误,格式如:0020_01420300");
           }
        }  
    }  
    
    if(b){
       return true;
    }
    
    return false;
}

</script>

<html>
	<head>
	</head>

	<body onload="setVal();">

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="changeVal();">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16"
				value="01_006">
			<input type="hidden" name="method" value="inf01006">
			<input name="CUSTCODEHIDDEN" id="CUSTCODEHIDDENID" type="hidden"
				size="20" maxlength="50" value="${map.CUSTCODEHIDDEN}">
			<input name="OBJECTCODEHIDDEN" id="OBJECTCODEHIDDENID" type="hidden"
				size="20" maxlength="50" value="${map.OBJECTCODEHIDDEN}">
			<table>
				<tr>
					<td>
						<font color="red" size="5">交易列表查询接口</font>
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

				<%--<tr>
					<td>
						SIGN：
						<input name="SIGN" type="text" size="20" maxlength="4096"
							value="SIGN">
					</td>
				</tr>
				
				<tr>
					<td>
						CER：
						<input name="CER" type="text" size="20" maxlength="4096"
							value="CER">
					</td>
				</tr>
				
				--%>
				<tr>
					<td height="50">
						请求参数：
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span>
						<select onchange="changeVal()" id="selValId">
							<option value="CUSTCODE" selected="selected">
								客户编码
							</option>
							<option value="OBJECTCODE">
								业务号码
							</option>
						</select>
						<%--						<span style="color:red">*</span>客户编码：--%>
						<input id="val" name="" type="text" size="15" maxlength="300"
							value="">
						<input id="val2" name="" type="text" size="15" maxlength="300"
							value="" style="display: none;">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red">*</span>用户名:
						<input name="STAFFCODE" type="text" size="32" maxlength="32"
							id="staffCode" value="${map.STAFFCODE}">
					</td>
				</tr>
					<tr>
						<td>数据源类型：
						<select name="DATATYPE">
							<option value="0" selected="selected">在途</option>
							<option value="1">历史</option>  
						</select>不选默认为：在途
					</td>
				</tr>
				<tr>
					<td>
						交易卡号：
						<input name="TRANSCARD" type="text" size="15" maxlength="32"
							value="${map.TRANSCARD}">
					</td>
				</tr>
				<!--<tr>
					<td> 账户类型：
						<select name="ACCTTYPE">
							<option value="0001" selected="selected">企业账户</option>
							<option value="0007">IPOS账户</option>
						</select>
					</td>
				</tr>
-->
				<%--				<tr>--%>
				<%--					<td>--%>
				<%--						<span style="color:red">*</span>查询时间：--%>
				<%--						<input name="SEARCHTIME" type="text" size="15" maxlength="14"--%>
				<%--							value="${map.SEARCHTIME}">--%>
				<%--					</td>--%>
				<%--				</tr>--%>
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
						<span style="color: red"></span>是否统计总数：
						<select name="COUNTTOTAL"">
							<option value="1">
								是
							</option>
							<option value="2" selected="selected">
								否
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red"></span>查询起始序号：
						<input name="STARTNUM" type="text" size="15" maxlength="14"
							value="${map.STARTNUM}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red"></span>查询结束序号：
						<input name="ENDNUM" type="text" size="15" maxlength="14"
							value="${map.ENDNUM}">
					</td>
				</tr>
				<tr>
					<td>
						外部订单号：
						<input name="ORDERSEQ" type="text" size="15" maxlength="25"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				<%--				<tr>--%>
				<%--					<td>--%>
				<%--						交易流水号：--%>
				<%--						<input name="TRANSSEQ" type="text" size="15" maxlength="15"--%>
				<%--							value="${map.TRANSSEQ}">--%>
				<%--					</td>--%>
				<%--				</tr>--%>
				<tr>
					<td>
						业务编码：
						<select name="ACTIONCODE" size="2" multiple="MULTIPLE">
							<option value="" selected="selected">
								所有
							</option>
							<option value="01010001">
								授权银行卡充值
							</option>
							<option value="01010002">
								网银充值
							</option>
							<option value="01020001">
								人工提现
							</option>
							<option value="01020003">
								授权银行卡提现
							</option>
							<option value="01030001">
								普通转账
							</option>
							<option value="01030002">
								定向转帐
							</option>
							<option value="01030003">
								订单支付-即时交易
							</option>
							<option value="01030004">
								批量付款
							</option>
							<option value="01030005">
								订单支付-担保交易
							</option>
							<option value="01030006">
								代扣
							</option>
							<option value="01040001">
								普通收款
							</option>
							<option value="01040002">
								批量收款
							</option>
							<option value="01040003">
								托收
							</option>
							<option value="01020002">
								自动提现
							</option>
							<option value="01030007">
								预授权
							</option>
							<option value="01030008">
								酬金结转
							</option>
							<option value="01010005">
								实时转到到交费易
							</option>
							<option value="01020005">
								新提现
							</option>
							<option value="01050002">
								税金费率
							</option>
							<option value="01010004">
								手工调账
							</option>
							<option value="01030009">
								资金归集
							</option>
							<option value="02010001">
								手机充值话费缴费
							</option>
							<option value="01050001">
								手续费
							</option>
							<option value="01010003">
								手工充值
							</option>
							<option value="01020004">
								手工提现
							</option>
							<option value="01010006">
								网银付款
							</option>
							<option value="15010001">
								签约实时代收付
							</option>
							<option value="15010009">
								验证实时代收付
							</option>
							<option value="15010010">
								实时代收付
							</option>
							<option value="15010008">
								签约实时代收入账
							</option>
							<option value="15010006">
								验证实时代收入账
							</option>
							<option value="15010007">
								实时代收入账
							</option>
							<option value="03010008">
								全国电信直充
							</option>
							<option value="05010005">
								全国移动直充-向上
							</option>
							<option value="04010003">
								全国联通直充-向上
							</option>
							<option value="16010001">
								游戏直充
							</option>
							<option value="03010100">
								全国电信固话缴费
							</option>
							<option value="03010200">
								全国电信宽带缴费
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						订单状态编码：
						<select name="ORDERSTAT">
							<option value="" selected="selected">
								所有
							</option>
							<option value="S0A">
								生成
							</option>
							<option value="S0B">
								申请成功
							</option>
							<option value="S0C">
								成功
							</option>
							<option value="S0D">
								处理中
							</option>
							<option value="S0E">
								超时
							</option>
							<option value="S0F">
								失败
							</option>
							<option value="S0M">
								已调账
							</option>
							<option value="S0P">
								已锁定
							</option>
							<option value="S0R">
								已退款
							</option>
							<option value="S0U">
								已撤销
							</option>
							<option value="S0V">
								对账异常
							</option>
							<option value="S0W">
								已冲正撤销
							</option>
							<option value="S0X">
								取消
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						交易区域：
						<input name="AREACODE" type="text" size="15" maxlength="10"
							value="${map.AREACODE}">
					</td>
				</tr>
				<tr>
					<td>
						产品编码：
						<input name="PRODUCTCODE" type="text" size="15" maxlength="300"
							id="PRODUCTCODE" value="${map.PRODUCTCODE}">
						<font color="red">格式:"0020_01420300"
							&nbsp;&nbsp;&nbsp;&nbsp;
							产品: 0039 Q币充值 0007游戏直充  0003全国电信直充 0005全国移动直充 0004全国联通直充 ……</font>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color: red"></span>是查询子卡订单：
						<select name="INCLUDESONCARD"">
							<option value="1">
								是
							</option>
							<option value="0" selected="selected">
								否
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						业务对象：
						<input name="BUSOBJECT" type="text" size="15" maxlength="64"
							value="${map.BUSOBJECT}">
						如：消费类的QQ号码，手机号码等

					</td>
				</tr>
				<tr>
					<td>
						<input type="submit" onclick="return preSubmitPr();" value="提交" />
					</td>
				</tr>

			</table>
		</form>

	</body>
</html>
