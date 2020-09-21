<!DOCTYPE html>
<html lang="en" xmlns:th="htttp://thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>All ships</title>
</head>
<body>

<div th:each="ship : ${ships}">
    <p th:text="${ship.getId()}">INFO</p>
</div>


</body>
</html>