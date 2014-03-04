<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF05003.txt");
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

<html>
	<head>
	</head>
	
	<body >

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF05003">
			<input type="hidden" name="method" value="inf05003">
			<table>
				<tr>
					<td><font color="red" size="5">付款到银行账户接口</font></td>
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
						<span style="color:red">*</span>商户编码
						<input name="TRANSCODE"  size="50" maxlength="50"
							value="${map.TRANSCODE}">
					</td>
				</tr>
				<tr>
					<td>
						收款商户编码
						<input name="RECVCODE"  size="50" maxlength="50"
							value="${map.RECVSCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>收款人银行账户号码
						<input name="REVACCNO"  size="30" maxlength="30"
							value="${map.REVACCNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>收款人银行账户名称
						<input name="REVACCNAME"  size="50" maxlength="128"
							value="${map.REVACCNAME}">
					</td>
				</tr>
				<tr>
					<td>
						收款人联系地址
						<input name="ADDR"  size="50" maxlength="50"
							value="${map.ADDR}">
					</td>
				</tr>
				<tr>
					<td>
						收款人联系电话
						<input name="PHONE"  size="20" maxlength="20"
							value="${map.PHONE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>收款人银行账户对应证件类型
						<select  id="CERTID" name="CERTID">
							<option value="00" selected="selected">身份证</option>
							<option value="01">护照</option>
							<option value="02" >军人证</option>
							<option value="03" >户口薄</option>
							<option value="04" >武警证</option>
							<option value="05" >法人营业执照</option>
							<option value="06" >港澳通行证</option>
							<option value="07" >台湾通行证</option>
							<option value="99" >其他证件</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>收款人银行账户对应证件号码
						<input name="CERTCODE"  size="32" maxlength="32"
							value="${map.CERTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>收款人银行账户对应归属地
						<input name="BANKBELONG"  size="20" maxlength="20"
							value="${map.BANKBELONG}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>收款人银行编码
						<input name="BANKID" type="text" size="20" maxlength="20"
							value="${map.BANKID}">
					</td>
				</tr>
				<tr>
					<td>
						收款人银行支行名称
						<input name="BANKSUBID" type="text" size="30" maxlength="128"
							value="${map.BANKSUBID}">
					</td>
				</tr>
				
				<tr>
					<td>
						<span style="color:red">*</span>收款人银行卡折标识：
						<select id="BANKCARDID" name="BANKCARDID">
							<option value="1" selected="selected">借记卡</option>
							<option value="2">信用卡（贷记卡）</option>
							<option value="4">存折</option>
							<option value="8">公司账户</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>收款人银行账户标识：
						<select id="BANKCARDTYPE" name="BANKCARDTYPE">
							<option value="0" selected="selected">对公</option>
							<option value="1">对私</option>
						</select>
					</td>
				</tr>				
				<tr>
					<td>
						<span style="color:red">*</span>金额(单位:分)：
						<input name="TXNAMOUNT" type="text" size="12" maxlength="12"
							value="${map.TXNAMOUNT}">
					</td>
				</tr>
	     		<tr>
					<td>
						<span style="color:red">*</span>订单号：
						<input name="ORDERSEQ" type="text" size="32" maxlength="32"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>交易时间：
						<input name="TRADETIME" type="text" size="14" maxlength="14"
							value="${map.TRADETIME}">
					</td>
				</tr>
				<tr>
					<td>
						备注：
						<input name="REMARK" type="text" size="15" maxlength="200"
							value="${map.REMARK}">
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
