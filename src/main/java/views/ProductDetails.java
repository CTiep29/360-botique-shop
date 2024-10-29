package views;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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

import enums.EDIT_TYPE;
import functionimpl.CategoryFunctionImpl;
import libraries.Utilities_Date;
import libraries.Utilities_Helper;
import objectcontrol.ProductControl;
import objectfuctions.CategoryFunction;
import objects.CategoryObject;
import objects.ProductObject;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class profiles
 */
@WebServlet("/product/details")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class ProductDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset = utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProductDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UserObject user = (UserObject) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect("/shop360/user/login");
		} else {
			view(request, response, user);
		}
	}

	protected void view(HttpServletRequest request, HttpServletResponse response, UserObject user)
			throws ServletException, IOException {
		response.setContentType(CONTENT_TYPE);
		RequestDispatcher h = request.getRequestDispatcher("/header?pos=product");
		if (h != null) {
			h.include(request, response);
		}
		String tab = request.getParameter("t");
		HashMap<String, String> tab_active = new HashMap<>();
		HashMap<String, String> show_active = new HashMap<>();
		if (tab != null && (tab.equalsIgnoreCase("over") || tab.equalsIgnoreCase("edit"))) {
			tab_active.put(tab, "active");
			show_active.put(tab, "show active");
		} else {
			tab_active.put("over", "active");
			show_active.put("over", "show active");
		}
		int id = Integer.parseInt(request.getParameter("id"));
		ProductObject selectedProduct = null;
		if (id > 0) {
			// Tìm bộ quản lí kết nối
			ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
			ProductControl pc = new ProductControl(cp);
			if (cp == null) {
				getServletContext().setAttribute("CPool", pc.getCP());
			}
			selectedProduct = pc.getProductObject(id);
		}
		// Tìm bộ quản lý kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		// Tạo đối tượng thực thi chức năng
		ProductControl pc = new ProductControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", pc.getCP());
		}
		CategoryFunction cf = new CategoryFunctionImpl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", cf.getCP());
		}
		PrintWriter out = response.getWriter();
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex justify-content-between\">");
		out.append("<h1>Thông tin sản phẩm</h1>");
		out.append("<nav class\"ms-auto\">");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/dashboard\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Sản phẩm</li>");
		out.append("<li class=\"breadcrumb-item active\">Chi tiết</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");
		out.append("<section class=\"section profile\">");
		out.append("<div class=\"row\">");
		out.append("<div class=\"col-xl-3\">");
		out.append("<div class=\"card\">");
		out.append("<div class=\"card-body profile-card text-center p-3\">");
		out.append("<img class=\"d-inline-block rounded w-100\" src=\"/shop360/img/products/"
				+ selectedProduct.getProduct_image() + "\" alt=\"Profile\" >");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"col-xl-9\">");
		out.append("<div class=\"card\">");
		out.append("<div class=\"card-body pt-3\">");
		out.append("<!-- Bordered Tabs -->");
		out.append("<ul class=\"nav nav-tabs nav-tabs-bordered\">");

		out.append("<li class=\"nav-item\">");
		out.append("<button class=\"nav-link " + tab_active.getOrDefault("over", "")
				+ "\" data-bs-toggle=\"tab\" data-bs-target=\"#profile-overview\"><i class=\"bi bi-info-square\"></i> Chi tiết</button>");
		out.append("</li>");

		out.append("<li class=\"nav-item\">");
		if (user.getUser_role() == 1) {
			out.append("<button class=\"nav-link" + tab_active.getOrDefault("edit", "")
					+ "\" data-bs-toggle=\"tab\" data-bs-target=\"#profile-edit\"><i class=\"bi bi-pencil-square\"></i> Chỉnh sửa</button>");
			out.append("</li>");
		}
		out.append("</ul>");
		out.append("<div class=\"tab-content pt-2\">");

		out.append("<div class=\"tab-pane fade " + show_active.getOrDefault("over", "")
				+ " profile-overview\" id=\"profile-overview\">");

		out.append("<h5 class=\"card-title\">Tên sản phẩm</h5>");
		out.append("<p class=\"small fst-italic\">" + selectedProduct.getProduct_name() + "</p>");

		out.append("<h5 class=\"card-title\">Giới thiệu</h5>");
		out.append("<p class=\"small fst-italic\">" + selectedProduct.getProduct_description() + "</p>");

		out.append("<h5 class=\"card-title\">Chi tiết</h5>");

		out.append("<div class=\"row\">");
		out.append("<div class=\"col-lg-3 col-md-4 label \">Danh mục</div>");
		out.append("<div class=\"col-lg-9 col-md-8\">"
				+ cf.getCategory(selectedProduct.getCategory_id()).getCategory_name() + "</div>");
		out.append("</div>");

		out.append("<div class=\"row\">");
		out.append("<div class=\"col-lg-3 col-md-4 label\">Giới tính</div>");
		String sexString = (selectedProduct.getProduct_sex() == 1) ? "Nam" : "Nữ";
		out.append("<div class=\"col-lg-9 col-md-8\">" + sexString + "</div>");
		out.append("</div>");

		out.append("<div class=\"row\">");
		out.append("<div class=\"col-lg-3 col-md-4 label\">Đơn vị</div>");
		out.append("<div class=\"col-lg-9 col-md-8\">" + selectedProduct.getProduct_unit() + "</div>");
		out.append("</div>");

		out.append("<div class=\"row\">");
		out.append("<div class=\"col-lg-3 col-md-4 label\">Kích cỡ</div>");
		out.append("<div class=\"col-lg-9 col-md-8\">" + selectedProduct.getProduct_size() + "</div>");
		out.append("</div>");

		out.append("<div class=\"row\">");
		out.append("<div class=\"col-lg-3 col-md-4 label\">Giá</div>");
		out.append("<div class=\"col-lg-9 col-md-8\">" + Utilities_Helper.formatNumber(selectedProduct.getProduct_price()) + "đ</div>");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"tab-pane fade " + show_active.getOrDefault("edit", "")
				+ " profile-edit pt-3\" id=\"profile-edit\">");

		out.append("<!-- Profile Edit Form -->");
		out.append(
				"<form class=\"needs-validation\" method=\"post\" enctype=\"multipart/form-data\" action=\"/shop360/product/details?id="
						+ id + "\" novalidate>");
		out.append("<div class=\"row mb-3\">");

		out.append("<div class=\"col-6 \">");
		out.append("<label for=\"profileImage\" class=\"form-label\">Hình ảnh</label>");
		out.append("<input type=\"file\" class=\"form-control\" id=\"image\" name=\"image\" accept=\"image/*\">");
		out.append("</div>");

		out.append("<div class=\"col-3 \">");
		out.append("<label for=\"category_id\" class=\"form-label\">Danh mục</label>");
		out.append("<select class=\"form-select\" id=\"category_id\" name=\"slcCategory\">");

		ArrayList<CategoryObject> cateList = cf.getAllCategory();
		int categoryID = selectedProduct.getCategory_id();
		cateList.forEach(item -> {
			if (item.category_id == categoryID) {
				out.append("<option value=\"" + item.category_id + "\" selected>" + item.category_name + "</option>");
			} else {
				out.append("<option value=\"" + item.category_id + "\">" + item.category_name + "</option>");
			}
		});
		out.append("</select>");
		out.append("</div>");

		out.append("<div class=\"col-3 \">");
		out.append("<label for=\"product_sex\" class=\"form-label\">Giới tính</label>");
		out.append("<select class=\"form-select\" id=\"product_sex\" name=\"slcSex\">");
		int selectedSex = selectedProduct.getProduct_sex();
		if (selectedSex == 1) {
			out.append("<option value=\"1\" selected>Nam</option>");
			out.append("<option value=\"0\">Nữ</option>");
		} else {
			out.append("<option value=\"1\">Nam</option>");
			out.append("<option value=\"0\" selected>Nữ</option>");
		}
		out.append("</select>");
		out.append("</div>");

		out.append("</div>");

		out.append("<div class=\"row mb-3\">");
		out.append("<label for=\"productName\" class=\"col-md-4 col-lg-3 form-label\">Tên sản phẩm</label>");
		out.append("<div class=\"col-md-3 col-lg-9\">");
		out.append("<input name=\"txtName\" type=\"text\" class=\"form-control\" id=\"productName\" required value=\""
				+ selectedProduct.getProduct_name() + "\">");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"row mb-3\">");
		out.append("<label for=\"about\" class=\"col-md-4 col-lg-3 form-label\">Giới thiệu</label>");
		out.append("<div class=\"col-md-8 col-lg-9\">");
		out.append("<textarea name=\"txtDes\" class=\"form-control\" id=\"description\" style=\"height: 100px\">"
				+ selectedProduct.getProduct_description() + "</textarea>");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"row mb-3\">");
		out.append("<label for=\"size\" class=\"col-md-4 col-lg-3 form-label\">Kích cỡ</label>");
		out.append("<div class=\"col-md-8 col-lg-9\">");
		out.append("<input name=\"txtSize\" type=\"text\" class=\"form-control\" id=\"size\" value=\""
				+ selectedProduct.getProduct_size() + "\">");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"row mb-3\">");
		out.append("<label for=\"unit\" class=\"col-md-4 col-lg-3 form-label\">Đơn vị</label>");
		out.append("<div class=\"col-md-8 col-lg-9\">");
		out.append("<input name=\"txtUnit\" type=\"text\" class=\"form-control\" id=\"unit\" value=\""
				+ selectedProduct.getProduct_unit() + "\">");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"row mb-3\">");
		out.append("<label for=\"price\" class=\"col-md-4 col-lg-3 form-label\">Giá</label>");
		out.append("<div class=\"col-md-8 col-lg-9\">");
		out.append("<input name=\"txtPrice\" type=\"text\" class=\"form-control\" id=\"price\" value=\""
				+ selectedProduct.getProduct_price() + "\">");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"text-center\">");
		out.append(
				"<button type=\"submit\" class=\"btn btn-primary\"><i class=\"bi bi-save2\"></i> Lưu thay đổi</button>");
		out.append("</div>");
		out.append("</form><!-- End Profile Edit Form -->");
		out.append("</div>");
		out.append("</div><!-- End Bordered Tabs -->");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</section>");
		out.append("</main><!-- End #main -->");
		RequestDispatcher f = request.getRequestDispatcher("/footer");
		if (f != null) {
			f.include(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		// Tìm bộ quản lý kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		// Tạo đối tượng thực thi chức năng
		ProductControl pc = new ProductControl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", pc.getCP());
		}

		ProductObject nPro = new ProductObject();
		String date = Utilities_Date.getDate();
		int id = Integer.parseInt(request.getParameter("id"));
		String name = request.getParameter("txtName");
		String des = request.getParameter("txtDes");
		String size = request.getParameter("txtSize");
		String unit = request.getParameter("txtUnit");
		int sex = Integer.parseInt(request.getParameter("slcSex"));
		int price = Integer.parseInt(request.getParameter("txtPrice"));
		int category_id = Integer.parseInt(request.getParameter("slcCategory"));
		nPro.setProduct_id(id);
		nPro.setProduct_name(name);
		nPro.setProduct_description(des);
		nPro.setProduct_size(size);
		nPro.setProduct_unit(unit);
		nPro.setProduct_price(price);
		nPro.setCategory_id(category_id);
		nPro.setProduct_sex(sex);
		nPro.setProduct_last_modified(date);
		Part part = request.getPart("image");
		String oldFileName = pc.getProductObject(id).getProduct_image();
		String fileName = oldFileName;
		if (part != null && part.getSize() > 0) {
			String realPath = request.getServletContext().getRealPath("img/products");
			fileName = Path.of(part.getSubmittedFileName()).getFileName().toString();
			if (!fileName.equalsIgnoreCase(oldFileName)) {
				if (!Files.exists(Path.of(realPath))) {
					Files.createDirectory(Path.of(realPath));
				}
				part.write(realPath + "/" + fileName);
			}
		}
		nPro.setProduct_image(fileName);
		boolean result = pc.editProduct(nPro, EDIT_TYPE.NORMAL);
		pc.releaseConnection();
		// Trả kết quả
		if (result) {
			response.sendRedirect("/shop360/product/details?id=" + id);
		} else {
			response.sendRedirect("/shop360/product/details?id=" + id + "&?err=notok");
		}
	}
}
