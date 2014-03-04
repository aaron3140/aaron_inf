<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<script type="text/javascript"
			src="${sessionScope.path}/common/jquery-1.4.2.min.js"></script>
		<SCRIPT LANGUAGE="JavaScript">
				var payType="";
				
				function getPayTypeName(){
				 if("01"==${payWap.payType }){
				 	document.getElementById("PAYTYPENAME").innerHTML = "单卡支付";
					}
				 if("02"==${payWap.payType }){
					document.getElementById("PAYTYPENAME").innerHTML ="多卡支付";
					}
				}
				
				function checkCardNoAndPwd(){
					$('#checkResult').html('');
					//var cardNo = PCENC.getCardNo();
					var cardNo = document.payApplet.getCardNo();
					//var pwd = PCENC.getEncrypt();
					var pwd = document.payApplet.getPassword();
					var cardType = $('#CARDTYPE').val();
					cardNo = cardNo.replace(/(^\s+)|(\s+$)/g,"");
   					cardNo = cardNo.replace(/\s/g,"");
					if(cardNo=="" || pwd==""){
						alert('卡号和密码不能为空');
						return false;
					}else{
						if(validate()){
						    $('#next').attr('disabled','disabled'); 
							//pwd = PCENC.getEncrypt();
							pwd = document.payApplet.encrypt();
							$.ajax({
							  type: 'POST',
							  url: '${sessionScope.path}/payWapCheckCard.do',
							  data: {CARDNO:cardNo,CARDPWD:pwd,CARDTYPE:cardType},
							  success:function(result){
							 	 if(result==''){
							 	 	document.getElementById("CARDNO").value=cardNo;
									document.getElementById("CARDPWD").value=pwd;
									document.Form1.submit();
							 	 }else{
							 	  	$('#next').attr('disabled',''); 
							  		$('#checkResult').html(result);
							  		 show();
							  	 }
						      }
							});
						}
					}
				}
			
		</SCRIPT>

		<SCRIPT LANGUAGE="JavaScript">
				var code ; //在全局 定义验证码  
				function createCode()  
				{  
				    code = "";  
				    var codeLength = 5;//验证码的长度  
				    //所有候选组成验证码的字符，可以用中文  
				    var selectChar = new Array(0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z');  
				    for(var i=0;i<codeLength;i++)  
				    {  
				        var charIndex = Math.floor(Math.random()*60);  
				        code +=selectChar[charIndex];  
				    }  
				    return code;  
				}  
				
				function validate()  
				{  
				    var inputCode = document.getElementById("vcode").value.toLowerCase();  
				    if(inputCode.length <=0)  
				    {  
				        alert("请输入验证码！");  
				        return false;  
				    }  
				    else if(inputCode != code.toLowerCase())  
				    {  
				        alert("验证码输入错误！");  
				        show();//刷新验证码  
				        return false;  
				    }  
				    else  
				    {  
				        return true;  
				    }  
				}  
				function show(){  
				        //显示验证码  
				        document.getElementById("code").src="${sessionScope.path}/vimage.do?code="+createCode();  
				}  
				window.onload = function() {//document.onload=show();  
				
				        show();//页面加载时加载验证码  
				        //这时无论在ie还是在firefox中，js没有加载完，页面的东西是不会被执行的；  
				        getPayTypeName();
				    }  
		</SCRIPT>


		<style type="text/css">
	    .ordermsg_top{
			height:82px;
			width:265px;
			background:url(../images/zf_k_top.jpg) right top no-repeat;
			overflow:hidden;
			clear:both;
			font-size:13px;
		}
		.ordermsg_middle{
			background:url(../images/zf_k_mid.jpg) right top repeat-y;
			overflow:hidden;
			width:265px;
			clear:both;
			
		}
		.ordermsg_bottom{
			height:68px;
			width:265px;
			background:url(../images/zf_k_btm.jpg) right top no-repeat;
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
		.top_div_d2{
		background:url(../images/top_menu_bg.jpg) right top no-repeat;
		overflow:hidden;
		clear:both;
		width:614px;
		height:40px;
		}
		.middle_div_d2{
			background:url(../images/zf_k_midl.jpg) right top repeat-y;
			overflow:hidden;
			clear:both;
			width:614px;
		}
		.bottom_div_d2{
			height:20px;
			width:614px;
			background:url(../images/zf_k_bottom.jpg) right top no-repeat;
			overflow:hidden;
			clear:both;
		}
		.table_card_content{
			margin-left:50px;
			font-size:13px;
			width:550px;
			margin-top:30px;
		}
		body {
			width:948px;
			margin:0px auto;
			text-align:center;
		}
		.sub_button{
			background:url(../images/zf_btn_1.jpg) no-repeat ;
			width:105px;
			height:28px;
			border:0px;
			font-weight:bold; 
			cursor:pointer;
			margin-top:10px;
		}
		.td1{
			text-align:center;
		}
    </style>
	</head>

	<body>
		<form id="Form1" name="Form1"
			action="${sessionScope.path}/payWapExecute.do" method="post">
			<input type="hidden" id="AGENTCODE" name="AGENTCODE"
				value="${payWap.agentCode }">
			<input type="hidden" id="AREACODE" name="AREACODE"
				value="${payWap.areaCode }">
			<input type="hidden" id="TXNCHANNEL" name="TXNCHANNEL"
				value="${payWap.txnChannel }">
			<input type="hidden" id="PAYTYPE" name="PAYTYPE"
				value="${payWap.payType }">
			<input type="hidden" id="TXNAMOUNT" name="TXNAMOUNT"
				value="${payWap.txnAmount }">
			<input type="hidden" id="GOODSCODE" name="GOODSCODE"
				value="${payWap.goodsCode }">
			<input type="hidden" id="GOODSNAME" name="GOODSNAME"
				value="${payWap.goodsName }">
			<input type="hidden" id="TRADESEQ" name="TRADESEQ"
				value="${payWap.tradeSeq }">
			<input type="hidden" id="TRADETIME" name="TRADETIME"
				value="${payWap.tradTime }">
			<input type="hidden" id="REQUESTSEQ" name="REQUESTSEQ"
				value="${payWap.requestSeq }">
			<input type="hidden" id="BACKMERCHANTURL" name="BACKMERCHANTURL"
				value="${payWap.backMerChanturl }">
			<input type="hidden" id="MERCHANTURL" name="MERCHANTURL"
				value="${payWap.merChanturl }">
			<input type="hidden" id="AGENTNAME" name="AGENTNAME"
				value="${payWap.agentName }">
			<input type="hidden" id="CARDNO" name="CARDNO" value="">
			<input type="hidden" id="CARDPWD" name="CARDPWD" value="">
			<input type="hidden" id="ENCODETYPE" name="ENCODETYPE"
				value="${enCodeType }">
			<input type="hidden" id="key" name="key" value="${key }">
			<input type="hidden" id="MAC" name="MAC" value="${MAC }">
			<input type="hidden" id="CARDLIST" name="CARDLIST"
				value="${cardList }">
			<input type="hidden" id="paywapBeginTime" name="paywapBeginTime"
				value="${paywapBeginTime }">
			<table class="main_table">

				<tr valign="top">

					<td>

						<div class="ordermsg_top" style="float: left;"></div>

						<div class="ordermsg_middle">

							<table class="ordermsg_table">
								<tr>
									<td align="right">
										交易流水：
									</td>
									<td align="left">
										${payWap.tradeSeq }
									</td>
								</tr>
								<tr>
									<td align="right" width="30%">
										商户名称：
									</td>
									<td align="left">
										${payWap.agentName }
									</td>
								</tr>
								<tr>
									<td align="right">
										商品名称：
									</td>
									<td align="left">
										${payWap.goodsName }
									</td>
								</tr>
								<tr>
									<td align="right">
										订单币种：
									</td>
									<td align="left">
										人民币
									</td>
								</tr>
								<tr>
									<td align="right">
										支付方式：
									</td>
									<td align="left">
										<label id="PAYTYPENAME"></label>
									</td>
								</tr>
								<tr>
									<td align="right">
										订单金额：
									</td>
									<td align="left">
										<font color="#E95412" style="font-weight: bold">${payWap.txnAmount }</font>元
									</td>
								</tr>
							</table>
						</div>
						<div class="ordermsg_bottom"></div>
					</td>
					<td width="613px">
						<div class="top_div_d2"></div>
						<div class="middle_div_d2">
							<table class="table_card_content">
								<tr>
									<td>
										<%--<object  id ="PCENC"  classid ="clsid:C241E62D-2EAD-4972-80F5-D01AC80033B8" codebase="${sessionScope.path}/activeX/PCENCCAB.cab#version=1,0,0,0" width="250" height="120" style="border:1px solid gray;"></object > --%>
										<applet code="common.applet.PayForm.class"
											codebase="${sessionScope.path}/applet/"
											archive="${sessionScope.path}/applet/PCENC.jar"
											name="payApplet" width="220 " height="90 "
											style="border:1px solid gray;"></applet>
									</td>
								</tr>
								<tr>
									<td>
										<label id="checkResult" style="color:red;"></label>
										<br>
										卡类型：
										<select id="CARDTYPE" name="CARDTYPE">
											<option value="1">
												天翼支付卡
											</option>
											<option value="2">
												11888卡
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										验证码：
										<input type="text" id="vcode" style="width: 70;" maxLength="5" />

										<img src="" id="code" />
										<a href="#" mce_href="#"
											onclick="javascript:show();returnfalse;">看不清,换一张！</a>
										<br>

									</td>
								</tr>
								<tr>
									<td class="td1">
										<input type="button" id="next" value="" class="sub_button"
											onclick="checkCardNoAndPwd();">
									</td>
								</tr>
							</table>
						</div>
						<div class="bottom_div_d2"></div>
					</td>
					<td>
						<c:if test="${cardList !=null }">
							<table>
								<tr>
									<td>
										已添加的支付卡：
										<br>
										<c:forEach var="card" items="${cardList}" varStatus="status">
											第${status.count}张卡${card}<br>
										</c:forEach>
									</td>
								</tr>
							</table>
						</c:if>
					</td>
				</tr>

			</table>
		</form>
	</body>
</html>
