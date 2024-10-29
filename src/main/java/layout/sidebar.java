package layout;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import objects.UserObject;

/**
 * Servlet implementation class sidebar
 */
@WebServlet("/sidebar")
public class sidebar extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset = utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public sidebar() {
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
		response.setContentType(CONTENT_TYPE);
		boolean isManager = user.getUser_role() == 1;
		// Lấy tham số xác định vị trí menu
		String pos = request.getParameter("pos");

		// Các tập hợp xác định vị trí menu lớn nếu tồn tại key thì không có giá trị
		HashMap<String, String> collapsed = new HashMap<>();

		// Tập hợp xác định việc mở menu con, nếu tồn tại key thì sẽ có giá trị là show,
		// ngược lại không có
		HashMap<String, String> show = new HashMap<>();

		// Tập hợp xác định vị trí chọn menu con, nếu tồn tại key thì sẽ có giá trị
		// class= "active"
		HashMap<String, String> active = new HashMap<>();
		if (pos != null) {
			if (pos.contains("product")) {
				collapsed.put("product", "");
				show.put("product", "show");
				if (pos.contains("list")) {
					active.put("list", "class=\"active\"");
				} else if (pos.contains("trash")) {
					active.put("trash", "class=\"active\"");
				} else if (pos.contains("inventory")) {
					active.put("inventory", "class=\"active\"");
				}
			} else if (pos.contains("chart")) {
				collapsed.put("chart", "");
				show.put("chart", "show");
				if (pos.contains("dr")) {
					active.put("dr", "class=\"active\"");
				} else if (pos.contains("profit")) {
					active.put("profit", "class=\"active\"");
				} else if (pos.contains("invested")) {
					active.put("invested", "class=\"active\"");
				} else if (pos.contains("revenue")) {
					active.put("revenue", "class=\"active\"");
				}
			} else if (pos.contains("note")) {
				collapsed.put("note", "");
				show.put("note", "show");
				if (pos.contains("delivery")) {
					active.put("delivery", "class=\"active\"");
				} else if (pos.contains("received")) {
					active.put("received", "class=\"active\"");
				}
			} else if (pos.contains("cate")) {
				collapsed.put("cate", "");
				show.put("cate", "show");
				if (pos.contains("list"))
					active.put("list", "class=\"active\"");
			} else {
				collapsed.put("Dashboard", "");
			}
		} else {
			collapsed.put("Dashboard", "");
		}

		// tạo đối tượng xuất nội dung
		PrintWriter out = response.getWriter();
		out.append("<!-- ======= Sidebar ======= -->");
		out.append("<aside id=\"sidebar\" class=\"sidebar\">");
		out.append("<ul class=\"sidebar-nav\" id=\"sidebar-nav\">");

		out.append("<li class=\"nav-item\">");
		out.append("<a class=\"nav-link " + collapsed.getOrDefault("Dashboard", "collapsed")
				+ "\" href=\"/shop360/dashboard\">");
		out.append("<i class=\"bi bi-house\"></i>");
		out.append("<span>Trang chủ</span>");
		out.append("</a>");
		out.append("</li><!-- End Dashboard Nav -->");

		out.append("<li class=\"nav-item\">");
		out.append("<a class=\"nav-link " + collapsed.getOrDefault("product", "collapsed")
				+ "\" data-bs-target=\"#product-nav\" data-bs-toggle=\"collapse\" href=\"#\">");
		out.append(
				"<i class=\"bi bi-menu-button-wide\"></i><span>Sản phẩm</span><i class=\"bi bi-chevron-down ms-auto\"></i>");
		out.append("</a>");
		out.append("<ul id=\"product-nav\" class=\"nav-content collapse " + show.getOrDefault("product", "")
				+ "  \" data-bs-parent=\"#sidebar-nav\">");
		out.append("<li>");
		out.append("<a href=\"/shop360/products\" " + active.getOrDefault("list", "") + " >");
		out.append("<i class=\"bi bi-circle\"></i><span>Danh sách</span>");
		out.append("</a>");
		out.append("</li>");
		out.append("<li>");
		out.append("<a href=\"/shop360/products?trash\"" + active.getOrDefault("trash", "") + ">");
		out.append("<i class=\"bi bi-circle\"></i><span>Thùng rác</span>");
		out.append("</a>");
		out.append("</li>");
		out.append("<li>");
		out.append("<a href=\"/shop360/products/inventory\" " + active.getOrDefault("inventory", "") + ">");
		out.append("<i class=\"bi bi-circle\"></i><span>Tồn kho</span>");
		out.append("</a>");
		out.append("</li>");
		out.append("</ul>");
		out.append("</li><!-- End Components Nav -->");

		out.append("<li class=\"nav-item\">");
		out.append("<a class=\"nav-link " + collapsed.getOrDefault("cate", "collapsed")
				+ "\" data-bs-target=\"#category-nav\" data-bs-toggle=\"collapse\" href=\"#\">");
		out.append(
				"<i class=\"bi bi-list-task\"></i><span>Danh mục</span><i class=\"bi bi-chevron-down ms-auto\"></i>");
		out.append("</a>");
		out.append("<ul id=\"category-nav\" class=\"nav-content collapse " + show.getOrDefault("cate", "")
				+ "\" data-bs-parent=\"#sidebar-nav\">");
		out.append("<li>");
		out.append("<a href=\"/shop360/categories\" " + active.getOrDefault("list", "") + ">");
		out.append("<i class=\"bi bi-circle\"></i><span>Danh sách</span>");
		out.append("</a>");
		out.append("</li>");
		out.append("</ul>");
		out.append("</li>");

		out.append("<li class=\"nav-item\">");
		out.append("<a class=\"nav-link " + collapsed.getOrDefault("note", "collapsed")
				+ "\" data-bs-target=\"#notes-nav\" data-bs-toggle=\"collapse\" href=\"#\">");
		out.append(
				"<i class=\"bi bi-journals\"></i><span>Nhập/Xuất</span><i class=\"bi bi-chevron-down ms-auto\"></i>");
		out.append("</a>");
		out.append("<ul id=\"notes-nav\" class=\"nav-content collapse " + show.getOrDefault("note", "")
				+ "\" data-bs-parent=\"#sidebar-nav\">");
		out.append("<li>");
		out.append("<a href=\"/shop360/notes/received\" " + active.getOrDefault("received", "") + ">");
		out.append("<i class=\"bi bi-circle\"></i><span>Nhập</span>");
		out.append("</a>");
		out.append("</li>");
		out.append("<li>");
		out.append("<a href=\"/shop360/notes/delivery\"" + active.getOrDefault("delivery", "") + ">");
		out.append("<i class=\"bi bi-circle\"></i><span>Xuất</span>");
		out.append("</a>");
		out.append("</li>");
		out.append("</ul>");
		out.append("</li><!-- End Charts Nav -->");

		if (isManager) {
			out.append("<li class=\"nav-item\">");
			out.append("<a class=\"nav-link " + collapsed.getOrDefault("chart", "collapsed")
					+ "\" data-bs-target=\"#charts-nav\" data-bs-toggle=\"collapse\" href=\"#\">");
			out.append(
					"<i class=\"bi bi-bar-chart\"></i><span>Thống kê</span><i class=\"bi bi-chevron-down ms-auto\"></i>");
			out.append("</a>");
			out.append("<ul id=\"charts-nav\" class=\"nav-content collapse" + show.getOrDefault("chart", "")
					+ " \" data-bs-parent=\"#sidebar-nav\">");
			out.append("<li>");
			out.append("<a href=\"/shop360/chart/drchart\" " + active.getOrDefault("dr", "") + ">");
			out.append("<i class=\"bi bi-circle\"></i><span>Nhập/Xuất</span>");
			out.append("</a>");
			out.append("</li>");
			out.append("<li>");
			out.append("<a href=\"/shop360/chart/invested\" " + active.getOrDefault("invested", "") + ">");
			out.append("<i class=\"bi bi-circle\"></i><span>Đầu tư</span>");
			out.append("</a>");
			out.append("</li>");
			out.append("<li>");
			out.append("<a href=\"/shop360/chart/revenue\" " + active.getOrDefault("revenue", "") + ">");
			out.append("<i class=\"bi bi-circle\"></i><span>Doanh thu</span>");
			out.append("</a>");
			out.append("</li>");
			out.append("<li>");
			out.append("<a href=\"/shop360/chart/profit\" " + active.getOrDefault("profit", "") + ">");
			out.append("<i class=\"bi bi-circle\"></i><span>Lợi nhuận</span>");
			out.append("</a>");
			out.append("</li>");
			out.append("</ul>");
			out.append("</li><!-- End Charts Nav -->");
		}
		out.append("</ul>");
		out.append("</aside><!-- End Sidebar-->");
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