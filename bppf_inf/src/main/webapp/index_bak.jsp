<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>前置INF接口测试首页</title>
<link href="css/count.css" rel="stylesheet" type="text/css" />
<script src="js/jquery-1.7.1.min.js"></script>
</head>
<body>
	<div class="wrap">
        <div class="borHead helpHead" style="font-size:15px;font-weight:bold;color:#007CE6; text-align: center">INF前置测试页面</div>
        <div class="borMain countMain">
        	<div class="countcont">
            	<div id="esunTree" class="tree sidebar" ></div>
                <div class="main">
                    <div class="tipsHead">新版测试页面特点：</div>
                        <ul class="tipsInfo">
                            <li>
                                      1. 新增测试接口步骤：
                            <ul class="tipsInfo">
                            <li>(1)  先把新增的接口页面的链接地址复制到index.jsp中，注意链接格式如：test/INF_01_001_test.jsp
                            			（新增地址按照页面原有例子格式作为叶子节点插入到所属接口的树中）。
                            </li>
                            <br>
                            <li>(2)  同理也将链接地址复制到common目录下的hdc-tree.js中,注意链接地址格式如：INF_01_001_test.jsp。</li>
                            <br>
                            <li>(3)  页面头部需引进3个外部链接，1.jquery插件;  2.公共样式count.css;  3.页面控制脚本xxx-deal.js。
                            		 注意：当所属分类为企业账户接口其为account-deal.js； IPOS客户端接口为ipos-deal.js；
                            		 EPOS接口为epos-deal.js； 纯业务接口为pure-deal.js。
                            </li>
                            <br>
                            <li>(4)  在body外引入接口树hdc-tree.js，通过在html中插入“ < div id="esunTree" class="tree sidebar" > < /div >”响应hdc-tree.js从而生成接口树。</li>
                            <br>
                           <li>(5)  页面所有元素用li标签进行封装，字段名用label标签封装，字段说明用p标签封装。</li>
                            </ul>
                            </li>
                        </ul>
                    </div>
                     <!-- main end -->
                 </div>
               
            </div>
        </div>
</body>
<script type="text/javascript">
	try{document.execCommand("BackgroundImageCache",false,true)}catch(e){};
			function Tree(data, el) {
				this.app=function(par,tag){return par.appendChild(document.createElement(tag))};
				this.create(document.getElementById(el),data)
			};
	Tree.fn = Tree.prototype = {
			create: function (par,group){
	var host=this, length = group.length;
	for (var i = 0; i < length; i++) {
	var dl =this.app(par,'DL'), dt = this.app(dl,'DT'), dd = this.app(dl,'DD');
	dt.innerHTML=group[i]['t'];
	if (!group[i]['s'])continue;
	dt.group=group[i]['s'];
	dt.className+=" node-close";
	dt.onclick=function (){
	var dd= this.nextSibling;
	if (!dd.hasChildNodes()){
		host.create(dd,this.group);
		this.className='node-open'
	}else{
		var set=dd.style.display=='none'?['','node-open']:['none','node-close'];
		dd.style.display=set[0];
		this.className=set[1]
		}
						}				
				}
			}					
								};
var data=[
		
	{t:"<ul>企业账户接口</ul>",
	s:[
	{t:"<li><a href='test/INF_01_001_test.jsp'>账户信息查询接口测试</a></li>"},
	{t:"<li><a href='test/INF02002_test.jsp'>客户银行信息查询接口</a></li>"},
	{t:"<li><a href='test/INF_01_003_test.jsp'>交易接口</a></li>"},
	{t:"<li><a href='test/INF02001_test.jsp'>交易综合查询接口</a></li>"},
	{t:"<li><a href='test/INF05001_test.jsp'>签约实时代收付接口</a></li>"},
	{t:"<li><a href='test/INF05102_test.jsp'>验证实时代收付接口</a></li>"},
	{t:"<li><a href='test/INF05103_test.jsp'>实时代收付测试接口</a></li>"},
	{t:"<li><a href='test/INF01011_test.jsp'>签约接口</a></li>"},
	{t:"<li><a href='test/INF01021_test.jsp'>签约绑定查询接口</a></li>"},
	{t:"<li><a href='test/INF01014_test.jsp'>实时验证接口</a></li>"},
	{t:"<li><a href='test/INF01015_test.jsp'>实时冲正接口</a></li>"},
	{t:"<li><a href='test/INF01013_test.jsp'>实时解签接口</a></li>"},
	{t:"<li><a href='test/INF02005_test.jsp'>转账收款名单查询接口</a></li>"},
	{t:"<li><a href='test/INF02006_test.jsp'>付款单查询接口</a></li>"},
	{t:"<li><a href='test/INF05003_test.jsp'>付款到银行账户接口</a></li>"},
	{t:"<li><a href='test/INF05004_test.jsp'>付款接口</a></li>"},
	{t:"<li><a href='test/INF03003_test.jsp'>收款请求接口</a></li>"},
	{t:"<li><a href='test/INF02014_test.jsp'>交易退款接口</a></li>"}
		]
	},

	{t:"<ul>IPOS客户端接口</ul>",
		s:[
		{t:"<li><a href='test/INF01012_test.jsp'>登录验证接口</a></li>"},
		{t:"<li><a href='test/INF03001_test.jsp'>短信验证码接口</a></li>"},
		{t:"<li><a href='test/INF03002_test.jsp'>加密随机数下发接口</a></li>"},
		{t:"<li><a href='test/INF02004_test.jsp'>余额查询接口</a></li>"},
		{t:"<li><a href='test/INF_01_006_test.jsp'>交易列表查询接口</a></li>"},
		{t:"<li><a href='test/INF_01_007_test.jsp'>交易明细查询接口</a></li>"},
		{t:"<li><a href='test/INF_01_004_test.jsp'>账户管理接口</a></li>"},
		{t:"<li><a href='test/INF02016_test.jsp'>账户交易记录查询</a></li>"},
		{t:"<li><a href='test/INF02003_test.jsp'>客户端版本管理接口</a></li>"},
		{t:"<li><a href='test/INF02021_test.jsp'>3G流量卡充值接口</a></li>"},
		{t:"<li><a href='test/INF02032_test.jsp'>充值转账接口</a></li>"},
		{t:"<li><a href='test/INF02008_test.jsp'>密码管理接口</a></li>"},
		{t:"<li><a href='test/INF02010_test.jsp'>Q币充值接口</a></li>"},
		{t:"<li><a href='test/INF02011_test.jsp'>话费充值接口</a></li>"},
		{t:"<li><a href='test/INF02012_test.jsp'>酬金结转接口</a></li>"},
		{t:"<li><a href='test/INF02009_test.jsp'>快捷交易设置接口</a></li>"},
		{t:"<li><a href='test/INF02013_test.jsp'>快捷交易查询接口</a></li>"},
		{t:"<li><a href='test/INF02017_test.jsp'>快捷交易关闭接口</a></li>"},
		{t:"<li><a href='test/INF02018_test.jsp'>电子售卡接口</a></li>"},
		{t:"<li><a href='test/INF02019_test.jsp'>游戏充值接口</a></li>"},
		{t:"<li><a href='test/INF06009_test.jsp'>代理商列表查询接口</a></li>"},
		{t:"<li><a href='test/INF02022_test.jsp'>商户注册接口</a></li>"},
		{t:"<li><a href='test/INF02031_test.jsp'>短信交易凭证接口</a></li>"},
		{t:"<li><a href='test/INF02029_test.jsp'>客户信息验证接口</a></li>"},
		{t:"<li><a href='test/INF02033_test.jsp'>腾讯QQ下单接口</a></li>"},
		{t:"<li><a href='test/INF02034_test.jsp'>腾讯QQ发货接口</a></li>"},
		{t:"<li><a href='test/INF02035_test.jsp'>全国固话宽带充值接口</li>"},
		{t:"<li><a href='test/INF04001_test.jsp'>IVR手机号码验证接口</a></li>"},
		{t:"<li><a href='test/INF04002_test.jsp'>IVR语音操作查密码验证接口</a></li>"}
			]
		},

		{t:"<ul>EPOS接口</ul>",
			s:[
		{t:"<li><a href='test/INF06001_test.jsp'>终端签到接口</a></li>"},
		{t:"<li><a href='test/INF06101_test.jsp'>信用卡还款接口</a></li>"},
		{t:"<li><a href='test/INF06003_test.jsp'>水电煤账单查询接口</a></li>"},
		{t:"<li><a href='test/INF06002_test.jsp'>水电煤地市查询接口</a></li>"},
		{t:"<li><a href='test/INF06102_test.jsp'>水电煤账单缴费接口</a></li>"},
		{t:"<li><a href='test/INF06004_test.jsp'>充值账户校验接口</a></li>"},
		{t:"<li><a href='test/INF06103_test.jsp'>个人账户充值接口</a></li>"},
		{t:"<li><a href='test/INF06005_test.jsp'>个人账户余额查询接口</a></li>"},
		{t:"<li><a href='test/INF06006_test.jsp'>车船税账单查询接口</a></li>"},
		{t:"<li><a href='test/INF06104_test.jsp'>车船税账单缴费接口</a></li>"},
		{t:"<li><a href='test/INF06007_test.jsp'>卡户管理接口</a></li>"},
		{t:"<li><a href='test/INF06008_test.jsp'>子卡列表查询接口</a></li>"},
		{t:"<li><a href='test/INF06200_test.jsp'>全国多媒体付款接口</a></li>"},
		{t:"<li><a href='test/INF02024_test.jsp'>账户绑卡验证接口</a></li>"},
		{t:"<li><a href='test/INF02030_test.jsp'>账户绑卡通知接口</a></li>"},
		{t:"<li><a href='test/INF02036_test.jsp'>账户绑卡查询接口</a></li>"}
				]
			},

			{t:"<ul>纯业务接口</ul>",
				s:[
			{t:"<li><a href='test/INF12034_test.jsp'>QQ发货（纯业务接口）</a></li>"},
			{t:"<li><a href='test/INF02025_test.jsp'>广州后付费查询接口</a></li>"},
			{t:"<li><a href='test/INF02026_test.jsp'>广州后付费交易接口</a></li>"},
			{t:"<li><a href='test/INF02027_test.jsp'>有线电视查询接口</a></li>"},
			{t:"<li><a href='test/INF02028_test.jsp'>有线电视交易接口</a></li>"},
			{t:"<li><a href='test/INF12011_test.jsp'>话费充值（纯业务接口）</a></li>"},
			{t:"<li><a href='test/INF12018_test.jsp'>电子售卡（纯业务接口）</a></li>"},
			{t:"<li><a href='test/INF12019_test.jsp'>游戏充值（纯业务接口）</a></li>"},
			{t:"<li><a href='test/INF12021_test.jsp'>3G流量充值（纯业务接口）</a></li>"},
			{t:"<li><a href='test/INF12038_test.jsp'>预订[火车票]（纯业务接口）</a></a></li>"},
			{t:"<li><a href='test/INF12039_test.jsp'>出票[火车票]（纯业务接口）</a></li>"}
					]
				},

			{t:"<ul>前置交易客户端API</ul>",
				s:[
			{t:"<li><a href='test/API_01.jsp'>测试页面API1</a></li>"}
					]
				}

		];
var et=new Tree(data,'esunTree');
//]]>
</script>
</html>