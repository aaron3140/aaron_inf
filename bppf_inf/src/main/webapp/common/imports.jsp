<%@ page language="java" pageEncoding="GBK"%>
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
</logic:notPresent>

<script type="text/javascript">
	//window.location.href = "${path}" + "/index.jsp";
</script>
