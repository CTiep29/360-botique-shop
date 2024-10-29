package views;

import java.io.*;
import java.nio.file.Paths;

import util.*;
import objects.*;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import functionimpl.CategoryFunctionImpl;

import java.util.*;
import libraries.*;
import objectfuctions.CategoryFunction;

/**
 * Servlet implementation class View
 */
@WebServlet("/categories")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class Categories extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset = utf-8";
	private static final int RECORDS_PER_PAGE = 5;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Categories() {
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
			view(request, response, null, user);
		}
	}

	protected void view(HttpServletRequest request, HttpServletResponse response, CategoryObject category,
			UserObject user) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);
		boolean isManager = user.getUser_role() == 1;
		int page = 1;
		if (request.getParameter("page") != null)
			page = Integer.parseInt(request.getParameter("page"));

		// Tham chiếu Servlet header
		RequestDispatcher h = request.getRequestDispatcher("/header?pos=catelist");
		if (h != null) {
			h.include(request, response);
		}
		PrintWriter out = response.getWriter();
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex\">");
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
		out.append("<div class=\"card-body\">");
		if (isManager) {
			out.append(
					"<button type=\"button\" class=\"btn btn-primary btnthem mt-2\" data-bs-toggle=\"modal\" data-bs-target=\"#addCategory\">");
			out.append("<i class=\"bi bi-person-add\"></i> Thêm mới");
			out.append("</button>");
		}
		generateAddModal(out, category);
		// Tìm bộ quản lý kết nối
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		// Tạo đối tượng thực thi chức năng
		CategoryFunction cf = new CategoryFunctionImpl(cp);
		if (cp == null) {
			getServletContext().setAttribute("CPool", cf.getCP());
		}
		// Lấy danh sách hiển thị
		ArrayList<CategoryObject> list = cf.getCategoryObject(category, (page - 1) * RECORDS_PER_PAGE,
				(byte) RECORDS_PER_PAGE);
		int noOfRecords = cf.getNoOfRecords();
		int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / RECORDS_PER_PAGE);
		cf.releaseConnection();

		out.append("<table class=\"table table-striped table-hover table-sm\">");
		out.append("<thead>");
		out.append("<tr>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\">ID</th>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\">Tên danh mục</th>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\">Mô tả</th>");
		out.append("<th scope=\"col\" class=\"text-center py-2 px-3\">Ảnh danh mục</th>");
		if (isManager) {
			out.append("<th scope=\"col\" class=\"text-center py-2 px-3\"colspan=\"2\">Thực hiện</th>");
		}
		out.append("</tr>");
		out.append("</thead>");
		out.append("<tbody>");
		list.forEach(item -> {
			out.append("<tr>");
			out.append("<th class=\"text-center align-middle py-3 px-3\" scope=\"row\">" + item.getCategory_id()
					+ "</th>");
			out.append("<td class=\"text-center align-middle py-3 px-3 \"><a href=\"/shop360/products?categoryid="
					+ item.getCategory_id() + "\">" + item.getCategory_name() + "</td>");
			out.append("<td class=\"text-center align-middle py-3 px-3\">" + item.getCategory_description() + "</td>");
			out.append("<td class=\"text-center align-middle py-2 \"><img src=\"/shop360/img/categories/"
					+ item.getCategory_image()
					+ "\" alt=\"Ảnh danh mục\" class=\"img-fluid\" style=\"max-width: 110px; max-height: 110px;\"></td>");
			if (isManager) {
				out.append(
						"<td class=\"text-center align-middle py-2\"><a class=\"btn btn-primary btn-sm text-white\" href=\"/shop360/category/update?id="
								+ item.getCategory_id() + "\"><i class=\"bi bi-pencil-square\"></i></a></td>");
				out.append(
						"<td class=\"text-center align-middle py-2\"><a href=\"#\" class=\"btn btn-danger btn-sm text-white\" data-bs-toggle=\"modal\"  data-bs-target=\"#delCategory"
								+ item.getCategory_id() + "\" > <i class=\"bi bi-archive\"></i></a></td>");
				out.append("</tr>");
				generateDeleteModal(out, item);
			}
		});

		out.append("</tbody>");
		out.append("</table>");
		out.append("");

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
				out.append("<li class=\"page-item active\"><a class=\"page-link\" href=\"categories?page=" + i + "\">"
						+ i + "</a></li>");
			} else {
				out.append("<li class=\"page-item\"><a class=\"page-link\" href=\"categories?page=" + i + "\">" + i
						+ "</a></li>");
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

	private void generateAddModal(PrintWriter out, CategoryObject item) {
		// Lấy ngày hiện tại
		String currentDate = Utilities_Date.getDate();
		out.append(
				"<div class=\"modal fade\" id=\"addCategory\" data-bs-backdrop=\"static\" data-bs-keyboard=\"false\" tabindex=\"-1\" aria-labelledby=\"addCategoryLabel\" aria-hidden=\"true\">");
		out.append("<div class=\"modal-dialog modal-lg\">");
		out.append("<div class=\"modal-content\">");
		out.append(
				"<form method=\"post\" action=\"/shop360/categories\" class=\"needs-validation\" enctype=\"multipart/form-data\" novalidate>");
		out.append("<div class=\"modal-header text-bg-primary\">");
		out.append(
				"<h1 class=\"modal-title fs-5\" id=\"addCategoryLabel\"><i class=\"bi bi-person-plus\"></i> Thêm danh mục mới</h1>");
		out.append(
				"<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
		out.append("</div>");
		out.append("<div class=\"modal-body\">");

		out.append("<div class=\"col-sm-12\">");
		out.append("<label for=\"category_name\" class=\"form-label\">Tên danh mục</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"category_name\" name=\"txtName\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin tên danh mục</div>");
		out.append("</div>");

		out.append("<div class=\"col-sm-12\">");
		out.append("<label for=\"category_description\" class=\"form-label\">Mô tả danh mục</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"category_description\" name=\"txtDes\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu thông tin mô tả danh mục</div>");
		out.append("</div>");

		// Phần thêm input chọn ảnh
		out.append("<div class=\"col-sm-12\">");
		out.append("<label for=\"category_image\" class=\"form-label\">Ảnh danh mục</label>");
		out.append("<input type=\"file\" class=\"form-control\" id=\"category_image\" name=\"txtImage\" required>");
		out.append("<div class=\"invalid-feedback\">Thiếu ảnh danh mục</div>");
		out.append("</div>");

		out.append("<div class=\"row mb-3\">");

		out.append("<div class=\"col-sm-4\">");
		out.append("<label for=\"created_date\" class=\"form-label\">Ngày tạo</label>");
		out.append("<input type=\"text\" class=\"form-control\" id=\"created_date\" name=\"txtCreated_date\" value=\""
				+ currentDate + "\" readonly required>");
		out.append("</div>");

		out.append("<div class=\"col-sm-4\">");
		out.append("<label for=\"updated_date\" class=\"form-label\">Ngày cập nhật</label>");
		out.append(
				"<input type=\"text\" class=\"form-control\" id=\"updated_date\" name=\"txtUpdated_date\" value=\"\" readonly required>");
		out.append("</div>");

		out.append("</div>");

		out.append("</div>");
		out.append("<div class=\"modal-footer\">");
		out.append(
				"<button type=\"submit\" class=\"btn btn-primary\"><i class=\"bi bi-person-plus-fill me-2\"></i>Thêm </button>");
		out.append("<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Close</button>");
		out.append("</div>");
		out.append("</form>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
	}

	private void generateDeleteModal(PrintWriter out, CategoryObject item) {
		out.append("<form action=\"/shop360/categories?delid=" + item.getCategory_id() + "\" method=\"post\">");
		out.append("<div class=\"modal fade\" id=\"delCategory" + item.getCategory_id()
				+ "\" tabindex=\"-1\" aria-labelledby=\"delCategoryLabel" + item.getCategory_id()
				+ "\" aria-hidden=\"true\">");
		out.append("<div class=\"modal-dialog\">");
		out.append("<div class=\"modal-content\">");
		out.append("<div class=\"modal-header\">");
		out.append("<h5 class=\"modal-title\" id=\"delCategoryLabel" + item.getCategory_id() + "\">Xóa danh mục</h5>");
		out.append(
				"<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
		out.append("</div>");
		out.append("<div class=\"modal-body\">");
		out.append("Bạn có chắc chắn muốn xóa danh mục này?");
		out.append("</div>");
		out.append("<div class=\"modal-footer\">");
		out.append("<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Hủy</button>");
		out.append("<button type=\"submit\" class=\"btn btn-danger\">Đồng ý</button>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("</form>");
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
		String delIdString = request.getParameter("delid");
		if (delIdString != null) {
			int delId = Integer.parseInt(delIdString);
			CategoryObject cateD = new CategoryObject();
			cateD.setCategory_id(delId);
			boolean result = uf.delCategory(cateD);
			uf.releaseConnection();
			if (result) {
				response.sendRedirect("/shop360/categories");
			} else {
				response.sendRedirect("/shop360/categories?err=notok");
			}
		} else {
			// Lấy thông tin trên giao diện
			String name = request.getParameter("txtName");
			String description = request.getParameter("txtDes");
			String createdDate = request.getParameter("txtCreated_date");
			String updatedDate = request.getParameter("txtUpdated_date");
			// Xử lý tệp ảnh
			Part filePart = request.getPart("txtImage");
			String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
			String savePath = getServletContext().getRealPath("/") + "/img/categories" + File.separator + fileName;
			filePart.write(savePath);
			// Tạo đối tượng lưu trữ mới
			CategoryObject cate = new CategoryObject();
			cate.setCategory_name(name);
			cate.setCategory_description(description);
			cate.setCategory_image(fileName);
			cate.setCategory_created_date(createdDate);
			cate.setCategory_last_modified(updatedDate);
			boolean result = uf.addCategory(cate);
			uf.releaseConnection();
			if (result) {
				response.sendRedirect("/shop360/categories");
			} else {
				response.sendRedirect("/shop360/categories?err=notok");
			}
		}
	}

}
