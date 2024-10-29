package layout;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import objects.UserObject;

/**
 * Servlet implementation class header
 */
@WebServlet("/header")
public class header extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset = utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public header() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UserObject user = (UserObject) request.getSession().getAttribute("user");
		if (user == null)
			response.sendRedirect("/shop360/notfound");
		else
			view(request, response, user);
	}

	protected void view(HttpServletRequest request, HttpServletResponse response, UserObject user)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);
		String name = user.getUser_name();
		String role = user.getUser_role() == 1 ? "Quản lý" : "Nhân viên";
		String fullname = user.getUser_fullname();
		String avatar = user.getUser_avatar();

		// tạo đối tượng xuất nội dung
		PrintWriter out = response.getWriter();

		out.append("<!DOCTYPE html>");
		out.append("<html lang=\"en\">");

		out.append("<head>");
		out.append("<meta charset=\"utf-8\">");
		out.append("<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\">");

		out.append("<title>Shop360</title>");
		out.append("<meta content=\"\" name=\"description\">");
		out.append("<meta content=\"\" name=\"keywords\">");

		out.append("<link href=\"/shop360/img/favicon.png\" rel=\"icon\">");
		out.append("<link href=\"/shop360/img/apple-touch-icon.png\" rel=\"apple-touch-icon\">");

		out.append("<!-- Google Fonts -->");
		out.append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">");
		out.append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>");
		out.append(
				"<link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@100..900&family=Source+Sans+3:ital,wght@0,200..900;1,200..900&display=swap\" rel=\"stylesheet\">");
		out.append("<link href=\"/shop360/css/bootstrap.min.css\" rel=\"stylesheet\">");
		out.append("<link href=\"/shop360/css/bi/bootstrap-icons.css\" rel=\"stylesheet\">");
		out.append("<link href=\"/shop360/css/style.css\" rel=\"stylesheet\">");
		out.append("</head>");

		out.append("<body>");
		out.append("<!-- ======= Header ======= -->");
		out.append("<header id=\"header\" class=\"header fixed-top d-flex align-items-center\">");

		out.append("<div class=\"d-flex align-items-center justify-content-between\">");
		out.append("<a href=\"/shop360/dashboard\" class=\"logo d-flex align-items-center\">");
		out.append("<img src=\"/shop360/img/logo.png\" alt=\"\">");
		out.append("<span class=\"d-none d-lg-block\">Shop360</span>");
		out.append("</a>");
		out.append("<i class=\"bi bi-list toggle-sidebar-btn\"></i>");
		out.append("</div><!-- End Logo -->");

		String key = request.getParameter("keyword");
		String saveKey = (key != null && !key.equalsIgnoreCase("")) ? key.trim() : "";

		out.append("<div class=\"search-bar\">");
		out.append(
				"<form class=\"search-form d-flex align-items-center\" method=\"get\" action=\"/shop360/products\">");
		out.append(
				"<input type=\"text\" name=\"keyword\" placeholder=\"Tìm kiếm\" title=\"Nhập từ khóa tìm kiếm\" value= \""
						+ saveKey + "\">");
		out.append("<button type=\"submit\" title=\"Tìm kiếm\"><i class=\"bi bi-search\"></i></button>");
		out.append("</form>");
		out.append("</div><!-- End Search Bar -->");

		out.append("<nav class=\"header-nav ms-auto\">");
		out.append("<ul class=\"d-flex align-items-center\">");

		out.append("<li class=\"nav-item d-block d-lg-none\">");
		out.append("<a class=\"nav-link nav-icon search-bar-toggle \" href=\"#\">");
		out.append("<i class=\"bi bi-search\"></i>");
		out.append("</a>");
		out.append("</li><!-- End Search Icon-->");

		out.append("<li class=\"nav-item dropdown\">");
		out.append("<a class=\"nav-link nav-icon\" href=\"#\" data-bs-toggle=\"dropdown\">");
		out.append("<i class=\"bi bi-bell\"></i>");
		out.append("<span class=\"badge bg-primary badge-number\">0</span>");
		out.append("</a><!-- End Notification Icon -->");
		out.append("</li><!-- End Notification Nav -->");

		out.append("<li class=\"nav-item dropdown\">");

		out.append("<a class=\"nav-link nav-icon\" href=\"#\" data-bs-toggle=\"dropdown\">");
		out.append("<i class=\"bi bi-chat-left-text\"></i>");
		out.append("<span class=\"badge bg-success badge-number\">0</span>");
		out.append("</a><!-- End Messages Icon -->");
		out.append("</li><!-- End Messages Nav -->");

		out.append("<li class=\"nav-item dropdown pe-3\">");

		out.append(
				"<a class=\"nav-link nav-profile d-flex align-items-center pe-0\" href=\"#\" data-bs-toggle=\"dropdown\">");
		out.append("<img src=\"/shop360/img/avatars/" + avatar
				+ "\" alt=\"Profile\" class=\"rounded-circle\" style=\"width: 33px; height: 33px; object-fit: cover; object-position: center;\">");
		out.append("<span class=\"d-none d-md-block dropdown-toggle ps-2\">" + name + "</span>");
		out.append("</a><!-- End Profile Image Icon -->");

		out.append("<ul class=\"dropdown-menu dropdown-menu-end dropdown-menu-arrow profile\">");
		out.append("<li class=\"dropdown-header\">");
		out.append("<h6>" + fullname + "</h6>");
		out.append("<span>" + role + "</span>");
		out.append("</li>");
		out.append("<li>");
		out.append("<hr class=\"dropdown-divider\">");
		out.append("</li>");

		out.append("<li>");
		out.append("<a class=\"dropdown-item d-flex align-items-center\" href=\"#\">");
		out.append("<i class=\"bi bi-person\"></i>");
		out.append("<span>Tài khoản của tôi</span>");
		out.append("</a>");
		out.append("</li>");
		out.append("<li>");
		out.append("<hr class=\"dropdown-divider\">");
		out.append("</li>");

		out.append("<li>");
		out.append("<a class=\"dropdown-item d-flex align-items-center\" href=\"/shop360/user/logout\">");
		out.append("<i class=\"bi bi-box-arrow-right\"></i>");
		out.append("<span>Đăng xuất</span>");
		out.append("</a>");
		out.append("</li>");

		out.append("</ul><!-- End Profile Dropdown Items -->");
		out.append("</li><!-- End Profile Nav -->");

		out.append("</ul>");
		out.append("</nav><!-- End Icons Navigation -->");

		out.append("</header><!-- End Header -->");

		// Tham chiếu tìm kiếm sidebar
		RequestDispatcher sidebar = request.getRequestDispatcher("/sidebar");
		if (sidebar != null) {
			sidebar.include(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
