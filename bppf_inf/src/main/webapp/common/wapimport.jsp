<%@ page language="java" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>

<%-- jstl --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%-- define --%>
<logic:notPresent name="path" scope="session">
	<c:set var="path" scope="session">
		<%=request.getContextPath()%>
	</c:set>
	<c:set var="mtp_path" scope="session">${sessionScope.path}/httppost/MTPayment</c:set>
	<c:set var="spp_path" scope="session">${sessionScope.path}/httppost/SPPayment</c:set>
</logic:notPresent>
