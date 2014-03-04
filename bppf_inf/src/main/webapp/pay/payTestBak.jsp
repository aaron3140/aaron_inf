<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="${sessionScope.path}/css/loadlayer.css">
		<script type="text/javascript" src="${sessionScope.path}/js/loadlayer.js" charset="utf-8"></script>

		<SCRIPT LANGUAGE="JavaScript">
				var key = '';
				function getCardNo() 
				{
				    var ret = document.payApplet.getCardNo();
				    alert(ret);
				}
				function getCardPwd() 
				{
				    var ret = document.payApplet.getPassword();
				    alert(ret);
				}
				function changePwd(){
					var ret = document.payApplet.encrypt();
					alert(ret);
					key = ret;
				}
				function decrypt(){
					var ret = document.payApplet.decrypt(key);
					alert(ret);
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
				
				function validate ()  
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
				        alert("^-^ OK");  
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
				    }  
</SCRIPT>

	</head>

	<body>
	
		<form onsubmit="return validate();" action="" method="post">
		<br>
		-------------------------------------APPLET方式--------------------------------------------
		<br>
			<applet code="common.applet.PayForm.class"
				codebase="${sessionScope.path}/applet/"
				archive="${sessionScope.path}/applet/PCENC.jar" name="payApplet"
				width="220 " height="90 " style="border:1px solid gray;"></applet>
			<br />


			请输入验证码：
			<input type="text" id="vcode" style="width: 60;" maxLength="5" />

			<img src="" id="code" />
			<a href="#" mce_href="#" onclick="javascript:show();return
				false;">看不清,换一张！</a><br>
			<input type="submit" />
		</form>


		<br />
		<button onClick="getCardNo()">
			获取卡号
		</button>
		<br />
		<br />
		<button onClick="getCardPwd()">
			获取密码
		</button>
		<br />
		<br />
		<button onClick="changePwd()">
			获取加密后密码
		</button>
		<br />
		<br />
		<br>
		
		
		<br>
		-------------------------------------ACTIVEX方式--------------------------------------------
		<br>
<script>
    var decrypt = "";
    function getEncrypt() {
        decrypt = PCENC.getEncrypt();
        alert(decrypt);
    }
    function getDecrypt() {
        alert(PCENC.getDecrypt(decrypt));
    }

</script>
<object  id ="PCENC"  classid ="clsid:C241E62D-2EAD-4972-80F5-D01AC80033B8" codebase="${sessionScope.path}/activeX/PCENCCAB.cab#version=1,0,0,1" width="250" height="120" style="border:1px solid gray;"></object > 
<br/><br/>
<input  type ="button"  onclick ="alert(PCENC.getCardNo());"  value ="显示卡号"   />
<input  type ="button"  onclick ="alert(PCENC.getCardPwd());"  value ="显示密码"   />
<input  type ="button"  onclick ="getEncrypt();"  value ="显示加密后的密码"   />
<input  type ="button"  onclick ="getDecrypt();"  value ="显示解密后的密码"   />

		<br>
		-------------------------------------显示LoadLayer--------------------------------------------
		
		<br>
		<input  type ="button"  onclick ="showLoadLayer();"  value ="显示LoadLayer"   />
		
		<script>
    function showLoadLayer() {
        			var loadLayer = new LoadLayer("${sessionScope.path}/images/loadlayer/loading.gif", "loadLayerDefault", "loadTextDefault", null, "正在初始化...");
        			loadLayer.init();
        			loadLayer.show();
    }
    


</script>
	</body>
</html>
