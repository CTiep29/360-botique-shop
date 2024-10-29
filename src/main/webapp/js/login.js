const login = (fn) => {
	if (fn.checkValidity()) {
		fn.method = 'post';
		fn.action = '/shop360/user/login';
		fn.submit();
	} else {
		fn.classList.add('was-validated');
	}
};