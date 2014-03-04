<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF01014.txt");
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
	
	function changeVal(arg){
		var span = document.getElementsByTagName("span");
		if(arg == "2"){
			for(var i = 0; i < span.length; i++){
				if(span[i].name == 'cred')
				span[i].innerHTML = '*';
			}
		}else{
			for(var i = 0; i < span.length; i++){
				if(span[i].name == 'cred')
				span[i].innerHTML = '';
			}
		}
	}
	

</script>

<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httppost" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF01014">
			<input type="hidden" name="method" value="inf01014">
			<input type="hidden" name="CONTRACTTYPE" value="0001">
			<table>
				<tr>
					<td><font color="red" size="5">实时验证接口</font></td>
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
						<span style="color:red">*</span>订单号：
						<input name="ORDERSEQ" type="text" size="20" maxlength="20"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="50" maxlength="50"
							value="${map.CUSTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>验证方式：
						<select onclick="changeVal()" id="BUSITYPE" name="BUSITYPE">
							<option value="01">无扣费身份验证</option>
							<option value="02">扣1分钱身份验证</option>

						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>区域编码：
						<input name="AREACODE" type="text" size="6" maxlength="6"
							value="${map.AREACODE}">
					</td>
				</tr>				
				<tr>
					<td>
						<span style="color:red" >*</span>银行账户户名：
						<input name="TRANSACCNAME" type="text" size="128" maxlength="128"
							value="${map.TRANSACCNAME}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" t>*</span>银行账号：
						<input name="BANKACCT" type="text" size="30" maxlength="30"
							value="${map.BANKACCT}">
					</td>
				</tr>
				<tr>
					<td>
						开户行名称：
						<input name="OPENBANK" type="text" size="128" maxlength="128"
							value="${map.OPENBANK}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>银行编码：
						<input name="BANKCODE" type="text" size="6" maxlength="6"
							value="${map.BANKCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>对公/对私标识：
						<select id="PRIVATEFLAG" name="PRIVATEFLAG">
							<option value="0" selected="selected">对公</option>
							<option value="1">对私</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡折标识：
						<select id="CARDFLAG" name="CARDFLAG">
							<option value="1" selected="selected">借记卡</option>
							<option value="2">信用卡（贷记卡）</option>
							<option value="4">存折</option>
							<option value="8">公司账户</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						信用卡有效期：
						<input name="VALIDITY" type="text" size="20" maxlength="20"
							value="${map.VALIDITY}">
					</td>
				</tr>			
				<tr>
					<td>
						信用卡校验码：
						<input name="CVN2" type="text" size="10" maxlength="10"
							value="${map.CVN2}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>证件类型：
						<select name="CERTTYPE">
							<option value="00" selected="selected">身份证</option>
							<option value="01">护照</option>						
							<option value="02">军人证</option>
							<option value="03">户口本</option>														
							<option value="06">港澳通行证</option>
							<option value="08">学生证</option>
							<option value="09">工作证</option>
							<option value="10">工商执照</option>
							<option value="11">警官证</option>
							<option value="12">事业单位编码</option>
							<option value="13">房产证</option>
							<option value="51">组织机构代码</option>
							<option value="99">其他证件</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>证件号：
						<input name="CERTNO" type="text" size="20" maxlength="20"
							value="${map.CERTNO}">
					</td>
				</tr>
				<tr>
					<td>
						联系电话：
						<input name="TEL" type="text" size="20" maxlength="20"
							value="${map.TEL}">
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
						<input name="REMARK1" type="text" size="15" maxlength="200"
							value="${map.REMARK1}">
					</td>
				</tr>
				<tr>
					<td>
						预留域2：
						<input name="REMARK2" type="text" size="15" maxlength="200"
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
