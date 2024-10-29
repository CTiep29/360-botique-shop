package views;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.javatuples.Quartet;
import org.javatuples.Triplet;

import enums.EDIT_TYPE;
import enums.PRO_ORDER;
import functionimpl.CategoryFunctionImpl;
import libraries.Utilities;
import libraries.Utilities_Date;
import libraries.Utilities_Helper;
import objectcontrol.ProductControl;
import objectfuctions.CategoryFunction;
import objects.CategoryObject;
import objects.ProductObject;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class ProductView
 */
@WebServlet("/products")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class Products extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Products() {
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
			viewPage(request, response, user);
		}
	}

	protected void viewPage(HttpServletRequest request, HttpServletResponse response, UserObject user)
			throws ServletException, IOException {
		response.setContentType(CONTENT_TYPE);
		// Tìm bộ quản lí kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");

		ProductControl pc = new ProductControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", pc.getCP());
		}
		// tìm từ khóa
		String key = request.getParameter("keyword");
		String saveKey = (key != null && !key.equalsIgnoreCase("")) ? key.trim() : "";
		if (saveKey.equalsIgnoreCase("Nam")) {
			saveKey = "1";
		}
		if (saveKey.equalsIgnoreCase("Nữ")) {
			saveKey = "0";
		}
		// tạo đối tượng lưu trữ thông tin bộ lọc
		ProductObject similar = new ProductObject();
		similar.setProduct_id(10);

		String trash = request.getParameter("trash");
		if (trash != null) {
			similar.setProduct_deleted(1);
		}
		similar.setProduct_name(saveKey);
		short page = Utilities.getPageParam(request, "p"); // lấy tham số phân trang

		String cateId = request.getParameter("categoryid");
		if (cateId != null) {
			similar.setCategory_id(Integer. parseInt(cateId));
		}
		// lấy cấu trúc
		Quartet<ProductObject, Short, Byte, PRO_ORDER> infos = new Quartet<>(similar, page, (byte) 10, PRO_ORDER.NAME);
		ArrayList<String> viewPro = pc.viewProducts(infos, user);
		pc.releaseConnection();

		// tạo đối tượng xuất nội dung
		PrintWriter out = response.getWriter();
		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request
				.getRequestDispatcher("/header?pos=product" + (trash == null ? "list" : "trash"));
		if (header != null) {
			header.include(request, response);
		}

		out.append("<main id=\"main\" class=\"main\">");

		out.append("<div class=\"pagetitle d-flex\">");
		out.append("<nav class=\"ms-auto\">");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/dashboard\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Sản phẩm</li>");
		out.append("<li class=\"breadcrumb-item active\">Danh sách</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");

		out.append("<section class=\"section\">");
		out.append("<div class=\"row\">");
		out.append("<div class=\"col-lg-12\">");

		out.append("<div class=\"card\">");
		out.append("<div class=\"card-body\">");
		if (user.getUser_role() == 1) {
			out.append(
					"<button type=\"button\" class=\"btn btn-primary btnthem mt-2\" data-bs-toggle=\"modal\" data-bs-target=\"#addProduct\">");
			out.append("<i class=\"bi bi-person-add\"></i> Thêm mới");
			out.append("</button>");
		}
		out.append(
				"<div class=\"modal fade\" id=\"addProduct\" data-bs-backdrop=\"static\" data-bs-keyboard=\"false\" tabindex=\"-1\" aria-labelledby=\"addProductLabel\" aria-hidden=\"true\">");
		out.append("<div class=\"modal-dialog modal-lg\">");
		out.append("<div class=\"modal-content\">");
		out.append(
				"<form method=\"post\" action=\"/shop360/products\" enctype=\"multipart/form-data\" class=\"needs-validation\" novalidate>");
		out.append("<div class=\"modal-header text-bg-primary\">");
		out.append(
				"<h1 class=\"modal-title fs-5\" id=\"addProductLabel\"><i class=\"bi bi-person-plus\"></i> Thêm sản phẩm</h1>");
		out.append(
				"<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
		out.append("</div>");
		out.append("<div class=\"modal-body\">");

		out.append("<div class=\"row mb-3\">");
		out.append("<div class=\"col-sm-7\">");
		out.append("<label for=\"product_name\" class=\"form-label\">Tên sản phẩm</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"product_name\" name=\"txtName\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin tên sản phẩm</div>");
		out.append("</div>");

		out.append("<div class=\"col-sm-5\">");

		out.append("<label for=\"product_sex\" class=\"form-label\">Danh mục</label>");
		out.append("<select class=\"form-select\" id=\"category_id\" name=\"slcCategory\">");
		CategoryFunction cf = new CategoryFunctionImpl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", cf.getCP());
		}
		ArrayList<CategoryObject> cateList = cf.getAllCategory();
		cf.releaseConnection();
		cateList.forEach(a -> {
			out.append("<option value=\"" + a.category_id + "\">" + a.category_name + "</option>");
		});
		out.append("</select>");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"row mb-3\">");

		out.append("<div class=\"col-sm-4\">");
		out.append("<label for=\"product_unit\" class=\"form-label\">Đơn vị</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"product_unit\" name=\"txtUnit\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin đơn vị sản phẩm</div>");
		out.append("</div>");

		out.append("<div class=\"col-sm-4\">");
		out.append("<label for=\"product_price\" class=\"form-label\">Giá</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"product_price\" name=\"txtPrice\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin giá sản phẩm</div>");
		out.append("</div>");

		out.append("<div class=\"col-sm-4\">");
		out.append("<label for=\"profileImage\" class=\"col-form-label\">Hình ảnh</label>");
		out.append("<div>");
		out.append("<input type=\"file\" class=\"form-control\" id=\"image\" name=\"image\" accept=\"image/*\">");
		out.append("</div>");
		out.append("</div>");

		out.append("</div>");

		out.append("<div class=\"row mb-3\">");

		out.append("<div class=\"col-sm-4\">");

		out.append("<label for=\"product_sex\" class=\"form-label\">Giới tính</label>");
		out.append("<select class=\"form-select\" id=\"product_sex\" name=\"slcSex\">");
		out.append("<option value=\"1\">Nam</option>");
		out.append("<option value=\"0\">Nữ</option>");
		out.append("</select>");

		out.append("<div class=\"invalid-feedback\" > Xác định giới tính </div>");
		out.append("</div>");

		out.append("<div class=\"col-sm-4\">");
		out.append("<label for=\"product_size\" class=\"form-label\">Kích cỡ</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"product_size\" name=\"txtSize\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin kích cỡ sản phẩm</div>");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"row mb-3\">");

		out.append("<div>");
		out.append("<label for=\"product_description\" class=\"form-label\">Mô tả sản phẩm</label>");
		out.append(
				"<textarea class=\"form-control\" id=\"product_description\" name=\"txtDes\" required rows=\"4\"></textarea>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin mô tả sản phẩm</div>");
		out.append("</div>");
		out.append("</div>");

		out.append("</div>");
		out.append("<div class=\"modal-footer\">");
		out.append(
				"<button type=\"submit\" class=\"btn btn-primary\"><i class=\"bi bi-person-plus-fill\"></i>Thêm mới</button>");
		out.append("<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Close</button>");
		out.append("</div>");
		out.append("</form>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append(viewPro.get(0));
		out.append("</div>");// card-body
		out.append("</div>");// card

		out.append("</div>");// col-lg-12

		out.append("</div>");
		out.append("</section>");
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
		request.setCharacterEncoding("utf-8");
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		ProductControl pc = new ProductControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", pc.getCP());
		}

		String delIdString = request.getParameter("delid");
		String resIdString = request.getParameter("resid");
		ProductObject nPro = new ProductObject();
		if (delIdString != null) {
			int delId = Integer.parseInt(delIdString);
			nPro.setProduct_id(delId);
			boolean result = pc.editProduct(nPro, EDIT_TYPE.TRASH);
			pc.releaseConnection();
			if (result) {
				response.sendRedirect("/shop360/products");
			} else {
				response.sendRedirect("/shop360/products?err=notok");
			}
		} else if (resIdString != null) {
			int resId = Integer.parseInt(resIdString);
			nPro.setProduct_id(resId);
			boolean result = pc.editProduct(nPro, EDIT_TYPE.RESTORE);
			pc.releaseConnection();
			// Trả kết quả
			if (result) {
				response.sendRedirect("/shop360/products?view=trash");
			} else {
				response.sendRedirect("/shop360/products?view=trash&?err=notok");
			}
		} else {
			String date = Utilities_Date.getDate();
			String name = request.getParameter("txtName");
			System.out.println(name);
			String des = request.getParameter("txtDes");
			String size = request.getParameter("txtSize");
			String unit = request.getParameter("txtUnit");
			int sex = Integer.parseInt(request.getParameter("slcSex"));
			int price = Integer.parseInt(request.getParameter("txtPrice"));
			int category_id = Integer.parseInt(request.getParameter("slcCategory"));
			nPro.setProduct_name(name);
			nPro.setProduct_description(des);
			nPro.setProduct_size(size);
			nPro.setProduct_unit(unit);
			nPro.setProduct_price(price);
			nPro.setProduct_created_date(date);
			nPro.setProduct_last_modified(date);
			nPro.setProduct_quantity(0);
			nPro.setProduct_sold(0);
			nPro.setProduct_deleted(0);
			nPro.setCategory_id(category_id);
			nPro.setProduct_sex(sex);
			Part part = request.getPart("image");
			String realPath = request.getServletContext().getRealPath("img/products");
			String fileName = Path.of(part.getSubmittedFileName()).getFileName().toString();
			if (!Files.exists(Path.of(realPath))) {
				Files.createDirectory(Path.of(realPath));
			}
			String filePath = realPath + File.separator + fileName;
			try (InputStream inputStream = part.getInputStream()) {
				Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
			}
			nPro.setProduct_image(fileName);
			boolean result = pc.addProduct(nPro);
			pc.releaseConnection();
			// Trả kết quả
			if (result) {
				response.sendRedirect("/shop360/products");
			} else {
				response.sendRedirect("/shop360/products?err=notok");
			}
		}
	}

	public static void generateDeleteModal(StringBuilder out, ProductObject item) {
		out.append("<form action=\"/shop360/products?delid=" + item.getProduct_id() + "\" method=\"post\">");
		out.append("<div class=\"modal fade\" id=\"delProduct" + item.getProduct_id()
				+ "\" tabindex=\"-1\" aria-labelledby=\"delProductLabel" + item.getProduct_id()
				+ "\" aria-hidden=\"true\">");
		out.append("<div class=\"modal-dialog\">");
		out.append("<div class=\"modal-content\">");
		out.append("<div class=\"modal-header\">");
		out.append("<h5 class=\"modal-title\" id=\"delProductLabel" + item.getProduct_id() + "\">Xóa sản phẩm</h5>");
		out.append(
				"<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
		out.append("</div>");
		out.append("<div class=\"modal-body\">");
		out.append("Bạn có chắc chắn muốn xóa sản phẩm này?");
		out.append("</div>");
		out.append("<div class=\"modal-footer\">");
		out.append("<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Hủy</button>");
		out.append("<button type=\"submit\" class=\"btn btn-danger\">Xóa</button>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</form>");
	}

	public static ArrayList<String> viewProducts(
			Triplet<ArrayList<ProductObject>, Integer, HashMap<Integer, Integer>> datas,
			Quartet<ProductObject, Short, Byte, PRO_ORDER> infos, UserObject user) {

		ArrayList<ProductObject> items = datas.getValue0();
		int total = datas.getValue1();
		HashMap<Integer, Integer> days = datas.getValue2();
		// thông tin phân trang
		short page = infos.getValue1();
		byte totalPerPage = infos.getValue2();
		ProductObject similar = infos.getValue0();

		// lấy cấu trúc trình bày và danh sách phân trang
		ArrayList<String> tmp = new ArrayList<>();
		StringBuilder list = new StringBuilder();
		list.append("<table class=\"table table-striped table-hover table-sm \">");
		list.append("<thead>");
		list.append("<tr>");
		list.append("<th scope=\"col\">Tên sản phẩm</th>");
		list.append("<th scope=\"col\">Kích cỡ</th>");
		list.append("<th scope=\"col\">Giới tính</th>");
		list.append("<th scope=\"col\">Giá</th>");
		list.append("<th scope=\"col\" colspan=\"3\" >Thực hiện</th>");
		list.append("</tr>");
		list.append("</thead>");

		list.append("<tbody>");
		items.forEach(item -> {
			Integer daysValue = days.get(item.getProduct_id());
			if (daysValue != null && daysValue.intValue() <= 2) {

				list.append("<tr class=\"align-items-center\">");
			} else {
				list.append("<tr class=\"align-items-center\" >");
			}
			int sex = item.getProduct_sex();
			String value = sex == 1 ? "Nam" : "Nữ";
			int price = item.getProduct_price();
			list.append("<td class=\"align-middle\">" + item.getProduct_name() + "</td>");
			list.append("<td class=\"align-middle\">" + item.getProduct_size() + "</td>");
			list.append("<td class=\"align-middle\">" + value + "</td>");
			list.append("<td class=\"align-middle\">" + Utilities_Helper.formatNumber(price) + "đ</td>");
			list.append("<td class=\"align-middle\"><a href=\"/shop360/product/details?id=" + item.getProduct_id()
					+ "&t=over\"class=\"btn btn-outline-primary btn-sm\" > <i class=\"bi bi-eye-fill\"></i> </a></td>");
			if (user.getUser_role() == 1) {
				list.append(
						"<td class=\"align-middle\"><a href=\"#\" class=\"btn btn-danger btn-sm\" data-bs-toggle=\"modal\"  data-bs-target=\"#delProduct"
								+ item.getProduct_id() + "\" > <i class=\"bi bi-trash3\"></i></a></td>");
				generateDeleteModal(list, item);
			}
			list.append("</tr>");
		});

		list.append("</tbody>");
		list.append("</table>");
		String key = similar.getProduct_name();
		list.append(Products.getPaging("/shop360/products", key, total, page, totalPerPage));
		tmp.add(list.toString());
		return tmp;
	}

	public static void generateRestoreModal(StringBuilder out, ProductObject item) {
		out.append("<form action=\"/shop360/products?resid=" + item.getProduct_id() + "\" method=\"post\">");
		out.append("<div class=\"modal fade\" id=\"restoreProduct" + item.getProduct_id()
				+ "\" tabindex=\"-1\" aria-labelledby=\"resProductLabel" + item.getProduct_id()
				+ "\" aria-hidden=\"true\">");
		out.append("<div class=\"modal-dialog\">");
		out.append("<div class=\"modal-content\">");
		out.append("<div class=\"modal-header\">");
		out.append(
				"<h5 class=\"modal-title\" id=\"resProductLabel" + item.getProduct_id() + "\">Khôi phục sản phẩm</h5>");
		out.append(
				"<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
		out.append("</div>");
		out.append("<div class=\"modal-body\">");
		out.append("Bạn có chắc chắn muốn khôi phục sản phẩm này?");
		out.append("</div>");
		out.append("<div class=\"modal-footer\">");
		out.append("<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Hủy</button>");
		out.append("<button type=\"submit\" class=\"btn btn-success\">Khôi phục</button>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</form>");
	}

	public static ArrayList<String> viewDeletedProducts(
			Triplet<ArrayList<ProductObject>, Integer, HashMap<Integer, Integer>> datas,
			Quartet<ProductObject, Short, Byte, PRO_ORDER> infos, UserObject user) {
		// bóc tách dữ liệu để xử lý
		ArrayList<ProductObject> items = datas.getValue0();
		int total = datas.getValue1();
		// thông tin phân trang
		short page = infos.getValue1();
		byte totalPerPage = infos.getValue2();
		ProductObject similar = infos.getValue0();
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
		list.append("<th scope=\"col\">Ngày xoá</th>");
		list.append("<th scope=\"col\" colspan=\"2\">Thực hiện</th>");
		list.append("</tr>");
		list.append("</thead>");

		list.append("<tbody>");

		items.forEach(item -> {
			int sex = item.getProduct_sex();
			String value = sex == 1 ? "Nam" : "Nữ";
			list.append("<tr>");
			list.append("<th scope=\"row\" class=\"align-middle\">" + item.getProduct_id() + "</th>");
			list.append("<td class=\"align-middle\">" + item.getProduct_name() + "</td>");
			list.append("<td class=\"align-middle\">" + item.getProduct_size() + "</td>");
			list.append("<td class=\"align-middle\">" + value + "</td>");
			list.append("<td class=\"align-middle\">" + item.getProduct_last_modified() + "</td>");
			list.append("<td class=\"align-middle\"><a href=\"/shop360/product/details?id=" + item.getProduct_id()
					+ "&res\"class=\"btn btn-outline-primary btn-sm\" > <i class=\"bi bi-eye-fill\"></i> </a></td>");
			if (user.getUser_role() == 1) {
				list.append(
						"<td class=\"align-middle\"><a href=\"#\" class=\"btn btn-success btn-sm\" data-bs-toggle=\"modal\"  data-bs-target=\"#restoreProduct"
								+ item.getProduct_id() + "\" > <i class=\"bi bi-reply\"></i></a></td>");
				generateRestoreModal(list, item);
			}
			list.append("</tr>");
		});

		list.append("</tbody>");
		list.append("</table>");
		String key = similar.getProduct_name();
		// phân trang
		list.append(Products.getPaging("/shop360/products", key, total, page, totalPerPage));

		tmp.add(list.toString());// danh sách

		return tmp;
	}

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
			key = "&keyword=" + key;
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
