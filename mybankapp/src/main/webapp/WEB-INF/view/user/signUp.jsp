<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>

<%@ include file="/WEB-INF/view/layout/header.jsp"%>

<div class="col-sm-8">
	<h2>ȸ������</h2>
	<h5>�������</h5>
	<div>
		<form action="/user/sign-up" method="post">
			<div class="form-group">
				<label for="username">username:</label> <input type="text"
					class="form-control" placeholder="Enter username" id="username"
					name="username">
			</div>
			<div class="form-group">
				<label for="pwd">Password:</label> <input type="password"
					class="form-control" placeholder="Enter password" id="pwd"
					name="password">
			</div>
			<div class="form-group">
				<label for="fullname">fullname:</label> <input type="text"
					class="form-control" placeholder="Enter fullname" id="fullname"
					name="fullname">
			</div>
			<button type="submit" class="btn btn-primary">ȸ�� ����</button>
		</form>
	</div>
</div>
</div>
</div>



<%@ include file="/WEB-INF/view/layout/footer.jsp"%>
