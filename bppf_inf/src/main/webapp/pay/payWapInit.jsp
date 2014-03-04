<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="${sessionScope.path}/css/loadlayer.css">
		<script type="text/javascript" src="${sessionScope.path}/js/loadlayer.js" charset="utf-8"></script>
		
		<SCRIPT LANGUAGE="JavaScript">
			function DetectActiveX(progId)
			 {
			    try
			    {
			       var comActiveX = new ActiveXObject(progId);   
			    }
			    catch(e)
			    {
			       return false;   
			    }
			    return true;
			 }
			 
				function showLoadLayer(title) {
        			var loadLayer = new LoadLayer("${sessionScope.path}/images/loadlayer/loading.gif", "loadLayerDefault", "loadTextDefault", null, title);
        			loadLayer.init();
        			loadLayer.show();
   				 }
   				 
   				 function checkActiveX() {
   				 	   	if (!DetectActiveX("PCENC.PCENCCtrl.1")) {
				        	alert("支付安全控件未安装，请将本站网址加入可信站点后，点击IE信息栏进行安装!");
				        	return false;
				        } else {
				        	return true;
				        }
   				 }
   				 
   				 window.onload = function() {
   				 	showLoadLayer("正在初始化支付页面...");
   				 	
   				 	 if (checkActiveX()) {
   				 	 	Form1.submit();
				     }
   				 }
		</SCRIPT>
		</head>
		<body>

			<form id="Form1" name="Form1"
			action="${sessionScope.path}/payWapAccept.do" method="post">
			<input type="hidden" id="AGENTCODE" name="AGENTCODE"
				value="<%=request.getParameter("AGENTCODE")%>">
			<input type="hidden" id="AREACODE" name="AREACODE"
				value="<%=request.getParameter("AREACODE")%>">
			<input type="hidden" id="TXNCHANNEL" name="TXNCHANNEL"
				value="<%=request.getParameter("TXNCHANNEL")%>">
			<input type="hidden" id="PAYTYPE" name="PAYTYPE"
				value="<%=request.getParameter("PAYTYPE")%>">
			<input type="hidden" id="TXNAMOUNT" name="TXNAMOUNT"
				value="<%=request.getParameter("TXNAMOUNT")%>">
			<input type="hidden" id="GOODSCODE" name="GOODSCODE"
				value="<%=request.getParameter("GOODSCODE")%>">
			<input type="hidden" id="GOODSNAME" name="GOODSNAME"
				value="<%=request.getParameter("GOODSNAME")%>">
			<input type="hidden" id="TRADESEQ" name="TRADESEQ"
				value="<%=request.getParameter("TRADESEQ")%>">
			<input type="hidden" id="TRADETIME" name="TRADETIME"
				value="<%=request.getParameter("TRADETIME")%>">
			<input type="hidden" id="REQUESTSEQ" name="REQUESTSEQ"
				value="<%=request.getParameter("REQUESTSEQ")%>">
			<input type="hidden" id="BACKMERCHANTURL" name="BACKMERCHANTURL"
				value="<%=request.getParameter("BACKMERCHANTURL")%>">
			<input type="hidden" id="MERCHANTURL" name="MERCHANTURL"
				value="<%=request.getParameter("MERCHANTURL")%>">
				
			<input type="hidden" id="ENCODETYPE" name="ENCODETYPE"
				value="<%=request.getParameter("ENCODETYPE")%>">

			<input type="hidden" id="MAC" name="MAC" value="<%=request.getParameter("MAC")%>">
			
		
		     <div style="display: none">
		     	<object id="PCENC1"
								classid="clsid:C241E62D-2EAD-4972-80F5-D01AC80033B8"
								codebase="${sessionScope.path}/activeX/PCENCCAB.cab#version=1,0,0,1"
								width="250" height="120" style="border:1px solid gray;"></object>
		     </div>
	</form>
	</body>
</html>
