<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF01011.txt");
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
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF01011">
			<input type="hidden" name="method" value="inf01011">
			<input type="hidden" name="CONTRACTTYPE" value="0001">
			<table>
				<tr>
					<td><font color="red" size="5">签约接口</font></td>
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
						<input name="ORDERSEQ" type="text" size="15" maxlength="32"
							value="${map.ORDERSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>客户编码：
						<input name="CUSTCODE" type="text" size="54" maxlength="64"
							value="${map.CUSTCODE}">
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
						项目：
						<select name="PROJECT">
							<option value="000" selected="selected">默认项目</option>
							<option value="001">电信空充渠道</option>
							<option value="002">电信终端公司</option>
							<option value="003">电信合作厅</option>
							<option value="007">天翼</option>
							<option value="008">讯源</option>
							<option value="009">华瑞达</option>
							<option value="006">企业账户平台</option>
							<option value="018">电子代办</option>
							<option value="020">三直</option>
							<option value="021">农村金融</option>
							<option value="022">电信号百</option>
							<option value="024">银联合作</option>
							<option value="025">保险合作</option>
							<option value="026">物业服务</option>
							<option value="027">电信社会代理商</option>
							<option value="028">电信佣金服务</option>
							<option value="999">其它</option>
							<option value="098" title="测试项目-全国">测试项目-全国</option>
							<option value="099" title="测试项目-广东">测试项目-广东</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>省代码：
						<input name="PROVINCE" type="text" size="2" maxlength="2"
							value="${map.PROVINCE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>地市代码：
						<input name="AREACODE" type="text" size="6" maxlength="6"
							value="${map.AREACODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>企业类型：
						<input name="PRTNTYPE" type="text" size="1" maxlength="1"
							value="${map.PRTNTYPE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>网点性质：
						<input name="BRANCHPROP" type="text" size="32" maxlength="32"
							value="${map.BRANCHPROP}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>用户全称：
						<input name="BRANCHNAME" type="text" size="64" maxlength="64"
							value="${map.BRANCHNAME}">
					</td>
				</tr>		
				<tr>
					<td>
						<span style="color:red">*</span>外部业务编码
						<input name="BRANCHCODE" type="text" size="20" maxlength="20"
							value="${map.BRANCHCODE}">
					</td>
				</tr>			
				<tr>
					<td>
						<span style="color:red">*</span>验证方式：
						<select name="VERITYPE">
							<option value="0001" selected="selected">无扣费身份验证</option>
							<option value="0002">扣1分钱验证</option>
							<option value="0003">无验证</option>
							<option value="9999">智能验证</option>
						</select>
					</td>
				</tr>
			    <tr>
					<td>
						<span style="color:red">*</span>代收付类型：
						<select name="BUSITYPE">
							<option value="BT001" selected="selected">代收</option>
							<option value="BT002" >代付</option>
						</select>
					</td>
				</tr>
                <tr>
					<td>
						<span style="color:red" >*</span>银行账户户名：
						<input name="ACCNAME" type="text" size="128" maxlength="128"
							value="${map.ACCNAME}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>银行账户：
						<input name="BANKACCT" type="text" size="25" maxlength="20"
							value="${map.BANKACCT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>区域编码：
						<input name="BANKAREA" type="text" size="6" maxlength="6"
							value="${map.BANKAREA}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>开户行名称：
						<input name="BANKINFO" type="text" size="128" maxlength="128"
							value="${map.BANKINFO}">
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
						<span style="color:red">*</span>对公/对私标识：
						<select id="PRIVATEFLAG" name="PRIVATEFLAG">
							<option value="0">对公</option>
							<option value="1" selected="selected">对私</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡折标识：
						<select id="CARDFLAG" name="CARDFLAG" onchange="changeVal(this.value)">
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
						<input name="CREDITVALIDTIME" type="text" size="6" maxlength="6"
							value="${map.CREDITVALIDTIME}">
					</td>
				</tr>
				<tr>
					<td>
						信用卡验证码：
						<input name="CREDITVALIDCODE" type="text" size="8" maxlength="8"
							value="${map.CREDITVALIDCODE}">
					</td>
				</tr>				
				<tr>
					<td>
						<span style="color:red">*</span>证件类型：
						<select name="CERTCODE">
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
						<span style="color:red" >*</span>证件号码：
						<input name="CERTNO" type="text" size="32" maxlength="32"
							value="${map.CERTNO}">
					</td>
				</tr>				
				<tr>
					<td>
						<span style="color:red">*</span>联系号码：
						<input name="CONTACTPHONE" type="text" size="15" maxlength="32"
							value="${map.CONTACTPHONE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>联系地址：
						<input name="CONTACTADDR" type="text" size="64" maxlength="64"
							value="${map.CONTACTADDR}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>收款单位名称：
						<input name="RECVCORP" type="text" size="64" maxlength="64"
							value="${map.RECVCORP}">
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
