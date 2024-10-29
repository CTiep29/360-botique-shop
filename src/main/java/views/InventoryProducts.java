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
 * Servlet implementation class InventoryListView
 */
@WebServlet("/products/inventory")
public class InventoryProducts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InventoryProducts() {
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

		short page = Utilities.getPageParam(request, "p"); // lấy tham số phân trang

		ArrayList<String> view = pc.viewInventoryProducts(page, (byte) 10);
		pc.releaseConnection();

		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header?pos=productinventory");
		if (header != null) {
			header.include(request, response);
		}

		PrintWriter out = response.getWriter();
		// Nội dung trang
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex\">");
		out.append("<nav class=\"ms-auto\">");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/view\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Sản phẩm</li>");
		out.append("<li class=\"breadcrumb-item active\">Tồn kho</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");

		out.append("<div class=\"card\">");
		out.append("<div class=\"card-header text-bg-primary\"></div>");
		out.append("<div class=\"card-body\">");
		out.append(view.get(0));
		out.append("</div>");// card-body
		out.append("<div class=\"card-footer text-bg-info\"></div>");
		out.append("</div>");// card

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
		request.setCharacterEncoding("utf-8");
	}

	public static ArrayList<String> viewInventoryProducts(Pair<ArrayList<HashMap<String, Object>>, Integer> datas,
			short page, byte totalPerPage) {
		// bóc tách dữ liệu để xử lý
		ArrayList<HashMap<String, Object>> items = datas.getValue0();
		int total = datas.getValue1();
		// cấu trúc trình bày danh sách và phân trang
		ArrayList<String> tmp = new ArrayList<>();

		// danh sách
		StringBuilder list = new StringBuilder();
		list.append("<table class=\"table table-striped table-sm\">");
		list.append("<thead>");
		list.append("<tr>");
		list.append("<th scope=\"col\">#</th>");
		list.append("<th scope=\"col\">Tên sản phẩm</th>");
		list.append("<th scope=\"col\">Kích cỡ</th>");
		list.append("<th scope=\"col\">Giới tính</th>");
		list.append("<th scope=\"col\">Số lượng tồn</th>");
		list.append("<th scope=\"col\">Xem</th>");
		list.append("</tr>");
		list.append("</thead>");
		list.append("<tbody>");
		items.forEach(item -> {
			int sex = Integer.valueOf(item.get("product_sex") + "");
			String value = sex == 1 ? "Nam" : "Nữ";
			list.append("<tr>");
			list.append("<th scope=\"row\" class=\"align-middle\">" + item.get("product_id") + "</th>");
			list.append("<td class=\"align-middle\">" + item.get("product_name") + "</td>");
			list.append("<td class=\"align-middle\">" + item.get("product_size") + "</td>");
			list.append("<td class=\"align-middle\">" + value + "</td>");
			list.append("<td class=\"align-middle\">" + item.get("total_quantity") + "</td>");
			list.append("<td class=\"align-middle\"><a href=\"/shop360/inventory/details?id=" + item.get("product_id") + "\" "
					+ "class=\"btn btn-outline-primary btn-sm\" > <i class=\"bi bi-eye-fill\"></i> </a></td>");
			list.append("</tr>");

		});

		list.append("</tbody>");
		list.append("</table>");
		String key = "";
		// phân trang
		list.append(InventoryProducts.getPaging("/shop360/products/inventory", key, total, page, totalPerPage));
		tmp.add(list.toString());// danh sách
		return tmp;
	}

	// phân trang
	public static String getPaging(String url, String key, int total, short page, byte totalPerPage) {
		// tính số trang
		short countPages = (short) (total / totalPerPage);
		if (total % totalPerPage != 0) {
			countPages++;
		}

		if (page <= 0 || page > countPages) {
			page = 1;
		}

		// xử lý key
		if (key != null && !key.equalsIgnoreCase("")) {
			key = "";
		} else {
			key = "";
		}

		StringBuilder left = new StringBuilder();
		StringBuilder right = new StringBuilder();
		int count = 0;

		for (short p = (short) (page - 1); p > 0; p--) {
			left.insert(0, "<li class=\"page-item\"><a class=\"page-link\" href=\"" + url + "?p=" + p + key + "\">" + p
					+ "</a></li>");
			if (++count >= 2) {
				break;
			}
		}
		if (page - 1 >= 3) {
			left.insert(0, "<li class=\"page-item\"><a class=\"page-link\" href=\"#\">...</a></li>");
		}
		count = 0;
		for (short p = (short) (page + 1); p <= countPages; p++) {
			right.append("<li class=\"page-item\"><a class=\"page-link\" href=\"" + url + "?p=" + p + key + "\">" + p
					+ "</a></li>");
			if (++count >= 2) {
				break;
			}
		}
		if (countPages - page >= 3) {
			right.append("<li class=\"page-item disable\"><a class=\"page-link\" href=\"#\">...</a></li>");
		}

		StringBuilder tmp = new StringBuilder();
		tmp.append("<nav aria-label=\"...\">");
		tmp.append("<ul class=\"pagination justify-content-center\">");
		tmp.append("<li class=\"page-item\"><a class=\"page-link\" href=\"" + url
				+ "?p=1\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
		tmp.append(left);
		tmp.append("<li class=\"page-item active\" aria-current=\"page\"><a class=\"page-link\" href=\"#\">" + page
				+ "</a></li>");
		tmp.append(right);
		tmp.append("<li class=\"page-item\"><a class=\"page-link\" href=\"" + url + "?p=" + countPages
				+ "\" ><span aria-hidden=\"true\">&raquo;</span></a></li>");
		tmp.append("</ul>");
		tmp.append("</nav>");
		return tmp.toString();
	}
}
