package views;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javatuples.Pair;

import libraries.Utilities;
import objectcontrol.ProductControl;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class InventoryDetailsView
 */
@WebServlet("/inventory/details")
public class InventoryDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InventoryDetails() {
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
		if (user == null) {
			response.sendRedirect("/shop360/user/login");
		} else {
			view(request, response);
		}
	}

	protected void view(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);
		// Tìm bộ quản lí kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");

		ProductControl pc = new ProductControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", pc.getCP());
		}

		// lấy id sản phẩm
		int id = Utilities.getIntParam(request, "id");
		Pair<ArrayList<HashMap<String, Object>>, String> details = pc.getInventoryList(id);
		pc.releaseConnection();
		ArrayList<HashMap<String, Object>> data = details.getValue0();
		String name = details.getValue1();

		// tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header?pos=productinventory");
		if (header != null) {
			header.include(request, response);
		}

		PrintWriter out = response.getWriter();
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex\">");
		out.append("<nav class=\"ms-auto\">");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/view\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Sản phẩm</li>");
		out.append("<li class=\"breadcrumb-item\">Tồn kho</li>");
		out.append("<li class=\"breadcrumb-item active\">Chi tiết</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");
		out.append("<div class=\"card my-4\">");
		out.append("<div class=\"card-header text-bg-primary\"></div>");
		out.append("<div class=\"card-body\">");
		out.append("<p class=\"fs-2 text-center mt-2\">Chi tiết tồn kho</p>");

		out.append("<p class=\"fw-bold\">Tên sản phẩm: " + name + "</p>");

		out.append("<table class=\"table table-striped table-hover table-sm \">");
		out.append("<thead>");
		out.append("<tr>");
		out.append("<th scope=\"col\">Ngày nhập kho</th>");
		out.append("<th scope=\"col\">Số ngày tồn</th>");
		out.append("<th scope=\"col\">Số lượng</th>");
		out.append("</tr>");
		out.append("</thead>");
		out.append("<tbody>");

		data.forEach(item -> {
			out.append("<tr>");
			out.append("<td class=\"align-middle\">" + item.get("received_note_date") + "</td>");
			out.append("<th scope=\"row\">" + item.get("days_passed") + "</th>");
			out.append("<td class=\"align-middle\">" + item.get("shipment_quantity") + "</td>");
			out.append("</tr>");
		});

		out.append("</tbody>");
		out.append("</table>");
		out.append("</div>");
		out.append("<div class=\"card-footer text-bg-info\"></div>");

		out.append("</div>");
		out.append("</main><!-- End #main -->");

		// Tham chiếu tìm kiếm footer
		RequestDispatcher footer = request.getRequestDispatcher("/footer");
		if (footer != null) {
			footer.include(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
	}
}
