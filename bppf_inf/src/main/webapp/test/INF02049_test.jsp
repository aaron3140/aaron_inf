<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02049.txt");
	pageContext.setAttribute("map",map);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
	</head>
	
	<body>

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httpipos" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02049">
			<input type="hidden" name="method" value="inf02049">
			<table>
				<tr>
					<td><font color="red" size="5">理财明细列表查询接口</font></td>
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
						<span style="color:red">*</span>用户名：
							<input name="STAFFCODE" type="text" size="50" maxlength="50" id="staffCodeValId"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码：
							<input name="CUSTCODE" type="text" size="32" maxlength="32" id="custCodeValId"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>明细类型：
						<select name="DETAILTYPE">
							<option value="01" selected="selected">
								明细
							</option>
							<option value="02">
								收益
							</option>
							<option value="03">
								转入
							</option>
							<option value="04">
								转出
							</option>
							<option value="05">
								消费
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>开始日期：
						<input name="STARTDATE" type="text" size="8" maxlength="8"
							value="${map.STARTDATE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>结束日期：
						<input name="ENDDATE" type="text" size="8" maxlength="8"
							value="${map.ENDDATE}">
					</td>
				</tr>
				<tr>
					<td>
						页码：
							<input name="PAGENO" type="text" size="10" maxlength="10" id="pageNoValId"
							value="${map.PAGENO}">
					</td>
				</tr>
				<tr>
					<td>
						页大小：
							<input name="PAGESIZE" type="text" size="10" maxlength="10" id="pageSizeValId"
							value="${map.PAGESIZE}">
					</td>
				</tr>
				<tr>
					<td>
						升序、降序标记：
						<select name="SORTFLAG">
							<option value="0" selected="selected">
								降序
							</option>
							<option value="1">
								升序
							</option>
						</select>
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
