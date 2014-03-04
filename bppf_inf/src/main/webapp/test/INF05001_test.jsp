<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF05001.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<style type="text/css">
.BT001{
	display: block;
}
.BT002{
	display: none;
}
</style>
<script type="text/javascript" >
	
	function changeVal(){
		var selVal = document.getElementById("BUSITYPE").value;
		var nodes = document.getElementsByTagName("span");
		if(selVal == 'BT001'){
			for ( var int = 0; int < nodes.length; int++) {
				if(nodes[int].type == "BT002"){
					nodes[int].innerHTML = "";
				}else{
					nodes[int].innerHTML = "*";
				}
			}
		}
		else if(selVal == 'BT002'){
			for ( var int = 0; int < nodes.length; int++) {
				if(nodes[int].type == "BT001"){
					nodes[int].innerHTML = "";
				}else{
					nodes[int].innerHTML = "*";
				}
			}
		}else{
			for ( var int = 0; int < nodes.length; int++) {
				nodes[int].innerHTML = "*";
			}
		}
		
	}
	
	function setVal(){
		var custCode = "${map.CUSTCODE}";
		var objectCode = "${map.OBJECTCODE}";
		document.getElementById("CUSTCODEHIDDENID").value = custCode;
		document.getElementById("OBJECTCODEHIDDENID").value = objectCode;
		document.getElementById("BUSITYPE").selectIndex = 2;
		changeVal();
	}

</script>

<html>
	<head>
	</head>
	
	<body onload="setVal();">

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF05001">
			<input type="hidden" name="method" value="inf05001">
			<table>
				<tr>
					<td><font color="red" size="5">签约实时代收付接口</font></td>
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
				
				--%><tr><td height="50">请求参数：</td></tr>
				<tr>
					<td>
						<span style="color:red">*</span>订单号：
						<input name="ORDERSEQ" type="text" size="15" maxlength="32"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>代收付类型：
						<select onclick="changeVal()" id="BUSITYPE" name="BUSITYPE">
							<option value="BT001">代收</option>
							<option value="BT002">代付</option>
						</select>
					</td>
				</tr>
				<!-- 
				<tr>
					<td>
						<span style="color:red">*</span>转账标识：
						<select name="TRANSFERFLAG">
							<option value="00" selected="selected">否</option>
							<option value="01">是</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						收款方客户编码：
						<input name="COLLECUSTCODE" type="text" size="20" maxlength="54"
							value="${map.COLLECUSTCODE}">
					</td>
				</tr>
				 -->
				<tr>
					<td>
						外部业务编码：
						<input name="BRANCHCODE" type="text" size="20" maxlength="20"
							value="${map.BRANCHCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>签约ID：
						<input name="TRANSCONTRACTID" type="text" size="20" maxlength="20"
							value="${map.TRANSCONTRACTID}">
					</td>
				</tr>			
				<tr>
					<td>
						外部ID：
						<input name="EXTERNALID" type="text" size="14" maxlength="14"
							value="${map.EXTERNALID}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>银行账号：
						<input name="BANKACCT" type="text" size="30" maxlength="30"
							value="${map.BANKACCT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>交易金额(单位:分)：
						<input name="TXNAMOUNT" type="text" size="15" maxlength="32"
							value="${map.TXNAMOUNT}">
					</td>
				</tr>
				<tr>
					<td>
						备注：
						<input name="MEMO" type="text" size="15" maxlength="200"
							value="${map.MEMO}">
					</td>
				</tr>
				<tr>
					<td>
						预留域1：
						<input name="REMARK" type="text" size="15" maxlength="200"
							value="${map.REMARK}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="15" maxlength="200"
							value="${map.REMARK2}">
					</td>
				</tr><!--
				<tr>
					<td>
						预留域3：
						<input name="REMARK3" type="text" size="15" maxlength="200"
							value="${map.REMARK4}">
					</td>
				</tr>
				<tr>
					<td>
						预留域4：
						<input name="REMARK4" type="text" size="15" maxlength="200"
							value="${map.REMARK4}">
					</td>
				</tr>
				--><tr>
					<td>
						<input name="submit" value="提交" type="submit">
					</td>
				</tr>

			</table>
		</form>

	</body>
</html>
