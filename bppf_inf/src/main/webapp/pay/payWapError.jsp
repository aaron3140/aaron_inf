<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/imports.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<SCRIPT LANGUAGE="JavaScript">
	
	</SCRIPT>

		<style type="text/css">
	    .ordermsg_top{
			height:82px;
			width:265px;
			background:url(images/rs_error_top.jpg) right top no-repeat;
			overflow:hidden;
			clear:both;
			font-size:13px;
		}
		.ordermsg_middle{
			background:url(images/zf_k_mid.jpg) right top repeat-y;
			overflow:hidden;
			width:265px;
			clear:both;
			color:#5D5D5D;
		}
		.ordermsg_bottom{
			height:68px;
			width:265px;
			background:url(images/zf_k_btm.jpg) right top no-repeat;
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
		.main_table{
		    valign:middle;
		    margin-top:auto;
		    margin-bottom:auto;
		}
		body {
		
			width:800px;
			margin:0 auto;
			text-align:center;
		}
	</style>

	</head>

	<body>
		<table style="height:100%">
			<tr>
				<td>
					<table class="main_table">

						<tr valign="top">

							<td>

								<div class="ordermsg_top" style="float: left;"></div>

								<div class="ordermsg_middle">

									<table class="ordermsg_table">
										<tr>
											<td align="right">
												错误信息：
											</td>
											<td align="left">
												${exceptions }
											</td>
										</tr>
									</table>
								</div>
								<div class="ordermsg_bottom"></div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
