<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06003.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	String dateStr = sdf.format(new Date());
	pageContext.setAttribute("acctDate",dateStr);
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
<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" >

		function onSelectAdditem1Change(){
			//alert("test")
			var v = $("#selectAdditem").find("option:selected").val();
			var acceptAreaCode = $("#acceptAreaCode").val();
			if("" == acceptAreaCode || acceptAreaCode.length != 6){
				alert("请输入6位受理区域编码");
				return false;
			}
			var prex = acceptAreaCode.substring(0,2);
			var addItem1 = prex+"0000"+acceptAreaCode + v;
			//alert(addItem1);
			$("#additem1").val(addItem1);
			//alert($("#additem1").val());
			return true;
		}

	
</script>

<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post" id="paymentInfo" 
			action="${sessionScope.path}/httppost" >
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06003">
			<input type="hidden" name="method" value="inf06003">
			<table>
				<tr>
					<td><font color="red" size="5">水电煤账单查询 </font></td>
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
							value="${map.ORDERSEQ}">
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
						<span style="color:red" type="BT001">*</span>客户终端号：
						<input name="TMNNUMNO" type="text" size="12" maxlength="12"
							value="${map.TMNNUMNO}">(注：当渠道为80时，可为终端号，可为外部终端号。当渠道为60时，只能为外部终端号)
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
						</span>查询类型：
						<select name="SELECTTYPE">
							<option value="001" selected="selected" >用户号</option>
							<option value="002">条形码</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>用户号或条形码：
						<input name="SELECTVALUE" type="text" size="64" maxlength="64"
							value="${map.SELECTVALUE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>手机号：
						<input name="PHONENUMBER" type="text" size="11" maxlength="20"
							value="${map.PHONENUMBER}">
					</td>
				</tr>
				<tr>
					<td>
						</span>账期：
						<input name="BILLMONTH" type="text" size="8" maxlength="8"
							value="${map.BILLMONTH}">
					</td>
				</tr>
				<tr>
					<td>
						</span>受理时间：
						<input name="ACCEPTDATE" type="text" size="14" maxlength="14"
							value="${acctDate}">
					</td>
				</tr>

				<tr>
					<td>
						</span>受理区域编码：
						<input name="ACCEPTAREACODE" type="text" size="6" maxlength="6" id="acceptAreaCode"
							value="${map.ACCEPTAREACODE}" >
					</td>
				</tr>
				<tr>
					<td>
						</span>收费单位编码：
						<%--<select name="SELECTADDITEM1"  id="selectAdditem" onchange="onSelectAdditem1Change()">
							<option value="6100006101002001"  >电费</option>
							<option value="5000005000001001" selected="selected">水费</option>
							<option value="6100006101003001">煤气费</option>
							<option value="6100006101004001">燃气费</option>
							<option value="6100006101005001" >手机</option>
							<option value="6100006101006001" >供暖</option>
							<option value="6100006101008001" >高铁售票</option>
							<option value="6100006101009001" >电信充值卡</option>
							<option value="2001">电费</option>
							<option value="1001" selected="selected">水费</option>
							<option value="3001">煤气费</option>
							<option value="4001">燃气费</option>
							<option value="5001" >手机</option>
							<option value="6001" >供暖</option>
							<option value="8001" >高铁售票</option>
							<option value="9001" >电信充值卡</option>
							</select>
							<input type="hidden" name="ADDITEM1" id="additem1">--%>
							<input name="ADDITEM1" type="text" size="32" maxlength="32"
							value="${map.ADDITEM1}">(注：交费单位代码一般为16位,如:水费 5000005001001001)
					</td>
				</tr>
				<tr>
					<td>
						查询金额：
						<input name="ADDITEM2" type="text" size="200" maxlength="200"
							value="${map.ADDITEM2}">
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
						预留域3：
						<input name="REMARK3" type="text" size="200" maxlength="200"
							value="${map.REMARK3}">
					</td>
				</tr>
				<tr>
					<td>
						预留域4：
						<input name="REMARK4" type="text" size="200" maxlength="200"
							value="${map.REMARK4}">
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
