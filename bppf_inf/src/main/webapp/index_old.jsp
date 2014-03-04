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
		<table align="center" border="1">

			<tr>
				<td>
					<a href="test/INF_01_001_test.jsp">账户信息查询接口测试</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF02002_test.jsp">客户银行信息查询接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF02004_test.jsp">账户余额查询接口测试</a>
				</td>
			</tr>

			<%--			<tr>--%>
			<%--				<td>--%>
			<%--					<a href="test/INF_01_002_test.jsp">交易查询接口测试</a>--%>
			<%--				</td>--%>
			<%--			</tr>--%>
			<tr>
				<td>
					-----------------------------
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF_01_003_test.jsp">交易</a>
				</td>
			</tr>

			<tr>
				<td>
					<a href="test/INF_01_004_test.jsp">账户管理接口测试</a>
				</td>
			</tr>
			<tr>
				<td>
					-----------------------------
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF_01_006_test.jsp">新交易列表查询接口测试</a>
				</td>
			</tr>

			<tr>
				<td>
					<a href="test/INF_01_007_test.jsp">新交易明细查询接口测试</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF02001_test.jsp">交易综合查询接口</a>
				</td>
			</tr>
			<tr>
				<td>
					-----------------------------
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF05001_test.jsp">签约实时代收付接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF05102_test.jsp">验证实时代收付接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF05103_test.jsp">实时代收付接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF01011_test.jsp">签约接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF01021_test.jsp">签约绑定查询接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF01013_test.jsp">实时解签接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF01014_test.jsp">实时验证接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF01015_test.jsp">实时冲正接口</a>
				</td>
			</tr>
			<tr>
				<td>
					-----------------------------
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF03001_test.jsp">短信下发接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF03002_test.jsp">加密随机数下发接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF01012_test.jsp">鉴权验证接口</a>
				</td>
			</tr>

			<tr>
				<td>
					<a href="test/INF02003_test.jsp">客户端版本管理接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF02005_test.jsp">转账收款名单查询接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF02006_test.jsp">付款单查询接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF05003_test.jsp">付款到银行账户接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF05004_test.jsp">付款接口</a>
				</td>
			</tr>
			<tr>
				<td>
					-----------------------------
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF06001_test.jsp">终端签到接口</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="test/INF06101_test.jsp">信用卡还款接口</a>
				</td>
			</tr>
		</table>
	</body>
</html>
