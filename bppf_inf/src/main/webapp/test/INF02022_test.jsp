<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF02022.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
	pageContext.setAttribute("REGDATE",sdf.format(new Date()));
	pageContext.setAttribute("ORDERNO",System.currentTimeMillis()+123456);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
	<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
	
	<script type="text/javascript">
	
		function onlaod(){

			onRegChange();
		}
		
	function onRegChange(){
	
	var REGTYPE = $("#REGTYPE").find("option:selected").val();

	var per = $(".per");
	
	var ent = $(".ent");

		if("PRT1001"==REGTYPE){
	
			per.show();
			ent.hide();
	
		}else{
	
			per.hide();
			ent.show();
		}
	
	}

	</script>
	</head>
	
	<body onload="onlaod()">

		<form name="paymentInfo" method="post"
			action="${sessionScope.path}/httpipos" onsubmit="">
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF02022">
			<input type="hidden" name="method" value="inf02022">
			<table>
				<tr>
					<td><font color="red" size="5">商户注册接口</font></td>
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
						注册类型：
						<select name="REGTYPE" id="REGTYPE" onchange="onRegChange()">
										<option value="PRT1001" selected="selected">个体商户</option>
										<option value="PRT1002">企业商户</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>企业账户:
							<input name="CUSTNAME" type="text" size="64" maxlength="64" 
							value="${map.CUSTNAME}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>业务类型:
							<input name="PRODUCTS" type="text" size="32" maxlength="32" 
							value="${map.PRODUCTS}">100|104
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>企业名称:
							<input name="ENTERNAME" type="text" size="32" maxlength="32" 
							value="${map.ENTERNAME}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>企业地址:
							<input name="ENTERADDRESS" type="text" size="32" maxlength="32" 
							value="${map.ENTERADDRESS}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>所在地区编码:
							<input name="AREACODE" type="text" size="6" maxlength="6" 
							value="${map.AREACODE}">
					</td>
				</tr>
				<tr>
					<td>
						所属代理商:
							<input name="AGENTCODE" type="text" size="32" maxlength="32" 
							value="${map.AGENTCODE}">
					</td>
				</tr>
				<tr>
					<td>
						所属行业：
						<select name="TRADE">
										<option value="01" selected="selected">零售连锁</option>
										<option value="02">通讯</option>
										<option value="03">证券投资</option>
										<option value="04">快消品</option>
										<option value="05">耐用品</option>
										<option value="06">物流</option>
										<option value="07">商旅</option>
										<option value="08">保险</option>
										<option value="09">电子商务</option>
										<option value="10">外贸</option>
										<option value="11">教育</option>
										<option value="12">其他</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>营业执照编码:
							<input name="BUSINESSLICENCE" type="text" size="64" maxlength="64" 
							value="${map.BUSINESSLICENCE}"> 个人商户时可选填
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>申请人:
							<input name="APPLYER" type="text" size="32" maxlength="32" 
							value="${map.APPLYER}">
					</td>
				</tr>
				<tr class="per" >
					<td>
						<span style="color:red">*</span>证件号码:
							<input name="CERTNO" type="text" size="18" maxlength="18" 
							value="${map.CERTNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>电子邮箱:
							<input name="EMAIL" type="text" size="32" maxlength="32" 
							value="${map.EMAIL}">
					</td>
				</tr>
				<tr class="ent">
					<td>
						<span style="color:red">*</span>组织机构代码:
							<input name="ENTERORGCODE" type="text" size="64" maxlength="64" 
							value="${map.ENTERORGCODE}">
					</td>
				</tr>
				<tr class="ent">
					<td>
						<span style="color:red">*</span>企业法人:
							<input name="ENTERPERSON" type="text" size="32" maxlength="32" 
							value="${map.ENTERPERSON}">
					</td>
				</tr>
				<tr class="ent">
					<td>
						<span style="color:red">*</span>法人身份证:
							<input name="ENTERCERNO" type="text" size="32" maxlength="32" 
							value="${map.ENTERCERNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>注册时间:
							<input name="REGDATE" type="text" size="14" maxlength="14" 
							value="${REGDATE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>验证码:
							<input name="VERIFYCODE" type="text" size="12" maxlength="12" 
							value="${map.VERIFYCODE}">
					</td>
				</tr>
				<tr>
					<td>
						验证类型：
						<select name="VERIFYTYPE">
										<option value="001" selected="selected">短信验证</option>
										<option value="000">无需验证</option>
										<option value="002">支付密码</option>
						</select>
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
						<input name="submit" value="提交" type="submit">
					</td>
				</tr>

			</table>
		</form>

	</body>
</html>
