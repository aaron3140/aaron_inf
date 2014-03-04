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
	{t:"<li><a href='INF_01_001_test.jsp'>账户信息查询接口测试</a></li>"},
	{t:"<li><a href='INF02002_test.jsp'>客户银行信息查询接口</a></li>"},
	{t:"<li><a href='INF_01_003_test.jsp'>交易接口</a></li>"},
	{t:"<li><a href='INF02001_test.jsp'>交易综合查询接口</a></li>"},
	{t:"<li><a href='INF05001_test.jsp'>签约实时代收付接口</a></li>"},
	{t:"<li><a href='INF05102_test.jsp'>验证实时代收付接口</a></li>"},
	{t:"<li><a href='INF05103_test.jsp'>实时代收付测试接口</a></li>"},
	{t:"<li><a href='INF01011_test.jsp'>签约接口</a></li>"},
	{t:"<li><a href='INF01021_test.jsp'>签约绑定查询接口</a></li>"},
	{t:"<li><a href='INF01014_test.jsp'>实时验证接口</a></li>"},
	{t:"<li><a href='INF01015_test.jsp'>实时冲正接口</a></li>"},
	{t:"<li><a href='INF01013_test.jsp'>实时解签接口</a></li>"},
	{t:"<li><a href='INF02005_test.jsp'>转账收款名单查询接口</a></li>"},
	{t:"<li><a href='INF02006_test.jsp'>付款单查询接口</a></li>"},
	{t:"<li><a href='INF05003_test.jsp'>付款到银行账户接口</a></li>"},
	{t:"<li><a href='INF05004_test.jsp'>付款接口</a></li>"},
	{t:"<li><a href='INF03003_test.jsp'>收款请求接口</a></li>"},
	{t:"<li><a href='INF02014_test.jsp'>交易退款接口</a></li>"}
		]
	},
	
	
	{t:"<ul>IPOS客户端接口</ul>",
		s:[
		{t:"<li><a href='INF01012_test.jsp'>登录验证接口</a></li>"},
		{t:"<li><a href='INF03001_test.jsp'>短信验证码接口</a></li>"},
		{t:"<li><a href='INF03002_test.jsp'>加密随机数下发接口</a></li>"},
		{t:"<li><a href='INF02004_test.jsp'>余额查询接口</a></li>"},
		{t:"<li><a href='INF_01_006_test.jsp'>交易列表查询接口</a></li>"},
		{t:"<li><a href='INF_01_007_test.jsp'>交易明细查询接口</a></li>"},
		{t:"<li><a href='INF_01_004_test.jsp'>账户管理接口</a></li>"},
		{t:"<li><a href='INF02016_test.jsp'>账户交易记录查询</a></li>"},
		{t:"<li><a href='INF02003_test.jsp'>客户端版本管理接口</a></li>"},
		{t:"<li><a href='INF02021_test.jsp'>3G流量卡充值接口</a></li>"},
		{t:"<li><a href='INF02032_test.jsp'>充值转账接口</a></li>"},
		{t:"<li><a href='INF02008_test.jsp'>密码管理接口</a></li>"},
		{t:"<li><a href='INF02010_test.jsp'>Q币充值接口</a></li>"},
		{t:"<li><a href='INF02011_test.jsp'>话费充值接口</a></li>"},
		{t:"<li><a href='INF02012_test.jsp'>酬金结转接口</a></li>"},
		{t:"<li><a href='INF02009_test.jsp'>快捷交易设置接口</a></li>"},
		{t:"<li><a href='INF02013_test.jsp'>快捷交易查询接口</a></li>"},
		{t:"<li><a href='INF02017_test.jsp'>快捷交易关闭接口</a></li>"},
		{t:"<li><a href='INF02018_test.jsp'>电子售卡接口</a></li>"},
		{t:"<li><a href='INF02019_test.jsp'>游戏充值接口</a></li>"},
		{t:"<li><a href='INF06009_test.jsp'>代理商列表查询接口</a></li>"},
		{t:"<li><a href='INF02022_test.jsp'>商户注册接口</a></li>"},
		{t:"<li><a href='INF02031_test.jsp'>短信交易凭证接口</a></li>"},
		{t:"<li><a href='INF02029_test.jsp'>客户信息验证接口</a></li>"},
		{t:"<li><a href='INF02033_test.jsp'>腾讯QQ下单接口</a></li>"},
		{t:"<li><a href='INF02034_test.jsp'>腾讯QQ发货接口</a></li>"},
		{t:"<li><a href='INF02035_test.jsp'>全国固话宽带充值接口</li>"},
		{t:"<li><a href='INF04001_test.jsp'>IVR手机号码验证接口</a></li>"},
		{t:"<li><a href='INF04002_test.jsp'>IVR语音操作查密码验证接口</a></li>"}
			]
		},

		{t:"<ul>EPOS接口</ul>",
			s:[
		{t:"<li><a href='INF06001_test.jsp'>终端签到接口</a></li>"},
		{t:"<li><a href='INF06101_test.jsp'>信用卡还款接口</a></li>"},
		{t:"<li><a href='INF06003_test.jsp'>水电煤账单查询接口</a></li>"},
		{t:"<li><a href='INF06002_test.jsp'>水电煤地市查询接口</a></li>"},
		{t:"<li><a href='INF06102_test.jsp'>水电煤账单缴费接口</a></li>"},
		{t:"<li><a href='INF06004_test.jsp'>充值账户校验接口</a></li>"},
		{t:"<li><a href='INF06103_test.jsp'>个人账户充值接口</a></li>"},
		{t:"<li><a href='INF06005_test.jsp'>个人账户余额查询接口</a></li>"},
		{t:"<li><a href='INF06006_test.jsp'>车船税账单查询接口</a></li>"},
		{t:"<li><a href='INF06104_test.jsp'>车船税账单缴费接口</a></li>"},
		{t:"<li><a href='INF06007_test.jsp'>卡户管理接口</a></li>"},
		{t:"<li><a href='INF06008_test.jsp'>子卡列表查询接口</a></li>"},
		{t:"<li><a href='INF06200_test.jsp'>全国多媒体付款接口</a></li>"},
		{t:"<li><a href='INF02024_test.jsp'>账户绑卡验证接口</a></li>"},
		{t:"<li><a href='INF02030_test.jsp'>账户绑卡通知接口</a></li>"},
		{t:"<li><a href='INF02036_test.jsp'>账户绑卡查询接口</a></li>"}
				]
			},

			{t:"<ul>纯业务接口</ul>",
				s:[
			{t:"<li><a href='INF12034_test.jsp'>QQ发货（纯业务接口）</a></li>"},
			{t:"<li><a href='INF02025_test.jsp'>广州后付费查询接口</a></li>"},
			{t:"<li><a href='INF02026_test.jsp'>广州后付费交易接口</a></li>"},
			{t:"<li><a href='INF02027_test.jsp'>有线电视查询接口</a></li>"},
			{t:"<li><a href='INF02028_test.jsp'>有线电视交易接口</a></li>"},
			{t:"<li><a href='INF12011_test.jsp'>话费充值（纯业务接口）</a></li>"},
			{t:"<li><a href='INF12018_test.jsp'>电子售卡（纯业务接口）</a></li>"},
			{t:"<li><a href='INF12019_test.jsp'>游戏充值（纯业务接口）</a></li>"},
			{t:"<li><a href='INF12021_test.jsp'>3G流量充值（纯业务接口）</a></li>"},
			{t:"<li><a href='INF12038_test.jsp'>预订[火车票]（纯业务接口）</a></a></li>"},
			{t:"<li><a href='INF12039_test.jsp'>出票[火车票]（纯业务接口）</a></li>"}
					]
				},
				
			{t:"<ul>前置交易客户端API</ul>",
				s:[
			{t:"<li><a href='API_01.jsp'>测试页面API_01</a></li>"}
					]
				}

		];
var et=new Tree(data,'esunTree');
//]]>
