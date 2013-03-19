<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>

<html>
<head>
<link type="text/css" rel="stylesheet" href="../WTM3_05.css" />
<script language="JavaScript" type="text/javascript"
	src="../jquery_min.js"></script>
<script language="JavaScript" type="text/javascript"
	src="../jquery_ui.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />
<script language="JavaScript" type="text/javascript" src="../WTM3_05.js"></script>
</head>

<body>
	<div id="mainContent">
		<div class="heading">Web Todo List Manager - 3.05</div>
		<div class="loginPanel" id="loginDiv">
					<p style="margin: 30px;font-family: cursive;">Login to wtm ... </p>
			<div>
				<span><input id="username" type="text"
					placeholder="user name"></span> <span><input id="password"
					type="password" placeholder="password"><p class="red"></p></span>
			</div>
			<div class="clsBtns" style="left: 50px;">
				<span style="display: inline-block; margin-right: 7px;" class="btns"
					onclick="validateLogin()">Login</span> <span
					style="display: inline-block;" class="btns" onclick="showRegister()">Register</span>
			</div>
		</div>
		<div class="loginPanel" id="regDiv" style="display: none;">
			<p style="margin: 30px;font-family: cursive;">Register now ... </p>
			<div>
				<span><input id="r_username" type="text"
					placeholder="user name"></span> <span><input id="r_password"
					type="password" placeholder="password"><p class="red"></p> </span>
			</div>
			<div class="clsBtns" style="left: 50px;">
				<span style="display: inline-block; margin-right: 7px;" class="btns"
					onclick="registerUser()">Go!</span>
					<span style="display: inline-block; margin-right: 7px;" class="btns"
					onclick="showRegisterBack()">Back</span>
			</div>
		</div>
</body>
</html>

<script language="JavaScript" type="text/javascript">
$(document).ready(function(){
	checkLoginAndRedirect();
});
</script>