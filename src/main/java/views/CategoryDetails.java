package views;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import functionimpl.CategoryFunctionImpl;
import libraries.Utilities;
import objectfuctions.CategoryFunction;
import objects.CategoryObject;
import objects.ProductObject;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class CategoryDetail
 */
@WebServlet("/category/products")
public class CategoryDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset = utf-8";
	private static final int RECORDS_PER_PAGE = 5;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CategoryDetails() {
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

		int page = 1;
		if (request.getParameter("page") != null)
			page = Integer.parseInt(request.getParameter("page"));

		// Lấy category id để xem sản phẩm theo danh mục
		int categoryId = Utilities.getIntParam(request, "categoryid");
		// Tìm bộ quản lý kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		// Tạo đối tượng thực thi chức năng
		CategoryFunction cf = new CategoryFunctionImpl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", cf.getCP());
		}
		// Lấy thông tin danh mục
		CategoryObject category = cf.getCategory(categoryId);
		// Lấy danh sách sản phẩm hiển thị
		ArrayList<ProductObject> list = cf.getProductByCID(categoryId, (page - 1) * RECORDS_PER_PAGE,
				(byte) RECORDS_PER_PAGE);
		int noOfRecords = cf.getNoOfRecordsByCID(categoryId);
		int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / RECORDS_PER_PAGE);
		cf.releaseConnection();

		// Tham chiếu Servlet header
		RequestDispatcher h = request.getRequestDispatcher("/header?pos=cate");
		if (h != null) {
			h.include(request, response);
		}
		PrintWriter out = response.getWriter();
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex\">");
		out.append("<h2>" + category.getCategory_name() + "</h2>");
		out.append("<nav class=\"ms-auto\" >");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/dashboard\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Danh mục</li>");
		out.append("<li class=\"breadcrumb-item active\"> " + category.getCategory_name() + "</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");

		out.append("<section class=\"section\">");
		out.append("<div class=\"row\">");
		out.append("<div class=\"col-lg-12\">");

		out.append("<div class=\"card\">");
		out.append("<div class=\"card-body\">");

		out.append("<table class=\"table table-striped table-hover table-sm\">");
		out.append("<thead>");
		out.append("<tr>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\">ID</th>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\">Tên sản phẩm</th>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\">Giá</th>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\" >Đơn vị</th>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\">Số lượng</th>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\" colspan=\"2\">Thực hiện</th>");
		out.append("</tr>");
		out.append("</thead>");
		out.append("<tbody>");
		list.forEach(item -> {
			out.append("<tr>");
			out.append(
					"<th class=\"text-center align-middle py-3 px-3\" scope=\"row\">" + item.getProduct_id() + "</th>");
			out.append("<td class=\"text-center align-middle py-2 px-3\">" + item.getProduct_name() + "</td>");
			out.append("<td class=\"text-center align-middle py-3 px-3\">" + item.getProduct_price() + "</td>");
			out.append("<td class=\"text-center align-middle py-3 px-3\">" + item.getProduct_unit() + "</td>");
			out.append("<td class=\"text-center align-middle py-3 px-3\">" + item.getProduct_quantity() + "</td>");
			out.append(
					"<td class=\"text-center align-middle \"><a class = \"btn btn-primary btn-sm text-white\"href=\"\"><i class=\"bi bi-pencil-square\"></i></a></td>");
			out.append(
					"<td class=\"text-center align-middle \"><a class = \" btn btn-danger btn-sm text-white\" href=\"\"><i class=\"bi bi-archive\"></i></a></td>");
			out.append("</tr>");
		});
		out.append("</tbody>");
		out.append("</table>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</section>");
		out.append("<nav aria-label=\"Page navigation\" class=\"d-flex justify-content-center\">");
		out.append("<ul class=\"pagination\">");
		out.append("<li class=\"page-item\">");
		out.append("<a class=\"page-link\" href=\"#\" aria-label=\"Previous\">");
		out.append("<span aria-hidden=\"true\">&laquo;</span>");
		out.append("</a>");
		out.append("</li>");
		for (int i = 1; i <= noOfPages; i++) {
			if (i == page) {
				out.append("<li class=\"page-item active\"><a class=\"page-link\" href=\"/shop360/products?categoryid="
						+ categoryId + "&page=" + i + "\">" + i + "</a></li>");
			} else {
				out.append("<li class=\"page-item\"><a class=\"page-link\" href=\"/shop360/products?categoryid="
						+ categoryId + "&page=" + i + "\">" + i + "</a></li>");
			}
		}
		out.append("<li class=\"page-item\">");
		out.append("<a class=\"page-link\" href=\"#\" aria-label=\"Next\">");
		out.append("<span aria-hidden=\"true\">&raquo;</span>");
		out.append("</a>");
		out.append("</li>");
		out.append("</ul>");
		out.append("</nav>");
		out.append("</main><!-- End #main -->");

		// Tham chiếu Servlet footer
		RequestDispatcher f = request.getRequestDispatcher("/footer");
		if (f != null) {
			f.include(request, response);
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
