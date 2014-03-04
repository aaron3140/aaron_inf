<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<style type="text/css">
.tab {
	margin: 0;
	padding: 0; /*合并边线*/
	border-collapse: collapse;
}

.tab td {
	border: solid 1px #000
}
</style>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<title>INF测试</title>
	</head>

	<body>
		<h3 align="center">前置INF测试列表</h3>
		<table align="center" border="0">
		<tr>
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;企业账户接口<br/>
					<ul>
						<li><a href="test/INF_01_001_test.jsp">账户信息查询接口测试</a></li>
						<li><a href="test/INF02002_test.jsp">客户银行信息查询接口</a></li>
						<li><a href="test/INF_01_003_test.jsp">交易</a></li>
						<li><a href="test/INF02001_test.jsp">交易综合查询接口</a></li>
						<li><a href="test/INF05001_test.jsp">签约实时代收付接口</a></li>
						<li><a href="test/INF05102_test.jsp">验证实时代收付接口</a></li>
						<li><a href="test/INF05103_test.jsp">实时代收付接口</a></li>
						<li><a href="test/INF01011_test.jsp">签约接口</a></li>
						<li><a href="test/INF01021_test.jsp">签约绑定查询接口</a></li>
						<li><a href="test/INF01014_test.jsp">实时验证接口</a></li>
						<li><a href="test/INF01015_test.jsp">实时冲正接口</a></li>
						<li><a href="test/INF01013_test.jsp">实时解签接口</a></li>
						<li><a href="test/INF02005_test.jsp">转账收款名单查询接口</a></li>
						<li><a href="test/INF02006_test.jsp">付款单查询接口</a></li>
						<li><a href="test/INF05003_test.jsp">付款到银行账户接口</a></li>
						<li><a href="test/INF05004_test.jsp">付款接口</a></li>
						<li><a href="test/INF03003_test.jsp">收款请求接口</a></li>
						<li><a href="test/INF02014_test.jsp">交易退款接口</a></li>
					</ul>
			
			</td>
		
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IPOS客户端接口<br/>
			       <ul>
						<li><a href="test/INF01012_test.jsp">登录验证接口</a></li>
						<li><a href="test/INF03001_test.jsp">短信验证码接口</a></li>
						<li><a href="test/INF03002_test.jsp">加密随机数下发接口</a></li>
						</ul>
					 <ul>
						<li><a href="test/INF02004_test.jsp">余额查询接口</a></li>
						<li><a href="test/INF_01_006_test.jsp">交易列表查询接口</a></li>
						<li><a href="test/INF_01_007_test.jsp">交易明细查询接口</a></li>
						<li><a href="test/INF_01_004_test.jsp">账户管理接口</a></li>
						<li><a href="test/INF02016_test.jsp">账户交易记录查询</a></li>
						<li><a href="test/INF02003_test.jsp">客户端版本管理接口</a></li>
						<li><a href="test/INF02021_test.jsp">3G流量卡充值接口</a></li>
						<li><a href="test/INF02032_test.jsp">充值转账接口</a></li>
						<li><a href="test/INF02033_test.jsp">腾讯QQ下单接口</a></li>
						<li><a href="test/INF02034_test.jsp">腾讯QQ发货接口</a></li>
						<li><a href="test/INF05104_test.jsp">分账方案管理接口</a></li>
						<li><a href="test/INF05105_test.jsp">分账方案查询接口</a></li>
					</ul>
					<ul>
						<li><a href="test/INF02008_test.jsp">密码管理接口</a></li>
						<li><a href="test/INF02010_test.jsp">Q币充值接口</a></li>
						<li><a href="test/INF02011_test.jsp">话费充值接口</a></li>
						<li><a href="test/INF02012_test.jsp">酬金结转接口</a></li>
						<li><a href="test/INF02009_test.jsp">快捷交易设置接口</a></li>
						<li><a href="test/INF02013_test.jsp">快捷交易查询接口</a></li>
						<li><a href="test/INF02017_test.jsp">快捷交易关闭接口</a></li>
						<li><a href="test/INF02018_test.jsp">电子售卡接口</a></li>
						<li><a href="test/INF02019_test.jsp">游戏充值接口</a></li>
						<li><a href="test/INF06009_test.jsp">代理商列表查询接口</a></li>
						<li><a href="test/INF02022_test.jsp">商户注册接口</a></li>
						<li><a href="test/INF02031_test.jsp">短信交易凭证接口</a></li>
						<li><a href="test/INF02029_test.jsp">客户信息验证接口</a></li>
						<li><a href="test/INF02035_test.jsp">全国固话宽带充值接口</a></li>
						<li><a href="test/INF02040_test.jsp">消息未阅条数查询接口</a></li>
						<li><a href="test/INF02041_test.jsp">消息管理接口</a></li>
						<li><a href="test/INF02042_test.jsp">消息列表查询接口</a></li>
						<li><a href="test/INF02043_test.jsp">消息详情查询接口</a></li>
						<li><a href="test/INF02044_test.jsp">产品溢价查询接口</a></li>
						<li><a href="test/INF05002_test.jsp">支付插件交易接口</a></li>
						
					</ul>
					
					<ul>
						<li><a href="test/INF04001_test.jsp">IVR 手机号码验证接口</a></li>
						<li><a href="test/INF04002_test.jsp">IVR 语音操作、查询密码验证接口</a></li>
					</ul>
					
					<ul>
						<li><a href="test/INF02045_test.jsp">理财开户接口</a></li>
						<li><a href="test/INF02046_test.jsp">理财申购接口</a></li>
						<li><a href="test/INF02047_test.jsp">理财申购支付接口</a></li>
						<li><a href="test/INF02048_test.jsp">理财赎回接口</a></li>
						<li><a href="test/INF02049_test.jsp">理财明细查询接口</a></li>
						<li><a href="test/INF02050_test.jsp">理财余额查询接口</a></li>
						<li><a href="test/INF02051_test.jsp">理财历史利率查询接口</a></li>
					</ul>
					
			</td>
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;EPOS接口<br/>
					<ul>
						<li><a href="test/INF06001_test.jsp">终端签到接口</a></li>
						<li><a href="test/INF06101_test.jsp">信用卡还款接口</a></li>
						<li><a href="test/INF06003_test.jsp">水电煤账单查询接口</a></li>
						<li><a href="test/INF06002_test.jsp">水电煤地市查询接口</a></li>
						<li><a href="test/INF06102_test.jsp">水电煤账单缴费接口</a></li>
						<li><a href="test/INF06004_test.jsp">充值账户校验接口</a></li>
						<li><a href="test/INF06103_test.jsp">个人账户充值接口</a></li>
						<li><a href="test/INF06005_test.jsp">个人账户余额查询接口</a></li>
						<li><a href="test/INF06006_test.jsp">车船税账单查询接口</a></li>
						<li><a href="test/INF06104_test.jsp">车船税账单缴费接口</a></li>
						<li><a href="test/INF06007_test.jsp">卡户管理接口</a></li>
						<li><a href="test/INF06008_test.jsp">子卡列表查询接口</a></li>
						<li>-----------------</li>
						<li><a href="test/INF06200_test.jsp">全国多媒体付款接口</a></li>
						<li><a href="test/INF06201_test.jsp">[东莞公交]签到接口</a></li>
						<li><a href="test/INF06202_test.jsp">[东莞公交]签退接口</a></li>
						<li><a href="test/INF06203_test.jsp">[东莞公交]卡操作接口</a></li>
						<li><a href="test/INF06204_test.jsp">[东莞公交]开卡/开卡冲正接口</a></li>
						<li><a href="test/INF06205_test.jsp">[东莞公交]充值/充值冲正接口</a></li>
						<li><a href="test/INF02024_test.jsp">账户绑卡验证接口</a></li>
						<li><a href="test/INF02030_test.jsp">账户绑卡通知接口</a></li>
						<li><a href="test/INF02036_test.jsp">账户绑卡查询接口</a></li>
					</ul>
			</td>
			
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;纯业务接口<br/>
					<ul>
						<li><a href="test/INF12034_test.jsp">QQ下单/发货（纯业务接口）</a></li>
						<li><a href="test/INF02025_test.jsp">广州后付费查询接口</a></li>
						<li><a href="test/INF02026_test.jsp">广州后付费交易接口</a></li>
						<li><a href="test/INF02027_test.jsp">有线电视查询接口</a></li>
						<li><a href="test/INF02028_test.jsp">有线电视交易接口</a></li>
						<li><a href="test/INF12011_test.jsp">话费充值（纯业务接口）</a></li>
						<li><a href="test/INF12018_test.jsp">电子售卡（纯业务接口）</a></li>
						<li><a href="test/INF12019_test.jsp">游戏充值（纯业务接口）</a></li>
						<li><a href="test/INF12021_test.jsp">3G流量充值（纯业务接口）</a></li>
						<li><a href="test/INF12035_test.jsp">车次查询</a></li>
						<li><a href="test/INF12036_test.jsp">余座查询</a></li>
						<li><a href="test/INF12037_test.jsp">订票查询</a></li>
						<li><a href="test/INF12038_test.jsp">预订[火车票]</a></li>
						<li><a href="test/INF12039_test.jsp">出票[火车票]</a></li>
					</ul>
			</td>
			
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运营调用接口<br/>
					<ul>
						<li><a href="test/INF00001_test.jsp">交易回调接口</a></li>
						<li><a href="test/INF13001_test.jsp">客户端交易统计明细接口</a></li>
						<li><a href="test/INF13002_test.jsp">客户端排行榜排名查询</a></li>
						<li><a href="test/INF13003_test.jsp">客户端排行榜明细信息</a></li>
					</ul>
			</td>
			
		</tr>
		</table>
	</body>
</html>
