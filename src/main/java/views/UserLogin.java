package views;

import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import functionimpl.UserFunctionImpl;
import objectfuctions.UserFunction;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class UserLogin
 */
@WebServlet("/user/login")
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserLogin() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response) Thường cung cấp GUI, Html Được gọi thông qua url của trình
	 *      duyệt, hoặc sự kiện của form (method=get)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);

		// Tim tham so bao loi neu co
		String error = request.getParameter("err");
		String msg = "";
		if (error != null) {
			switch (error) {
			case "param":
				msg = "Tham số lấy giá trị không chính xác";
				break;
			case "value":
				msg = "Không tồn tại giá trị";
				break;
			case "notok":
				msg = "Đăng nhập thất bại";
				break;
			default:
				msg = "Không thành công";
			}
		}
		
		PrintWriter out = response.getWriter();
		out.append(" <!doctype html>");
		out.append(" <html lang=\"en\">");
		out.append(" <head>");
		out.append(" <meta charset=\"utf-8\">");
		out.append(" <title>Login</title>");
		out.append("<link href=\"/shop360/img/favicon.png\" rel=\"icon\">");
		out.append("<!-- Google Fonts -->");
		out.append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">");
		out.append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>");
		out.append(
				"<link href=\"https://fonts.googleapis.com/css2?family=Source+Sans+3:ital,wght@0,200..900;1,200..900&display=swap\" rel=\"stylesheet\">");
		out.append(" <link href=\"/shop360/css/all.min.css\" rel=\"stylesheet\" type=\"text/css\" />");
		out.append(" <link href=\"/shop360/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\" />");
		out.append("<link href=\"/shop360/css/bi/bootstrap-icons.css\" rel=\"stylesheet\">");
		out.append(" <link href=\"/shop360/css/basic.css\" rel=\"stylesheet\" type=\"text/css\" />");
		out.append(" </head>");
		out.append(" <body>");
		out.append(" <div class=\"container-fluid bg-full\">");
		out.append(" <div class=\"container-sm\">");
		out.append(" <div class=\"row vh-100 align-items-center justify-content-center\">");
		out.append(" <div class=\" col-lg-5 col-md-7\">");
		out.append(" <div class=\"bg-light view\" id=\"view\">");
		out.append(" <div class=\"row my-5\">");
		out.append(" <div class=\"col-sm-12 text-center pt-2\">");
		out.append(" <img src=\"/shop360/img/logo.png\" alt=\"\" class=\"w-15\" />");
		out.append(" </div>");
		out.append(" </div>");

		out.append(" <form action=\"\" class=\"needs-validation\" novalidate>");
		out.append(" <div class=\"row justify-content-center mb-4\">");
		out.append(" <div class=\"col-sm-8\">");
		out.append(" <div class=\"input-group\">");
		out.append(" <label");
		out.append(" for=\"txtName\"");
		out.append(" class=\"input-group-text text-bg-primary border-primary shadow-sm\"");
		out.append(" >");
		out.append(" <i class=\"bi bi-person-circle\"></i>");
		out.append(" </label>");
		out.append(" <input");
		out.append(" type=\"text\"");
		out.append(" name=\"txtName\"");
		out.append(" id=\"txtName\"");
		out.append(" class=\"form-control border-primary shadow-sm rounded-end\"");
		out.append(" placeholder=\"Tên đăng nhập\"");
		out.append(" required");
		out.append(" />");
		out.append(" <div class=\"invalid-feedback text-center\">");
		out.append(" Vui lòng nhập tên đăng nhập!");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" <div class=\"row justify-content-center mb-4\">");
		out.append(" <div class=\"col-sm-8\">");
		out.append(" <div class=\"input-group\">");
		out.append(" <label");
		out.append(" for=\"txtPass\"");
		out.append(" class=\"input-group-text text-bg-primary border-primary shadow-sm\"");
		out.append(" >");
		out.append(" <i class=\"bi bi-key-fill\"></i>");
		out.append(" </label>");
		out.append(" <input");
		out.append(" type=\"password\"");
		out.append(" name=\"txtPass\"");
		out.append(" id=\"txtPass\"");
		out.append(" class=\"form-control border-primary shadow-sm rounded-end\"");
		out.append(" placeholder=\"Mật khẩu\"");
		out.append(" required");
		out.append(" />");
		out.append(" <div class=\"invalid-feedback text-center\">");
		out.append(" Vui lòng nhập mật khẩu!");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </div>");

		if (!"".equalsIgnoreCase(msg)) {
			out.append(" <div>");
			out.append(" <p class=\"text-center text-danger\">");
			out.append(msg);
			out.append(" </p>");
			out.append(" </div>");
		}

		out.append(" <div class=\"row justify-content-center mb-5\">");
		out.append(" <div class=\"col-sm-8\">");
		out.append(" <button");
		out.append(" type=\"button\"");
		out.append(" class=\"btn text-bg-primary w-100 shadow-lg\"");
		out.append(" onClick=\"login(this.form)\">");
		out.append(" <i class=\"bi bi-box-arrow-in-left me-2\"></i>");
		out.append(" Đăng nhập");
		out.append(" </button>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </form>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" <script src=\"/shop360/js/bootstrap.bundle.min.js\"></script>");
		out.append(" <script src=\"/shop360/js/main.js\"></script>");
		out.append(" <script src=\"/shop360/js/login.js\"></script>");
		out.append(" </body>");
		out.append(" </html>");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response) Xử lý dữ liệu do doGet truyền cho
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String name = request.getParameter("txtName");
		String pass = request.getParameter("txtPass");

		if (name != null && pass != null) {
			name = name.trim();
			pass = pass.trim();

			if (!"".equalsIgnoreCase(name) && !"".equalsIgnoreCase(pass)) {
				// Tham chiếu ngữ cảnh ứng dụng
				ServletContext application = getServletConfig().getServletContext();
				// Tim bo quan ly ket noi
				ConnectionPool cp = (ConnectionPool) application.getAttribute("CPool");
				// Tạo đối tượng thực thi chức năng
				UserFunction uf = new UserFunctionImpl(cp);
				if (cp == null) {
					application.setAttribute("CPool", uf.getCP());
				}

				// Thực hiện đăng nhập
				UserObject user = uf.getUserObject(name, pass);
				// Trả về kết nối
				uf.releaseConnection();
				// Kiểm tra kết quả
				if (user != null) {
					// Tham chiếu phiên làm việc (Session)
					HttpSession session = request.getSession(true);
					// Đưa thông tin đăng nhập vào phiên
					session.setAttribute("user", user);
					// Chuyển sang giao diện chính
					response.sendRedirect("/shop360/dashboard");
				} else {
					response.sendRedirect("/shop360/user/login?err=notok");
				}
			} else {
				response.sendRedirect("/shop360/user/login?err=value");
			}
		} else {
			response.sendRedirect("/shop360/user/login?err=param");
		}
	}
}
