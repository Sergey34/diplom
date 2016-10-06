<%@ page language="java" contentType="text/html; charset=utf8"
		 pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8">
	<title><spring:message code="label.title" /></title>

	<style>
		.error {
			padding: 15px;
			margin-bottom: 20px;
			border: 1px solid transparent;
			border-radius: 4px;
			color: #a94442;
			background-color: #f2dede;
			border-color: #ebccd1;
		}

		.msg {
			padding: 15px;
			margin-bottom: 20px;
			border: 1px solid transparent;
			border-radius: 4px;
			color: #31708f;
			background-color: #d9edf7;
			border-color: #bce8f1;
		}

		#login-box {
			width: 300px;
			padding: 20px;
			margin: 100px auto;
			background: #fff;
			-webkit-border-radius: 2px;
			-moz-border-radius: 2px;
			border: 1px solid #000;
		}
	</style>
</head>
<body>

<c:if test="${not empty param.error}">
	<font color="red"> <spring:message code="label.loginerror" />
		: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} </font>
</c:if>

<div id="login-box">

	<h2>Login with Username and Password</h2>

	<c:if test="${not empty error}">
		<div class="error">${error}</div>
	</c:if>
	<c:if test="${not empty msg}">
		<div class="msg">${msg}</div>
	</c:if>

	<form method="POST" action="<c:url value="/j_spring_security_check" />">
	<table>
		<tr>
			<td align="right"><spring:message code="label.login" /></td>
			<td><input type="text" name="j_username" /></td>
		</tr>
		<tr>
			<td align="right"><spring:message code="label.password" /></td>
			<td><input type="password" name="j_password" /></td>
		</tr>
		<tr>
			<td align="right"><spring:message code="label.remember" /></td>
			<td><input type="checkbox" name="_spring_security_remember_me" /></td>
		</tr>
		<tr>
			<td colspan="2" align="right"><input type="submit" value=<spring:message code="label.sign_in" /> />
				<input type="reset" value=<spring:message code="label.clean" /> /></td>
		</tr>
	</table>
</form>
</div>

</body>
</html>