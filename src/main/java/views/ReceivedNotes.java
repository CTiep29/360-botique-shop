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

import org.javatuples.Triplet;

import functionimpl.UserFunctionImpl;
import libraries.Utilities;
import libraries.Utilities_Date;
import libraries.Utilities_Helper;
import objectcontrol.ProductControl;
import objectcontrol.ReceivedNoteControl;
import objectfuctions.UserFunction;
import objects.ProductObject;
import objects.ReceivedNoteObject;
import objects.ReceivedObject;
import objects.SupplierObject;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class ReceivedNoteView
 */
@WebServlet("/notes/received")
public class ReceivedNotes extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReceivedNotes() {
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
			viewReceivedNotes(request, response, user);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void viewReceivedNotes(HttpServletRequest request, HttpServletResponse response, UserObject user)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);
		// Tìm bộ quản lí kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		boolean isManager = user.getUser_role() == 1;

		ReceivedNoteControl rnc = new ReceivedNoteControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", rnc.getCP());
		}

		// tạo đối tượng lưu trữ thông tin bộ lọc
		ReceivedNoteObject similar = new ReceivedNoteObject();
		if (isManager) {
			String emp = request.getParameter("emp");
			if (null != emp) {
				Integer userId = Integer.parseInt(emp);
				similar.setUser_id(userId);
			}
		} else {
			similar.setUser_id(user.getUser_id());
		}

		String date = request.getParameter("date");
		if (null != date) {
			similar.setReceived_note_date(date);
		}

		short page = Utilities.getPageParam(request, "p"); // lấy tham số phân trang

		// lấy cấu trúc
		Triplet<ReceivedNoteObject, Short, Byte> infos = new Triplet<>(similar, page, (byte) 10);
		ArrayList<SupplierObject> suppliers = rnc.getSuppliers();
		ArrayList<String> view = rnc.viewReceivedNotes(infos);
		rnc.releaseConnection();
		UserFunction u = new UserFunctionImpl(cp);
		ArrayList<UserObject> staffs = u.getStaffs();
		u.releaseConnection();

		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header?pos=receivednote");
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
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/dashboard\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Phiếu</li>");
		out.append("<li class=\"breadcrumb-item active\">Nhập</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");

		out.append("<div class=\"card\">");
		out.append("<div class=\"card-body\">");

		out.append("<div class=\"row my-2 align-items-center\">");
		// nút thêm phiếu nhập
		out.append("<div class=\"col-sm-2\">");
		if (!isManager) {
			out.append(
					"<button type=\"button\" class=\"btn btn-primary my-2\" data-bs-toggle=\"modal\" data-bs-target=\"#addProduct\">");
			out.append("<i class=\"bi bi-plus-circle\"></i> Thêm mới");
			out.append("</button>");
		}
		out.append("</div>");

		out.append("<div class=\"col-sm-4\">");
		out.append("</div>");

		out.append("<div class=\"col-sm-3\">");
		if (isManager) {
			out.append("<select class=\"form-select my-2\" id=\"emp\" name=\"emp\" required>");
			out.append("<option value=\"\">Chọn nhân viên</option>");
			staffs.forEach(staff -> {
				out.append("<option value=\"" + staff.getUser_id() + "\">" + staff.getUser_fullname() + "</option>");
			});
			out.append("</select>");
		}
		out.append("</div>");

		// ô input chọn ngày
		out.append("<div class=\"col-sm-3\">");
		out.append("<input type=\"date\" class=\"form-control my-2\" id=\"date\" name=\"date\">");
		out.append("</div>");
		out.append("</div>");

		// modal thêm phiếu
		out.append(
				"<div class=\"modal fade\" id=\"addProduct\" data-bs-backdrop=\"static\" data-bs-keyboard=\"false\" tabindex=\"-1\" aria-labelledby=\"addProductLabel\" aria-hidden=\"true\">");
		out.append("<div class=\"modal-dialog modal-lg\">");
		out.append("<div class=\"modal-content\">");

		// form thêm
		out.append(
				"<form method=\"post\" action=\"/shop360/notes/received\" id=\"add\" class=\"needs-validation\" novalidate>");
		out.append("<div class=\"modal-header text-bg-primary\">");
		out.append(
				"<h1 class=\"modal-title fs-5\" id=\"addProductLabel\"><i class=\"bi bi-truck\"></i> Nhập hàng</h1>");
		out.append(
				"<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
		out.append("</div>");
		out.append("<div class=\"modal-body\">");
		out.append("<div class=\"row mb-3\">");
		out.append("<div class=\"col-sm-12\">");
		out.append("<label for=\"supplier_id\" class=\"form-label\">Nhà cung cấp</label>");
		out.append("<select class=\"form-select\" id=\"supplier_id\" name=\"slcSupplier\" required>");
		out.append("<option value=\"\">Chọn nhà cung cấp</option>");
		suppliers.forEach(supplier -> {
			out.append(
					"<option value=\"" + supplier.getSupplier_id() + "\">" + supplier.getSupplier_name() + "</option>");
		});
		out.append("</select>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin nhà cung cấp</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("<div class=\"row mb-3\">");
		out.append("<div class=\"col-sm-4\">");
		out.append(
				"<button id=\"addInputBtn\" type=\"button\" class=\"btn btn-success\"><i class=\"bi-plus-circle\"></i> Thêm sản phẩm</button>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin nhà cung cấp</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("<input type=\"hidden\" id=\"counter\" value=\"0\" name=\"counter\">");
		out.append("<div class=\"row mb-3\">");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"product_id0\" class=\"form-label\">ID</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"product_id0\" name=\"txtProductID0\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin sản phẩm</div>");
		out.append("</div>");
		out.append("<div class=\"col-sm-4\">");
		out.append("<label for=\"product_name0\" class=\"form-label\">Sản phẩm</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"product_name0\" name=\"txtProductName0\" required readonly>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"product_quantity0\" class=\"form-label\">SL còn</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"product_quantity0\" name=\"txtProductQuantity0\" required readonly>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"received_quantity0\" class=\"form-label\">SL nhập</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"received_quantity0\" name=\"txtQuantity0\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin số lượng sản phẩm</div>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"received_price0\" class=\"form-label\">Giá nhập</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"received_price0\" name=\"txtPrice0\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin giá sản phẩm</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("<div class=\"modal-footer\">");
		out.append(
				"<button type=\"submit\" class=\"btn btn-primary\" name=\"submit\"><i class=\"bi bi-plus-circle\"></i> Thêm mới</button>");
		out.append("<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Close</button>");
		out.append("</div>");
		out.append("</form>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append(view.get(0));
		out.append("</div>");// card-body
		out.append("</div>");// card
		out.append("</main><!-- End #main -->");

		// Tham chiếu tìm kiếm footer
		RequestDispatcher footer = request.getRequestDispatcher("/footer");
		if (footer != null) {
			footer.include(request, response);
		}

		// javascript
		out.append("<script src=\"https://code.jquery.com/jquery-3.6.0.min.js\"></script>");
		out.append("<script>");
		out.append("document.addEventListener(\"DOMContentLoaded\", function () {");
		out.append("var i = 0;");
		out.append("var sourceInput = document.getElementById(\'product_id0\');");
		out.append("var targetInput1 = 0;");
		out.append("var targetInput2 = 0;");
		out.append("var counterInput = document.getElementById(\'counter\');");
		out.append("document.addEventListener(\'click\', function(event) {");
		out.append("sourceInput = event.target;");
		out.append("if (sourceInput.id.startsWith(\"product_id\")) {");
		out.append("var number = parseInt(sourceInput.id.substring(10));");
		out.append("targetInput1 = document.getElementById(\"product_name\" + number);");
		out.append("targetInput2 = document.getElementById(\"product_quantity\" + number);");
		out.append("sourceInput.addEventListener(\'keyup\', function() {");
		out.append("var value = sourceInput.value;");
		out.append("var xhttp = new XMLHttpRequest();");
		out.append("xhttp.onreadystatechange = function() {");
		out.append("if (this.readyState == 4 && this.status == 200) {");
		out.append("targetInput1.value = this.responseText.split(\"+\")[0];");
		out.append("targetInput2.value = this.responseText.split(\"+\")[1];");
		out.append("}");
		out.append("};");
		out.append("");
		out.append("xhttp.open(\"POST\", \"/shop360/notes/received\", true);");
		out.append("xhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");");
		out.append("xhttp.send(\"inputValue=\" + value);");
		out.append("});");
		out.append("}");
		out.append("});");

		out.append("var addInputBtn = document.getElementById(\"addInputBtn\");");
		out.append("var addForm = document.getElementById(\"add\");");
		out.append("addInputBtn.addEventListener(\"click\", function (event) {");
		out.append("event.preventDefault();");
		out.append("i = i + 1;");
		out.append("counterInput.value = i;");
		out.append("var newElement = `");
		out.append("<div class=\"row mb-3\">");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"product_id${i}\" class=\"form-label\">ID</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"product_id${i}\" name=\"txtProductID${i}\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin sản phẩm</div>");
		out.append("</div>");
		out.append("<div class=\"col-sm-4\">");
		out.append("<label for=\"product_name${i}\" class=\"form-label\">Sản phẩm</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"product_name${i}\" name=\"txtProductName${i}\" required readonly>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"product_quantity${i}\" class=\"form-label\">SL còn</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"product_quantity${i}\" name=\"txtProductQuantity${i}\" required readonly>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"received_quantity${i}\" class=\"form-label\">SL nhập</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"received_quantity${i}\" name=\"txtQuantity${i}\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin số lượng sản phẩm</div>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"received_price${i}\" class=\"form-label\">Giá nhập</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"received_price${i}\" name=\"txtPrice${i}\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin giá sản phẩm</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("`;");
		out.append("var lastRow = addForm.querySelector(\".row:last-of-type\");");
		out.append("lastRow.insertAdjacentHTML(\"afterend\", newElement);");
		out.append("});");
		out.append("});");

		if (isManager) {
			out.append("document.getElementById(\"emp\").addEventListener(\"change\", function() {");
			out.append("var emp = this.value;");
			out.append("var url = \"/shop360/notes/received?emp=\" + encodeURIComponent(emp);");
			out.append("var xhr = new XMLHttpRequest();");
			out.append("xhr.open(\"GET\", url, true);");
			out.append("xhr.setRequestHeader(\"Content-Type\", \"application/x-www-form-urlencoded\");");
			out.append("xhr.onreadystatechange = function() {");
			out.append("if (xhr.readyState === XMLHttpRequest.DONE) {");
			out.append("if (xhr.status === 200) {");
			out.append("var response = xhr.responseText;");
			out.append("window.location.href = url;");
			out.append("} else {");
			out.append("console.error(\"Đã xảy ra lỗi\");");
			out.append("}");
			out.append("}");
			out.append("};");
			out.append("xhr.send();");
			out.append("});");
		}

		out.append("document.getElementById(\"date\").addEventListener(\"change\", function() {");
		out.append("var date = this.value;");
		out.append("var url = \"/shop360/notes/received?date=\" + encodeURIComponent(date);");
		out.append("var xhr = new XMLHttpRequest();");
		out.append("xhr.open(\"GET\", url, true);");
		out.append("xhr.setRequestHeader(\"Content-Type\", \"application/x-www-form-urlencoded\");");
		out.append("xhr.onreadystatechange = function() {");
		out.append("if (xhr.readyState === XMLHttpRequest.DONE) {");
		out.append("if (xhr.status === 200) {");
		out.append("var response = xhr.responseText;");
		out.append("window.location.href = url;");
		out.append("} else {");
		out.append("console.error(\"Đã xảy ra lỗi\");");
		out.append("}");
		out.append("}");
		out.append("};");
		out.append("xhr.send();");
		out.append("});");

		out.append("</script>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");

		// Tìm bộ quản lý kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");

		String submitValue = request.getParameter("submit");
		if (submitValue != null) {
			ReceivedNoteControl dnc = new ReceivedNoteControl(cp);
			if (cp == null) {
				getServletContext().setAttribute("CPool", dnc.getCP());
			}
			int supplier_id = Utilities.getIntParam(request, "slcSupplier");
			int counter = Integer.parseInt(request.getParameter("counter"));
			ReceivedNoteObject n = new ReceivedNoteObject();
			String today = Utilities_Date.getDate();
			n.setReceived_note_date(today);
			n.setSupplier_id((short) supplier_id);
			UserObject user = (UserObject) request.getSession().getAttribute("user");
			n.setUser_id(user.getUser_id());
			ArrayList<ReceivedObject> myList = new ArrayList<>();
			for (int i = 0; i <= counter; i++) {
				int product_id = Integer.parseInt(request.getParameter("txtProductID" + i));
				int quantity = Integer.parseInt(request.getParameter("txtQuantity" + i));
				int price = Integer.parseInt(request.getParameter("txtPrice" + i));

				ReceivedObject nr = new ReceivedObject();
				nr.setProduct_id(product_id);
				nr.setReceived_quantity(quantity);
				nr.setReceived_price(price);
				myList.add(nr);
			}

			// Thực hiện thêm mới
			boolean result = dnc.addReceivedNote(n, myList);
			dnc.releaseConnection();

			// Trả kết quả
			if (result) {
				response.sendRedirect("/shop360/notes/received");
			}
		}

		String inputValue = request.getParameter("inputValue");
		if (inputValue != null) {
			int id = Integer.parseInt(inputValue);
			ProductControl pc = new ProductControl(cp);
			ProductObject p = pc.getProductObject(id);
			pc.releaseConnection();
			response.setContentType(CONTENT_TYPE);
			if (p != null) {
				if (p.getProduct_deleted() == 0) {
					String processedValue = p.getProduct_name() + "+" + p.getProduct_quantity();
					response.getWriter().write(processedValue);
				} else {
					response.getWriter().write("Đã ngừng bán+0");
				}
			} else {
				response.getWriter().write("Không tồn tại+0");
			}
		}
	}

	// lấy bảng phiếu nhập
	public static ArrayList<String> viewReceivedNotes(
			Triplet<ArrayList<ReceivedNoteObject>, Integer, HashMap<Integer, Integer>> datas,
			Triplet<ReceivedNoteObject, Short, Byte> infos) {

		ArrayList<ReceivedNoteObject> items = datas.getValue0();
		int total = datas.getValue1();

		// thông tin phân trang
		short page = infos.getValue1();
		byte totalPerPage = infos.getValue2();
		ReceivedNoteObject similar = infos.getValue0();

		// lấy cấu trúc trình bày và danh sách phân trang
		ArrayList<String> tmp = new ArrayList<>();

		// danh sach
		StringBuilder list = new StringBuilder();
		list.append("<table class=\"table table-striped table-hover table-sm\">");
		list.append("<thead>");
		list.append("<tr>");
		list.append("<th scope=\"col\">ID</th>");
		list.append("<th scope=\"col\">Ngày nhập</th>");
		list.append("<th scope=\"col\">Giá trị</th>");
		list.append("<th scope=\"col\">ID Nhà cung cấp</th>");
		list.append("<th scope=\"col\">ID Nhân viên</th>");
		list.append("<th scope=\"col\">Xem</th>");
		list.append("</tr>");
		list.append("</thead>");

		list.append("<tbody>");
		items.forEach(item -> {
			list.append("<tr class=\"align-items-center\">");
			list.append("<th scope=\"row\" class=\"align-middle\">" + item.getReceived_note_id() + "</th>");
			list.append("<td class=\"align-middle\">" + item.getReceived_note_date() + "</td>");
			list.append("<td class=\"align-middle\">"
					+ Utilities_Helper.formatNumber((int) item.getReceived_note_total()) + "đ</td>");
			list.append("<td class=\"align-middle\">" + item.getSupplier_id() + "</td>");
			list.append("<td class=\"align-middle\">" + item.getUser_id() + "</td>");
			list.append("<td><a href=\"/shop360/received/details?id=" + item.getReceived_note_id()
					+ "\" class=\"btn btn-outline-primary btn-sm\" > <i class=\"bi bi-eye-fill\"></i> </a></td>");
			list.append("</tr>");
		});
		list.append("</tbody>");
		list.append("</table>");

		String key = similar.getUser_id() != null ? similar.getUser_id() + "" : null;
		// phân trang
		list.append(ReceivedNotes.getPaging("/shop360/notes/received", key, total, page, totalPerPage));
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
			key = "&emp=" + key;
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

		StringBuilder tmp = new StringBuilder();// tạo cấu trúc hoàn chỉnh
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
