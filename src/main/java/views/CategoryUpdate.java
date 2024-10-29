package views;

import java.io.*;
import java.nio.file.Paths;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import functionimpl.CategoryFunctionImpl;
import libraries.*;
import objectfuctions.CategoryFunction;
import objects.CategoryObject;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class CategoryUpdate
 */
@WebServlet("/category/update")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50 // 50MB
)

public class CategoryUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset = utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CategoryUpdate() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		UserObject user = (UserObject) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect("/shop360/user/login");
		} else {
			if (user.getUser_role() == 1) {
				view(request, response);
			} else {
				response.sendRedirect("/shop360/user/notfound");
			}
		}
	}

	protected void view(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(CONTENT_TYPE);
		// Lấy id để sửa
		int categoryId = Utilities.getIntParam(request, "id");

		// Tìm bộ quản lý kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		// Tạo đối tượng thực thi chức năng
		CategoryFunction uf = new CategoryFunctionImpl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", uf.getCP());
		}
		// Lấy thông tin danh mục
		CategoryObject category = uf.getCategory(categoryId);
		uf.releaseConnection();
		// Lấy ngày hiện tại
		String currentDate = Utilities_Date.getDate();
		request.setAttribute("category", category);

		// Tham chiếu Servlet header
		RequestDispatcher h = request.getRequestDispatcher("/header?pos=cate");
		if (h != null) {
			h.include(request, response);
		}

		PrintWriter out = response.getWriter();
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex\">");
		out.append("<h1></h1>");
		out.append("<nav class=\"ms-auto\" >");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/dashboard\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item\">Danh mục</li>");
		out.append("<li class=\"breadcrumb-item active\">Danh sách</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");

		out.append("<section class=\"section\">");
		out.append("<div class=\"row\">");
		out.append("<div class=\"col-lg-12\">");

		out.append("<div class=\"card\">");
		out.append("<div class=\"card-header text-bg-primary text-white\">");
		out.append(
				"<h1 class=\"modal-title fs-5\" id=\"addCategoryLabel\"><i class=\"bi bi-pencil-square\"></i> Sửa danh mục </h1>");
		out.append("</div>");
		out.append("<div class=\"card-body\">");

		out.append(
				"<form method=\"post\" action=\"/shop360/category/update\" class=\"needs-validation\" enctype=\"multipart/form-data\" novalidate>");
		out.append("<input type=\"hidden\" name=\"category_id\" value=\"" + category.getCategory_id() + "\">");
		out.append("<div class=\"mb-3\">");
		out.append("<label for=\"category_name\" class=\"form-label\">Tên danh mục</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"category_name\" name=\"txtName\" value=\""
				+ category.getCategory_name() + "\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin tên danh mục</div>");
		out.append("</div>");

		out.append("<div class=\"mb-3\">");
		out.append("<label for=\"category_description\" class=\"form-label\">Mô tả danh mục</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"category_description\" name=\"txtDes\" value=\""
				+ category.getCategory_description() + "\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin mô tả danh mục</div>");
		out.append("</div>");
		// Phần thêm input chọn ảnh
		out.append("<div class=\"row mb-3\">");
		out.append("<div class=\"col-md-4 mb-3\">");
		out.append("<label for=\"category_image\" class=\"form-label\">Ảnh danh mục</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"category_image\" name=\"txtImage\"  value=\""
				+ category.getCategory_image() + "\" readonly required>");
		out.append("</div>");
		out.append("<div class=\"col-md-8 mb-3\">");
		out.append("<label for=\"category_imageNew\" class=\"form-label\">Chọn ảnh mới</label>");
		out.append("<input type=\"file\" class=\"form-control\" id=\"category_imageNew\" name=\"txtImageNew\">");
		out.append("</div>");
		out.append("</div>");

		out.append("<div class=\"row mb-3\">");

		out.append("<div class=\"col-md-6 mb-3\">");
		out.append("<label for=\"created_date\" class=\"form-label\">Ngày tạo</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"created_date\" name=\"txtCreated_date\" value=\""
				+ category.getCategory_created_date() + "\" readonly required>");
		out.append("</div>");

		out.append("<div class=\"col-md-6 mb-3\">");
		out.append("<label for=\"updated_date\" class=\"form-label\">Ngày cập nhật</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"updated_date\" name=\"txtUpdated_date\" value=\""
				+ currentDate + "\" readonly required>");
		out.append("</div>");

		out.append("</div>"); // End of row

		out.append("<div class=\"d-flex justify-content-end\">");
		out.append(
				"<button type=\"submit\" class=\"btn btn-primary me-2\"><i class=\"bi bi-save\"></i> Lưu thay đổi</button>");
		out.append("<a href=\"/shop360/categories\" class=\"btn btn-secondary\">Hủy bỏ</a>");
		out.append("</div>");
		out.append("</form>");

		out.append("</div>");
		out.append("</div>");

		out.append("</div>");
		out.append("</div>");
		out.append("</section>");

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
		request.setCharacterEncoding("utf-8");

		// Tìm bộ quản lý kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		// Tạo đối tượng thực thi chức năng
		CategoryFunction uf = new CategoryFunctionImpl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", uf.getCP());
		}

		// Lấy thông tin trên giao diện
		int categoryId = Integer.parseInt(request.getParameter("category_id"));
		String name = request.getParameter("txtName");
		String description = request.getParameter("txtDes");
		String oldImage = request.getParameter("txtImage");
		String createdDate = request.getParameter("txtCreated_date");
		String updatedDate = request.getParameter("txtUpdated_date");

		// Xử lý tệp ảnh mới
		Part filePart = request.getPart("txtImageNew");
		String fileName = null;
		if (filePart != null && filePart.getSize() > 0) {
			fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
			String savePath = getServletContext().getRealPath("/") + "/img/categories" + File.separator + fileName;
			filePart.write(savePath);
		} else {
			fileName = oldImage;
		}

		// Tạo đối tượng lưu trữ mới
		CategoryObject cate = new CategoryObject();
		cate.setCategory_id(categoryId);
		cate.setCategory_name(name);
		cate.setCategory_image(fileName);
		cate.setCategory_description(description);
		cate.setCategory_created_date(createdDate);
		cate.setCategory_last_modified(updatedDate);

		boolean result = uf.editCategory(cate);

		if (result) {
			response.sendRedirect("/shop360/categories?success=update");
		} else {
			response.sendRedirect("/shop360/categories?error=update");
		}
	}

}
