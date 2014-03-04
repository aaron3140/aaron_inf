<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF12037.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("ACCEPTDATE",sdf.format(new Date()));
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
	
</script>

<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF12037">
			<input type="hidden" name="method" value="inf12037">
			<table>
				<tr>
					<td><font color="red" size="5">火车票订票查询接口 </font></td>
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
				<!-- 
				<tr>
					<td>
						<span style="color:red">*</span>订单号：
						<input name="ORDERSEQ" type="text" size="15" maxlength="25"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				 -->
				<tr>
					<td>
						<span style="color:red">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="32" maxlength="32"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						系统参考号：
						<input name="SYSTEMNO" type="text" size="15" maxlength="500"
							value="${map.SYSTEMNO}">系统参考号与外部流水号二选一 暂不支持外部流水号
					</td>
				</tr>
				<tr>
					<td>
						外部流水号:
						<input name="KEEPNO" type="text" size="15" maxlength="500"
							value="${map.KEEPNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>受理时间:
						<input name="ACCEPTDATE" type="text" size="15" maxlength="16"
							value="${ACCEPTDATE}">
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