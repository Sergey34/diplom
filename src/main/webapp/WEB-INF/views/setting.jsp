<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <h1>Настройки фильтров</h1>
</head>
<style type="text/css">
    input[type="checkbox"] {
        vertical-align: baseline
    }

</style>
<body>

<h1>Welcome : ${pageContext.request.userPrincipal.name} , выберите желаемые фильтры</h1>

<p>выберите дни недели на которые хотите получать расписание фильмов в дом кино:</p>


<form:form method="post" action="add" commandName="setting">
    <form:checkbox path="Monday"/>
    <form:label path="Monday">
        <spring:message code="label.Monday"/>
    </form:label>

    <form:checkbox path="Tuesday"/>
    <form:label path="Tuesday">
        <spring:message code="label.Tuesday"/>
    </form:label>

    <form:checkbox path="Wednesday"/>
    <form:label path="Wednesday">
        <spring:message code="label.Wednesday"/>
    </form:label>

    <form:checkbox path="Thursday"/>
    <form:label path="Thursday">
        <spring:message code="label.Thursday"/>
    </form:label>

    <form:checkbox path="Friday"/>
    <form:label path="Friday">
        <spring:message code="label.Friday"/>
    </form:label>

    <form:checkbox path="Saturday"/>
    <form:label path="Saturday">
        <spring:message code="label.Saturday"/>
    </form:label>

    <form:checkbox path="Sunday"/>
    <form:label path="Sunday">
        <spring:message code="label.Sunday"/>
    </form:label>
    </br>
    <p>выберите группу:</p>
    <form:select path="group">
        <form:option label="выберите группу" value="${null}"/>
        <form:options items="${groupList}"/>
    </form:select>

    <input type="submit"
           value="<spring:message code="label.save"/>"/>


</form:form>




</body>
</html>