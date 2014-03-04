<%@page import="common.utils.FileUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ include file="/common/imports.jsp"%>
<%
	java.util.Map map=FileUtil.getPageparams("WEB_INF06203.txt");
	pageContext.setAttribute("map",map);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	String dateStr = sdf.format(new Date());
	pageContext.setAttribute("acctDate",dateStr);
	Date currentDate = new Date();
	String sequence = "666" + sdf.format(currentDate) + "123";
	String currentTime = sdf.format(currentDate);
	String orderSeq = System.currentTimeMillis() + "";
	orderSeq = orderSeq.substring(orderSeq.length() - 9, orderSeq.length());
	request.setAttribute("orderSeq", orderSeq);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	</head>
	
	<body onload="">
		<form name="paymentInfo" method="post" id="paymentInfo" 
			action="${sessionScope.path}/httppost" >
			<input name="WEBSVRCODE" type="hidden" size="16" maxlength="16" value="INF06203">
			<input type="hidden" name="method" value="inf06203">
			<table>
				<tr>
					<td><font color="red" size="5">[东莞公交]卡操作接口 </font></td>
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
						<span style="color:red" >*</span>POS机流水号：
						<input name="ORDERSEQ" type="text" size="15" maxlength="32"
							value="${orderSeq}">
					</td>
				</tr>
				<tr>
					<td>
						客户编码：
						<input name="CUSTCODE" type="text" size="15" maxlength="32"
							value="${map.CUSTCODE}">(注：当渠道为60时，可为空)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>客户终端号：
						<input name="TMNNUMNO" type="text" size="15" maxlength="12"
							value="${map.TMNNUMNO}">(由翼支付系统为客户生成的12位终端号（非60渠道）或8位外部终端号(60渠道))
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>操作员:
							<input name="STAFFCODE" type="text" size="32" maxlength="32" id="staffCode"
							value="${map.STAFFCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>E卡号:
							<input name="ECARDNO" type="text" size="32" maxlength="32" 
							value="${map.ECARDNO}">(60渠道必填)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>PSAM卡号:
							<input name="PSAMCARDNO" type="text" size="32" maxlength="32"
							value="${map.PSAMCARDNO}">(60渠道必填)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>密码类型:
							<input name="PASSFLAG" type="text" size="32" maxlength="32"
							value="${map.PASSFLAG}">(60渠道必填)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red">*</span>交易密码：
						<input name="PAYPASSWORD" type="text" size="15" maxlength="64"
							value="${map.PAYPASSWORD}">(经加密)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>受理时间：
						<input name="TRADETIME" type="text" size="30"
							maxlength="30" value="<%=currentTime%>">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡操作交易类型：
						<input name="CARDOPRTYPE" type="text" size="15" maxlength="32"
							value="${map.CARDOPRTYPE}">(充值：2062,	售卡(开卡)：2063)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>城市代码：
						<input name="CITYCODE" type="text" size="15" maxlength="32"
							value="${map.CITYCODE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡内号：
						<input name="CARDID" type="text" size="15" maxlength="32"
							value="${map.CARDID}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>主卡类型：
						<input name="CARDMKND" type="text" size="15" maxlength="32"
							value="${map.CARDMKND}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>子卡类型：
						<input name="CARDSKND" type="text" size="15" maxlength="32"
							value="${map.CARDSKND}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>APDU指令：
						<input name="COMMAND" type="text" size="15" maxlength="32"
							value="${map.COMMAND}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>APDU指令长度：
						<input name="COMMANDLEN" type="text" size="15" maxlength="32"
							value="${map.COMMANDLEN}">(充值送0)
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡型：
						<input name="CARDMODEL" type="text" size="15" maxlength="32"
							value="${map.CARDMODEL}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>业务类型：
						<input name="TRANSTYPE" type="text" size="15" maxlength="32"
							value="${map.TRANSTYPE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>押金：
						<input name="DEPOSIT" type="text" size="15" maxlength="32"
							value="${map.DEPOSIT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>应收金额：
						<input name="ORIGAMT" type="text" size="15" maxlength="32"
							value="${map.ORIGAMT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡有效期：
						<input name="CARDVALDATE" type="text" size="15" maxlength="32"
							value="${map.CARDVALDATE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡片原额：
						<input name="SRCBAL" type="text" size="15" maxlength="32"
							value="${map.SRCBAL}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡片交易流水：
						<input name="CARDSEQ" type="text" size="15" maxlength="32"
							value="${map.CARDSEQ}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡片密钥版本：
						<input name="KEYVER" type="text" size="15" maxlength="32"
							value="${map.KEYVER}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡片算法标志：
						<input name="ALGIND" type="text" size="15" maxlength="32"
							value="${map.ALGIND}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡片随机数：
						<input name="CARDRAND" type="text" size="15" maxlength="32"
							value="${map.CARDRAND}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>充资返回信息MAC1：
						<input name="MAC1" type="text" size="15" maxlength="32"
							value="${map.MAC1}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>分散因子：
						<input name="DIVDATA" type="text" size="15" maxlength="32"
							value="${map.DIVDATA}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>批次号：
						<input name="BATCHNO" type="text" size="15" maxlength="32"
							value="${map.BATCHNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>密钥：
						<input name="KEYSET" type="text" size="15" maxlength="32"
							value="${map.KEYSET}">（签到时返回的值）
					</td>
				</tr>
				<%--<tr>
					<td>
						<span style="color:red" >冲正参数</span>
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡计数器：
						<input name="CARDCNT" type="text" size="15" maxlength="32"
							value="${map.CARDCNT}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>交易前余额：
						<input name="BEFBALANCE" type="text" size="15" maxlength="32"
							value="${map.BEFBALANCE}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>卡内版本号：
						<input name="CARDVERNO" type="text" size="15" maxlength="32"
							value="${map.CARDVERNO}">
					</td>
				</tr>
				<tr>
					<td>
						<span style="color:red" >*</span>交易金额：
						<input name="TXNAMT" type="text" size="15" maxlength="32"
							value="${map.TXNAMT}">
					</td>
				</tr>
				<tr>
					<td>
						售卡方式：
						<input name="SALEMODE" type="text" size="15" maxlength="32"
							value="${map.SALEMODE}">（开卡冲正时必填）
					</td>
				</tr>
				<tr>
					<td>
						手续费：
						<input name="HANDINGCHARGE"   type="text" size="15" maxlength="8"
							value="${map.HANDINGCHARGE}">（充值冲正时必填）
					</td>
				</tr>
				<tr>
					<td>
						POS终端上笔充值流水号：
						<input name="LASTPOSSVSEQ" type="text" size="64" maxlength="64"
							value="${map.LASTPOSSVSEQ}">（充值冲正时必填）
					</td>
				</tr>
				--%><tr>
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
