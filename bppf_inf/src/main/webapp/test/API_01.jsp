<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
	<link href="../css/count.css" rel="stylesheet" type="text/css" />
	<script src="../js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="../common/api-deal.js"></script><!-- 跳转处理 -->
	<script type="text/javascript">
		$(document).ready(function(){
	//判断提交方法
			onMethodChange();
			

	//POST中添加元素按钮
			$("#postAddMore").click(function(){
				var fatherUL=$("#postAddMore").parent().parent();
				fatherUL.after("<ul><li><input  type='text' class='txt theName'></li><li><input  type='text' class='txt theValue'></li> <li ><input  type='button' value='删除' onclick='cancelThis(this)' ></li></ul>");
				});

			
			

	//提交方式
			$("#buttonSub").click(function(){
				//请求地址
				var actionSTR=$("#URLaddress").val();
				//请求方法
				var theMethod=$("#method").val();
				if("post"==theMethod){
				//POST提交
				
				//获取页面信息
				var theNames=$(".theName");
				var theValues=$(".theValue");
				//创建数据对象保存键值对
				var nameValues = new Array();
				//封装数据数据形成key-value形式
				 for(var i = 0; i < theNames.length; i ++){
						var inputName= theNames[i].value;
						var inputValue= theValues[i].value;
						if(inputValue!=""&&inputName!=""){//过滤添加的单元格但没有填写的
							var NV="\""+inputName+"\""+":"+"\""+inputValue+"\"";
							nameValues.push(NV);
						}
					 }
				//变为json格式
				var thejson="{"+nameValues+"}";
				alert(thejson);
				//alert(JSON.stringify(thejson));

				$.ajax({
					   type: "post",
					   url: actionSTR,
					   crossDomain : "ture",
					   dataType:"json",      
			            contentType:"application/json",         
					   data: thejson, 
					   success: function(msg){
							alert("POST发送成功");
					       // alert(msg);
							$("#reDate").html(msg);
					   },
					   error: function (xml) { 
						   alert("POST访问失败");
								//alert(xml);
						 } 
					});
				
				  //$.post(
					//	  actionSTR,
					//	  JSON.stringify(thejson),
				 // function(data,status){
				  //  alert("Data: " + data);
				 //  $("#reDate").html(data);
				//  }
			//			  );

				}else{
				  //GET提交
					 //$.getJSON(actionSTR,function(msg){
					//	 alert("请求发送成功");
					    //alert(msg);
					//  });

					$.ajax({
						   type: "GET",
						   url: actionSTR,
						   jsonp: 'callback', //放一个参数在链接地址上，名字叫callback
					       dataType:"jsonp",      
					       Accept:"application/json",         
				           //contentType:"application/json",         
						   //data: JSON.stringify(saveData), 
						   success: function(msg){
						         alert(msg);
							    alert("GET发送成功");
						   },
						   error: function (xml) { 
							   alert("GET访问失败");
								//alert(xml);
						 } 
						});

					

					 
				}
				});

			
			
			});

//方法选择
		function onMethodChange(){
			var method = $("#methodSelect").find("option:selected").val();
			var postClass = $(".post");
			var getClass = $(".get");
			if("post"==method){
				postClass.show();
				getClass.hide();
				$("#method").val("post");
				$("#URLaddress").val("http://192.168.95.202:8080/inf-util/inf/user");
			}else{
				postClass.hide();
				getClass.show();
				$("#method").val("get");
				$("#URLaddress").val("http://192.168.95.202:8080/inf-util/inf/user/getUser");
			}
		}

//POST删除元素函数
		function cancelThis(obj){
		var cancelFatherUL	=$(obj).parent().parent();
		cancelFatherUL.remove();//删除元素
		}

//GET中添加元素按钮
		function getAdd(obj){
			//url为空不触发添加事件
			if($("#URLaddress").val()==""){
					alert("请先输入url地址");
					return;
			}
			//当前值为空不触发添加事件
			if($(obj).val()==""){
				return;
				}
			var fatherUL= $(obj).parent().parent();
			fatherUL.after("<ul><li><input  type='text' class='txt' onblur='getAdd(this)'></li><li><input  type='text' onblur='addPamToUrl(this)' class='txt'></li> <li ><input  type='button' value='删除' onclick='cancelThisGet(this)' ></li></ul>");
			obj.onblur = null;
			}

//GET中组装URL
		function addPamToUrl(obj){
			//得到键值对
			var value=$(obj).val();
			var key=$(obj).parent().prev().find("input").val();//由值找到键
			//组装
			var URLaddress=$("#URLaddress").val();
			//url为空不触发组装事件
			if(URLaddress==""){
					//alert("请先输入url地址");
					return;
			}
			var newURL="";//新url地址
		if(value!=""&&key!=""){//键值对非空才组装
				
			if (URLaddress.indexOf("?")>0){//原本地址包含问号
				newURL=URLaddress+"&"+key+"="+value;
			}else{
				newURL=URLaddress+"?"+key+"="+value;
			}
			$("#URLaddress").attr("value","");//清空原值
			$("#URLaddress").val(newURL);//赋上新值
			obj.onblur = null;
		}

		}


//GET中去除键值对并删除元素
		function cancelThisGet(obj){
			//删除元素
			var cancelFatherUL	=$(obj).parent().parent();
			cancelFatherUL.remove();
			
			//去除url键值对
			var URLaddress=$("#URLaddress").val();
			var key=$(obj).parent().prev().prev().find("input").val();
			var value=$(obj).parent().prev().find("input").val();
			if (URLaddress.indexOf("?")>0&&URLaddress.indexOf("&")>0){//原本地址包含问号以及包含&

				var beforKey=URLaddress.split(key)[0];//key前一段字符串
				if(beforKey.indexOf("&")>0){//前一段字符包含&说明他是非首个键值对
					URLaddress = URLaddress.replace("&"+key+"="+value, "");
					$("#URLaddress").attr("value","");//清空原值
					$("#URLaddress").val(URLaddress);
				}else{///前一段字符不包含&说明他是首个键值对
					URLaddress = URLaddress.replace(key+"="+value+"&", "");//保留问号删除后面的&
					$("#URLaddress").attr("value","");//清空原值
					$("#URLaddress").val(URLaddress);
					}
				
			}else{//只有1个或者0个键值对
				URLaddress = URLaddress.replace("?"+key+"="+value, "");
				$("#URLaddress").attr("value","");//清空原值
				$("#URLaddress").val(URLaddress);
			}
			
			}

	</script>
	</head>
	
	<body>
	<div class="wrap">
        <div class="borHead helpHead" style="font-size:15px;font-weight:bold;color:#007CE6; text-align: center">INF前置测试页面</div>
        <div class="borMain countMain">
        	<div class="countcont">
            	<div id="esunTree" class="tree sidebar" ></div>
                    <div class="main">
                    
                    <div class="msg">
                    <div class="head"><h4 id="theID">测试页面API_01</h4></div><!-- 写ID用于页面跳转后控制展开并显示当前接口 -->
                      <form name="APIfrom"  id="APIfrom"  >
							<input type="hidden" name="method" id="method">
                        <ul class="box" style="border: 1px solid rgb(213, 213, 213);">
                        	<li><label>URL地址：</label>
                        	<input name="URLaddress" id="URLaddress"  type="text" size="100" maxlength="100" class="txt" style="width: 450px;" >
                        	</li>
                        	<li><label>提交方式：</label>
                        	<select name="methodSelect" id="methodSelect" onchange="onMethodChange()">
								<option value="post" selected="selected">post</option>
								<option value="get">get</option>
							</select>
                        	</li>
                       		
                        </ul>
                       
                   
                   <div class="charge post">
                    	<h4>POST参数</h4>
                    	<ul>
                           <li><p class="finish">Name</p></li>
                           <li><p class="finish">Value</p></li>
                           <li><input  type="button"  id="postAddMore"  value="添加" ></li>
                        </ul>
                        
                        <ul>
                           <li ><input  type="text" class="txt theName"></li>
                           <li ><input  type="text" class="txt theValue"></li>
                           <li ><input  type="button" value="删除"  onclick="cancelThis(this)"></li>
                        </ul>
                    </div>
                    
                    <div class="charge get">
                    	<h4>GET参数</h4>
                    	
                    	<ul>
                           <li><p class="finish">Name</p></li>
                           <li><p class="finish">Value</p></li>
                        </ul>
                    	
                        <ul>
                           <li ><input  type="text" class="txt" onblur="getAdd(this)"></li>
                           <li ><input  type="text" class="txt" onblur="addPamToUrl(this)"></li>
                           <li ><input  type='button' value='删除' onclick='cancelThisGet(this)' ></li>
                        </ul>
                    </div>
                    <div class="btn"><input name="buttonSub" id="buttonSub" value="提交"   type="button" class="blueBtnSub" /></div>
                    <div class="tipsHead">数据返回处：</div>
                        <ul class="tipsInfo">
                            <li id="reDate"></li>
                        </ul>
                     </form>
                     </div>
                    </div>
                    </div>
            </div>
        </div>
		</body>
	<script type="text/javascript" src="../common/hdc-tree.js"></script>
</html>
