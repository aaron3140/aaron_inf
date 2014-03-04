//所有企业账户接口的公共js
$(document).ready(function(){
	   $("dt:contains('企业账户接口')").click();//让列表展开
	   var name= $("#theID").html();
	   $("li:contains("+name+")").attr("class","current");//让展开为当前选择的接口
		})