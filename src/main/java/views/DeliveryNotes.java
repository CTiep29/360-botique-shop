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
import objectcontrol.DeliveryNoteControl;
import objectcontrol.ProductControl;
import objectfuctions.UserFunction;
import objects.DeliveryNoteObject;
import objects.DeliveryObject;
import objects.ProductObject;
import objects.StoreObject;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class DeliveryNoteView
 */
@WebServlet("/notes/delivery")
public class DeliveryNotes extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeliveryNotes() {
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
			viewDeliveryNotes(request, response, user);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void viewDeliveryNotes(HttpServletRequest request, HttpServletResponse response, UserObject user)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);
		// Tìm bộ quản lí kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		boolean isManager = user.getUser_role() == 1;

		DeliveryNoteControl dnc = new DeliveryNoteControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", dnc.getCP());
		}

		// tạo đối tượng lưu trữ thông tin bộ lọc
		DeliveryNoteObject similar = new DeliveryNoteObject();
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
			similar.setDelivery_note_date(date);
		}

		PrintWriter out = response.getWriter();
		String err = request.getParameter("err");
		if (null != err) {
			out.append("<script>alert(\"Thêm phiếu xuất không thành công\")</script>");
		}

		short page = Utilities.getPageParam(request, "p"); // lấy tham số phân trang
		// lấy cấu trúc
		Triplet<DeliveryNoteObject, Short, Byte> infos = new Triplet<>(similar, page, (byte) 10);
		ArrayList<StoreObject> stores = dnc.getStores();
		ArrayList<String> view = dnc.viewDeliveryNotes(infos);
		dnc.releaseConnection();
		UserFunction u = new UserFunctionImpl(cp);
		ArrayList<UserObject> staffs = u.getStaffs();
		u.releaseConnection();

		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header?pos=deliverynote");
		if (header != null) {
			header.include(request, response);
		}

		// Nội dung trang
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex\">");
		out.append("<nav class=\"ms-auto\">");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/dashboard\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Phiếu</li>");
		out.append("<li class=\"breadcrumb-item active\">Xuất</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");

		out.append("<div class=\"card\">");
		out.append("<div class=\"card-body\">");

		out.append("<div class=\"row my-2 align-items-center\">");
		// nút thêm phiếu xuất
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
				"<form method=\"post\" action=\"/shop360/notes/delivery\" id=\"add\" class=\"needs-validation\" novalidate>");
		out.append("<div class=\"modal-header text-bg-primary\">");
		out.append(
				"<h1 class=\"modal-title fs-5\" id=\"addProductLabel\"><i class=\"bi bi-truck\"></i> Xuất hàng</h1>");
		out.append(
				"<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
		out.append("</div>");
		out.append("<div class=\"modal-body\">");
		out.append("<div class=\"row mb-3\">");
		out.append("<div class=\"col-sm-12\">");
		out.append("<label for=\"store_id\" class=\"form-label\">Cửa hàng</label>");
		out.append("<select class=\"form-select\" id=\"store_id\" name=\"slcStore\" required>");
		out.append("<option value=\"\">Chọn cửa hàng</option>");
		stores.forEach(store -> {
			out.append("<option value=\"" + store.getStore_id() + "\">" + store.getStore_name() + "</option>");
		});
		out.append("</select>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin cửa hàng</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("<div class=\"row mb-3\">");
		out.append("<div class=\"col-sm-4\">");
		out.append(
				"<button id=\"addInputBtn\" type=\"button\" class=\"btn btn-success\"><i class=\"bi-plus-circle\"></i> Thêm sản phẩm</button>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin cửa hàng</div>");
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
				"<input type=\"text\" class=\"form-control\" id=\"product_quantity0\" name=\"txtProductQuantity0\" onkeyup=\"checkValues()\" required readonly>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"delivery_quantity0\" class=\"form-label\">SL xuất</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"delivery_quantity0\" name=\"txtQuantity0\" onkeyup=\"checkValues()\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin số lượng sản phẩm</div>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"delivery_price0\" class=\"form-label\">Giá xuất</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"delivery_price0\" name=\"txtPrice0\" required>");
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
		out.append("xhttp.open(\"POST\", \"/shop360/notes/delivery\", true);");
		out.append("xhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");");
		out.append("xhttp.send(\"inputValue=\" + value);");
		out.append("});");
		out.append("}");
		out.append("});");

		// thêm dòng nhập sản phẩm xuất
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
				"<input type=\"text\" class=\"form-control\" id=\"product_quantity${i}\" name=\"txtProductQuantity${i}\" onkeyup=\"checkValues()\" required readonly>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"delivery_quantity${i}\" class=\"form-label\">SL xuất</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"delivery_quantity${i}\" name=\"txtQuantity${i}\" onkeyup=\"checkValues()\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin số lượng sản phẩm</div>");
		out.append("</div>");
		out.append("<div class=\"col-sm-2\">");
		out.append("<label for=\"delivery_price${i}\" class=\"form-label\">Giá xuất</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"delivery_price${i}\" name=\"txtPrice${i}\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin giá sản phẩm</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("`;");
		out.append("var lastRow = addForm.querySelector(\".row:last-of-type\");");
		out.append("lastRow.insertAdjacentHTML(\"afterend\", newElement);");
		out.append("});");
		out.append("});");

		if (isManager) {
			// bắt sự kiện input emp thay đổi
			out.append("document.getElementById(\"emp\").addEventListener(\"change\", function() {");
			out.append("var emp = this.value;");
			out.append("var url = \"/shop360/notes/delivery?emp=\" + encodeURIComponent(emp);");
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

		// bắt sự kiện input date thay đổi
		out.append("document.getElementById(\"date\").addEventListener(\"change\", function() {");
		out.append("var date = this.value;");
		out.append("var url = \"/shop360/notes/delivery?date=\" + encodeURIComponent(date);");
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

		// kiểm tra số lượng xuất nhỏ hơn số lượng sản phẩm trong kho
		out.append("function checkValues() {");
		out.append("let deliveryInputs = document.querySelectorAll(");
		out.append("\'input[id^=\"delivery_quantity\"]\'");
		out.append(");");
		out.append("");
		out.append("let productInputs = document.querySelectorAll(");
		out.append("\'input[id^=\"product_quantity\"]\'");
		out.append(");");
		out.append("");
		out.append("if (deliveryInputs.length === productInputs.length) {");
		out.append("for (let i = 0; i < deliveryInputs.length; i++) {");
		out.append("let deliveryValue = parseInt(deliveryInputs[i].value);");
		out.append("let productValue = parseInt(productInputs[i].value);");
		out.append("");
		out.append("if (productValue < deliveryValue) {");
		out.append("alert(\"Vui lòng nhập số lượng xuất bé hơn số lượng còn trong kho!!!\");");
		out.append("}");
		out.append("}");
		out.append("}");
		out.append("}");

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
			DeliveryNoteControl dnc = new DeliveryNoteControl(cp);
			if (cp == null) {
				getServletContext().setAttribute("CPool", dnc.getCP());
			}
			int store_id = Utilities.getIntParam(request, "slcStore");
			int counter = Integer.parseInt(request.getParameter("counter"));
			DeliveryNoteObject n = new DeliveryNoteObject();
			String today = Utilities_Date.getDate();
			n.setDelivery_note_date(today);
			n.setStore_id((short) store_id);
			UserObject user = (UserObject) request.getSession().getAttribute("user");
			n.setUser_id(user.getUser_id());
			ArrayList<DeliveryObject> myList = new ArrayList<>();
			for (int i = 0; i <= counter; i++) {
				int product_id = Integer.parseInt(request.getParameter("txtProductID" + i));
				int product_quantity = Integer.parseInt(request.getParameter("txtProductQuantity" + i));
				int quantity = Integer.parseInt(request.getParameter("txtQuantity" + i));
				int price = Integer.parseInt(request.getParameter("txtPrice" + i));

				if (quantity <= product_quantity) {
					DeliveryObject nd = new DeliveryObject();
					nd.setProduct_id(product_id);
					nd.setDelivery_quantity(quantity);
					nd.setDelivery_price(price);
					myList.add(nd);
				}
			}

			// Thực hiện thêm mới
			if (!myList.isEmpty()) {
				boolean result = dnc.addDeliveryNote(n, myList);
				dnc.releaseConnection();
				if (result) {
					response.sendRedirect("/shop360/notes/delivery");
				} else {
					response.sendRedirect("/shop360/notes/delivery?err=1");
				}
			} else {
				response.sendRedirect("/shop360/notes/delivery?err=1");
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

	// lấy bảng phiếu xuất
	public static ArrayList<String> viewDeliveryNotes(
			Triplet<ArrayList<DeliveryNoteObject>, Integer, HashMap<Integer, Integer>> datas,
			Triplet<DeliveryNoteObject, Short, Byte> infos) {

		ArrayList<DeliveryNoteObject> items = datas.getValue0();
		int total = datas.getValue1();
		// HashMap<Integer, Integer> days = datas.getValue2();

		// thông tin phân trang
		short page = infos.getValue1();
		byte totalPerPage = infos.getValue2();
		DeliveryNoteObject similar = infos.getValue0();

		// lấy cấu trúc trình bày và danh sách phân trang
		ArrayList<String> tmp = new ArrayList<>();

		// danh sách
		StringBuilder list = new StringBuilder();
		list.append("<table class=\"table table-striped table-hover table-sm\">");
		list.append("<thead>");
		list.append("<tr>");
		list.append("<th scope=\"col\">ID</th>");
		list.append("<th scope=\"col\">Ngày xuất</th>");
		list.append("<th scope=\"col\">Giá trị</th>");
		list.append("<th scope=\"col\">ID Cửa hàng</th>");
		list.append("<th scope=\"col\">ID Nhân viên</th>");
		list.append("<th scope=\"col\">Xem</th>");
		list.append("</tr>");
		list.append("</thead>");
		list.append("<tbody>");

		items.forEach(item -> {
			list.append("<tr class=\"align-items-center\">");
			list.append("<th scope=\"row\" class=\"align-middle\">" + item.getDelivery_note_id() + "</th>");
			list.append("<td class=\"align-middle\">" + item.getDelivery_note_date() + "</td>");
			list.append("<td class=\"align-middle\">"
					+ Utilities_Helper.formatNumber((int) item.getDelivery_note_value()) + "đ</td>");
			list.append("<td class=\"align-middle\">" + item.getStore_id() + "</td>");
			list.append("<td class=\"align-middle\">" + item.getUser_id() + "</td>");
			list.append("<td><a href=\"/shop360/delivery/details?id=" + item.getDelivery_note_id()
					+ "\" class=\"btn btn-outline-primary btn-sm\" > <i class=\"bi bi-eye-fill\"></i> </a></td>");
		});

		list.append("</tbody>");
		list.append("</table>");

		String key = similar.getUser_id() != null ? similar.getUser_id() + "" : null;
		// phân trang
		list.append(DeliveryNotes.getPaging("/shop360/notes/delivery", key, total, page, totalPerPage));

		// danh sách
		tmp.add(list.toString());
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
