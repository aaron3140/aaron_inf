//所有前置交易客户端API的公共js
$(document).ready(function(){
	   $("dt:contains('前置交易客户端API')").click();//让列表展开
	   var name= $("#theID").html();//当前页面获取页面ID
	   $("li:contains("+name+")").attr("class","current");//让展开为当前选择的接口
		})