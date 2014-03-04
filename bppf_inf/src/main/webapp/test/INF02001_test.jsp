<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02001.txt");
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
			document.getElementById("objectCodeValId").value = objectCode;
			document.getElementById("custCodeValId").value = "";
			document.getElementById("objectCodeDivId").style.display = "block";
			document.getElementById("custCodeDivId").style.display = "none";
		}
		else{
			document.getElementById("custCodeValId").value = custCode;
			document.getElementById("objectCodeValId").value = "";
			document.getElementById("custCodeDivId").style.display = "block";
			document.getElementById("objectCodeDivId").style.display = "none";
			
		}
		
	}

	function setVal(){
		var custCode = "${map.CUSTCODE}";
		var objectCode = "${map.OBJECTCODE}";
		document.getElementById("CUSTCODEHIDDENID").value = custCode;
		document.getElementById("OBJECTCODEHIDDENID").value = objectCode;
	}

</script>

<html>
	<head>
	</head>
	
	<body onload="setVal();"> 

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02001">
			<input type="hidden" name="method" value="inf02001">
			<input name="CUSTCODEHIDDEN" id="CUSTCODEHIDDENID" type="hidden" size="20" maxlength="50" value="${map.CUSTCODEHIDDEN}">
			<input name="OBJECTCODEHIDDEN" id="OBJECTCODEHIDDENID" type="hidden" size="20" maxlength="50" value="${map.OBJECTCODEHIDDEN}">
			<table>
				<tr>
					<td><font color="red" size="5">交易综合查询接口</font></td>
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
						<td>查询模式：
						<select name="QUERYMODE">
							<option value="0" selected="selected">一般模式</option>
							<option value="1">特殊模式</option>
						</select>不选默认为：一般模式
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
						<span style="color:red">*</span>
						<select onchange="changeVal()" id="selValId">
							<option value="CUSTCODE" selected="selected">客户编码</option>
							<option value="OBJECTCODE">业务号码</option>
						</select>
						<DIV id="custCodeDivId">
							<input name="CUSTCODE" type="text" size="54" maxlength="54" id="custCodeValId"
							value="${map.CUSTCODE}">
						</DIV>
						<div id="objectCodeDivId" style="display: none">
							<input name="OBJECTCODE" type="text" size="15" maxlength="50" id="objectCodeValId"
							value="${map.OBJECTCODE}">
						</div>
					</td>
				</tr>
				<tr>
					<td><span style="color:red">说明：订单号、KEEPNO值、交易流水号至少填一个。</span><br/>
						订单号：
						<input name="ORDERNO" type="text" size="25" maxlength="25"
							value="${map.ORDERNO}">
					</td>
				</tr>
				<tr>
					<td>
						KEEPNO值：
						<input name="KEEPNO" type="text" size="64" maxlength="64"
							value="${map.KEEPNO}">
					</td>
				</tr>
				<tr>
					<td>
						交易流水号：
						<input name="TRANSSEQ" type="text" size="25" maxlength="25"
							value="${map.TRANSSEQ}">
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
