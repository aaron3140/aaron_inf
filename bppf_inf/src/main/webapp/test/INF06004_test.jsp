<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06004.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdf2 = new SimpleDateFormat("hhmmss");
	Date date = new Date();
	String dateStr = sdf.format(date);
	String timeStr = sdf2.format(date);
	pageContext.setAttribute("acctDate",dateStr);
	pageContext.setAttribute("acctTime",timeStr);
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
</style>

<html>
	<head>
		<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
	
	<script type="text/javascript">
	
		function onlaod(){

			onTypeChange();
		}
		
	function onTypeChange(){
	
		var VERIFY = $("#VERIFY").find("option:selected").val();
	
		var verifyType = $(".verifyType");
	
			if("0"==VERIFY){
		
				verifyType[0].innerText="充值金额：";
		
			}else{
		
				verifyType[0].innerText="充值流量：";
			}
	
	}
	
</script>
	</head>
	
	<body onload="onlaod()">
		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06004">
			<input type="hidden" name="method" value="inf06004">
			<table>
				<tr>
					<td><font color="red" size="5">充值账户校验接口 </font></td>
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
						<span style="color:red" type="BT001">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">(注：当渠道为60时，可为空)
					</td>
				</tr>
				<tr>
					<td>
						登录工号：
						<input name="STAFFCODE" type="text" size="15" maxlength="50"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						验证类型：
						<select name="VERIFY" id="VERIFY" onchange="onTypeChange()" >
							<option value="0" selected="selected">个人账户</option>
							<option value="1">3G流量充值</option>
							<option value="2">全国电信宽带</option>
							<option value="3">全国电信固话</option>				
						</select>
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
						受理区域编码：
						<input name="ACCEPTAREACODE" type="text" size="15" maxlength="12"
							value="${map.ACCEPTAREACODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>验证号码：
						<input name="ACCTCODE" type="text" size="64" maxlength="64"
							value="${map.ACCTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>受理日期：
						<input name="ACCEPTDATE" type="text" size="15" maxlength="14"  id="ACCEPTDATE"
							value="${acctDate}"> (格式：YYYYMMDD)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" type="BT001">*</span>受理时间：
						<input name="ACCEPTTIME" type="text" size="15" maxlength="14"  id="ACCEPTTIME"
							value="${acctTime}"> (格式：hhmmss)
					</td>
				</tr>
				<tr>
					<td>
						<span class="verifyType">充值金额：</span>
						<input name="REAMOUNT" type="text" size="15" maxlength="14"  id="ACCEPTTIME"
							value="${map.REAMOUNT}"> 
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
