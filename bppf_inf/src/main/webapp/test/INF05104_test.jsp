<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF05104.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("TRADETIME",sdf.format(new Date()));
	pageContext.setAttribute("ORDERNO",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF05104">
			<input type="hidden" name="method" value="inf05104">
			<table>
				<tr>
					<td><font color="red" size="5">分账方案管理接口</font></td>
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
						<span style="color:red">*</span>订单号:
							<input name="ORDERNO" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${ORDERNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码:
							<input name="CUSTCODE" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						用户名:
							<input name="STAFFCODE" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${map.STAFFCODE}">渠道为20时必填
					</td>
				</tr>
				<tr>
					<td>
						登录密码:
							<input name="PAYPASSWORD" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${map.PAYPASSWORD}">渠道为20时必填
					</td>
				</tr>
				<tr>
					<td>
						操作类型：
						<select name="OPERTYPE">
										<option value="0" selected="selected">新增方案</option>
										<!-- 
										<option value="1">修改方案</option>
										 -->
						</select>
					</td>
				</tr>
				<tr>
					<td>
						方案编码：
						<input name="PLANCODE" type="text" size="15" maxlength="15"
							value="${map.PLANCODE}">操作类型为修改时方案编码必填
							
					</td>
				</tr>
				<tr>
					<td>
						方案名称：
						<input name="PLANNAME" type="text" size="15" maxlength="64"
							value="${map.PLANNAME}">
							
					</td>
				</tr>
				<tr>
					<td>
						方案描述：
						<input name="PLANDESC" type="text" size="15" maxlength="300"
							value="${map.PLANDESC}">
							
					</td>
				</tr>
				<tr>
					<td>
						方案类型：
						<select name="PLANTYPE">
										<option value="P001" selected="selected">定额分账</option>
										<option value="P002">比例分账</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						收款账号：
						<input name="PLANCUSTCODE" type="text" size="15" maxlength="300"
							value="${map.PLANCUSTCODE}">接收分账的企业账户，多个用、分隔，最多不超过10收款人
							
					</td>
				</tr>
				<tr>
					<td>
						分账数值：
						<input name="PLANVALUE" type="text" size="15" maxlength="300"
							value="${map.PLANVALUE}">多个用、分隔与收款账号对应定额分账以元为单位，比例分账保留两位小数如0.25表示25%
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>受理时间：
						<input name="TRADETIME" type="text" size="14" maxlength="14"
							value="${TRADETIME}">
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
