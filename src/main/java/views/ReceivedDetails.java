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

import libraries.Utilities;
import libraries.Utilities_Helper;
import objectcontrol.ReceivedNoteControl;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class ReceivedDeatailsView
 */
@WebServlet("/received/details")
public class ReceivedDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReceivedDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		UserObject user = (UserObject) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect("/shop360/user/login");
		} else {
			viewReceivedDetails(request, response, user);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void viewReceivedDetails(HttpServletRequest request, HttpServletResponse response, UserObject user)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);
		// Tìm bộ quản lí kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");

		ReceivedNoteControl rnc = new ReceivedNoteControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", rnc.getCP());
		}

		// tìm từ khóa
		int id = Utilities.getIntParam(request, "id");
		HashMap<String, Object> rn = rnc.getReceivedNoteObject(id);
		rnc.releaseConnection();
		if (user.getUser_id() != (int) rn.get("user_id") && user.getUser_role() != 1) {
			response.sendRedirect("/shop360/notfound");
		}

		ArrayList<HashMap<String, Object>> details = new ArrayList<HashMap<String, Object>>();
		details = rnc.getReceivedNoteDetails(id);

		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header?pos=receivednote");
		if (header != null) {
			header.include(request, response);
		}

		PrintWriter out = response.getWriter();
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex\">");
		out.append("<nav class=\"ms-auto\">");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/dashboard\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Nhập</li>");
		out.append("<li class=\"breadcrumb-item active\">Chi tiết</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");
		out.append("<div class=\"card my-4\">");
		out.append("<div class=\"card-header text-bg-primary\"></div>");
		out.append("<div class=\"card-body\">");
		out.append("<p class=\"fs-2 text-center mt-2\">Chi tiết phiếu nhập</p>");

		out.append("<div class=\"row my-3\">");
		out.append("<div class=\"col-sm-6\">");
		out.append("<p class=\"fw-bold\">ID: " + id + "</p>");
		out.append("<p class=\"fw-bold\">Ngày: " + rn.get("received_note_date") + "</p>");
		out.append("</div>");

		out.append("<div class=\"col-sm-6\">");
		out.append("<p class=\"fw-bold\">Nhân viên: " + rn.get("user_fullname") + "</p>");
		out.append("<p class=\"fw-bold\">Nhà cung cấp: " + rn.get("supplier_name") + "</p>");
		out.append("</div>");
		out.append("</div>");

		out.append("<table class=\"table table-striped table-hover table-sm \">");
		out.append("<thead>");
		out.append("<tr>");
		out.append("<th scope=\"col\">ID</th>");
		out.append("<th scope=\"col\">Tên sản phẩm</th>");
		out.append("<th scope=\"col\">Số lượng</th>");
		out.append("<th scope=\"col\">Đơn vị tính</th>");
		out.append("<th scope=\"col\">Giá nhập</th>");
		out.append("<th scope=\"col\">Tổng</th>");
		out.append("</tr>");
		out.append("</thead>");
		out.append("<tbody>");
		details.forEach(item -> {
			out.append("<tr>");
			out.append("<td class=\"align-middle\">" + item.get("product_id") + "</td>");
			out.append("<th scope=\"row\">" + item.get("product_name") + "</th>");
			out.append("<td class=\"align-middle\">" + item.get("received_quantity") + "</td>");
			out.append("<td class=\"align-middle\">" + item.get("product_unit") + "</td>");
			out.append("<td class=\"align-middle\">" + Utilities_Helper.formatNumber((int) item.get("received_price"))
					+ "đ</td>");
			out.append("<td class=\"align-middle\">" + Utilities_Helper
					.formatNumber((int) item.get("received_price") * (int) item.get("received_quantity")) + "đ</td>");
			out.append("</tr>");
		});
		out.append("<tr>");
		out.append("<td colspan=\"4\"></td>");
		out.append("<th class=\"align-middle\">Tổng giá trị:</th>");
		out.append("<th class=\"align-middle\">" + Utilities_Helper.formatNumber((int) rn.get("received_note_total"))
				+ "đ</th>");
		out.append("</tr>");
		out.append("</tbody>");
		out.append("</table>");
		out.append("<button type=\"button\" class=\"btn btn-primary my-2\" id=\"print\">");
		out.append("In PDF");
		out.append("</button>");
		out.append("</div>");
		out.append("<div class=\"card-footer text-bg-info\"></div>");

		out.append("</div>");
		out.append("</main><!-- End #main -->");

		// Tham chiếu tìm kiếm footer
		RequestDispatcher footer = request.getRequestDispatcher("/footer");
		if (footer != null) {
			footer.include(request, response);
		}

		out.append("<script src=\"https://code.jquery.com/jquery-3.6.0.min.js\"></script>");
		out.append("<script>");
		out.append("$(document).ready(function() {");
		out.append("$(\'#print\').on(\'click\', function() {");
		out.append("$.ajax({");
		out.append("url: \"/shop360/received/details?id=" + id + "\",");
		out.append("type: \'POST\',");
		out.append("success: function(response) {");
		out.append("	alert(\'Xuất file PDF thành công!!!\');");
		out.append("},");
		out.append("error: function(xhr, status, error) {");
		out.append("	alert(\'Xuất file PDF thất bại!!!\');");
		out.append("}");
		out.append("});");
		out.append("});");
		out.append("});");
		out.append("</script>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int id = Utilities.getIntParam(request, "id");
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");

		ReceivedNoteControl rnc = new ReceivedNoteControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", rnc.getCP());
		}
		rnc.printPDF(id, getServletContext());
		rnc.releaseConnection();
		response.setContentType(CONTENT_TYPE);
	}

}