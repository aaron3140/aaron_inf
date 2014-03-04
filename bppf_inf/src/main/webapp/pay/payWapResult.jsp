<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<SCRIPT LANGUAGE="JavaScript">
			function addPayCard(){
				document.Form1.action="${sessionScope.path}/payWapAccept.do";
				document.Form1.submit();
			}
			javascript:window.history.forward(1);
			function toAgentPage(){
				document.Form1.action="${sessionScope.path}/payWapMerchantURL.do";
				document.Form1.submit();
			}

		</SCRIPT>
		<style type="text/css">
	    .ordermsg_top{
			height:82px;
			width:265px;
			background:url(images/rs_k_top.jpg) right top no-repeat;
			overflow:hidden;
			clear:both;
			font-size:13px;
		}
		.ordermsg_middle{
			background:url(images/zf_k_mid.jpg) right top repeat-y;
			overflow:hidden;
			width:265px;
			clear:both;
			color:#5D5D5D;
		}
		.ordermsg_bottom{
			height:68px;
			width:265px;
			background:url(images/zf_k_btm.jpg) right top no-repeat;
			overflow:hidden;
			clear:both;
		}
		.ordermsg_table{
			font-size:13px;
			color:#333333;
			width:265px;
			line-height:260%;
			margin-top:10px;
		}
		.main_table{
		    valign:middle;
		    margin-top:auto;
		    margin-bottom:auto;
		}
		body {
		
			width:800px;
			margin:0 auto;
			text-align:center;
		}
	</style>
	</head>

	<body>
		<form id="Form1" name="Form1" action="${sessionScope.path}/payWap.do"
			method="post">

			<input type="hidden" id="CARDLIST" name="CARDLIST"
				value="	${cardList}">
			<input type="hidden" id="RESPONSECODE" name="RESPONSECODE"
				value="${responsecode }">
			<input type="hidden" id="RESPONSECONTENT" name="RESPONSECONTENT"
				value="${responsecontent }">
			<input type="hidden" id="AGENTCODE" name="AGENTCODE"
				value="${paywap.agentCode }">
			<input type="hidden" id="AREACODE" name="AREACODE"
				value="${paywap.areaCode }">
			<input type="hidden" id="TXNCHANNEL" name="TXNCHANNEL"
				value="${paywap.txnChannel }">
			<input type="hidden" id="PAYTYPE" name="PAYTYPE"
				value="${paywap.payType }">
			<input type="hidden" id="TXNAMOUNT" name="TXNAMOUNT"
				value="${paywap.txnAmount }">
			<input type="hidden" id="GOODSCODE" name="GOODSCODE"
				value="${paywap.goodsCode }">
			<input type="hidden" id="GOODSNAME" name="GOODSNAME"
				value="${paywap.goodsName }">
			<input type="hidden" id="TRADESEQ" name="TRADESEQ"
				value="${paywap.tradeSeq }">
			<input type="hidden" id="TRADETIME" name="TRADETIME"
				value="${paywap.tradTime }">
			<input type="hidden" id="REQUESTSEQ" name="REQUESTSEQ"
				value="${paywap.requestSeq }">
			<input type="hidden" id="AGENTNAME" name="AGENTNAME"
				value="${paywap.agentName }">
			<input type="hidden" id="BACKMERCHANTURL" name="BACKMERCHANTURL"
				value="${paywap.backMerChanturl }">
			<input type="hidden" id="MERCHANTURL" name="MERCHANTURL"
				value="${paywap.merChanturl }">
			<input type="hidden" id="ENCODETYPE" name="ENCODETYPE"
				value="${enCodeType }">
			<input type="hidden" id="key" name="key" value="${key }">
			<input type="hidden" id="orderId" name="orderId" value="${orderId }">
			<input type="hidden" id="MAC" name="MAC" value="${MAC }">
			<table style="height:100%">
				<tr>
					<td>
						<table class="main_table">

							<tr valign="top">

								<td>

									<div class="ordermsg_top" style="float: left;"></div>

									<div class="ordermsg_middle">

										<table class="ordermsg_table">
											<tr>
												<td align="center">
													${responsecontent }
												</td>
											</tr>
										</table>
									</div>
									<div class="ordermsg_bottom"></div>
								</td>
							</tr>
							<tr>
								<td align="center">
									<c:if test="${responsecode == '2001209' }">
										<input type="button" value="添加另一张卡进行多卡支付" id="addCard"
											onclick="addPayCard();">
									</c:if>
									<input type="button" value="返回商户页面" id="toPage"
										onclick="toAgentPage();">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>

		</form>
	</body>
</html>
