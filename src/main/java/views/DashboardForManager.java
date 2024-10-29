package views;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import objectcontrol.DeliveryNoteControl;
import objectcontrol.ReceivedNoteControl;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class View
 */
@WebServlet("/dashboardformanager")
public class DashboardForManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DashboardForManager() {
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
			if (user.getUser_role() == 1) {
				view(request, response);
			} else {
				response.sendRedirect("/shop360/notfound");
			}
		}
	}

	protected void view(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);
		Gson gson = new Gson();
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		DeliveryNoteControl dnc = new DeliveryNoteControl(cp);
		ReceivedNoteControl rnc = new ReceivedNoteControl(cp);
		YearMonth currentYearMonth = YearMonth.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

		ArrayList<String> recentMonths = new ArrayList<>();

		for (int i = 0; i < 12; i++) {
			YearMonth month = currentYearMonth.minusMonths(i);
			recentMonths.add(month.format(formatter));
		}

		Collections.reverse(recentMonths);
		StringBuilder months = new StringBuilder();
		StringBuilder invested = new StringBuilder();
		StringBuilder revenue = new StringBuilder();
		recentMonths.forEach(item -> {
			invested.append("" + rnc.getInvestedByMonth(item));
			revenue.append("" + dnc.getRevenueByMonth(item));
			months.append("" + gson.toJson(item));
			if (recentMonths.indexOf(item) < recentMonths.size() - 1) {
				invested.append(",");
				revenue.append(",");
				months.append(",");
			}
		});

		if (cp == null) {
			getServletContext().setAttribute("CPool", dnc.getCP());
		}

		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header");
		if (header != null) {
			header.include(request, response);
		}

		HashMap<String, Object> result1 = rnc.getDateQuantityOnDashboard();
		HashMap<String, Object> result2 = rnc.getMonthQuantityOnDashboard();
		HashMap<String, Object> result3 = rnc.getDateInvestedOnDashboard();
		HashMap<String, Object> result4 = rnc.getMonthInvestedOnDashboard();
		HashMap<String, Object> result5 = dnc.getDateRevenueOnDashboard();
		HashMap<String, Object> result6 = dnc.getMonthRevenueOnDashboard();
		HashMap<String, Object> result7 = dnc.getDateProfitOnDashboard();
		HashMap<String, Object> result8 = dnc.getMonthProfitOnDashboard();
		rnc.releaseConnection();
		dnc.releaseConnection();

		// Tạo đối tượng xuất nội dung
		PrintWriter out = response.getWriter();
		out.append("<main id=\"main\" class=\"main\">");
		out.append("<div class=\"pagetitle d-flex\">");
		out.append("<nav class=\"ms-auto\">");
		out.append("<ol class=\"breadcrumb\">");
		out.append(
				"<li class=\"breadcrumb-item\"><a href=\"/shop360/dashboard\"><i class=\"bi bi-house-fill\"></i></a></li>");
		out.append("<li class=\"breadcrumb-item active\">Trang chủ</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");
		out.append("<section class=\"section\">");
		out.append("<div class=\"row\">");
		out.append("");

		out.append("<!-- Products Card -->");
		out.append("<div class=\"col-xxl-4 col-md-3\">");
		out.append("<div class=\"card info-card\">");
		out.append("<div class=\"filter\">");
		out.append(
				"<a class=\"icon\" href=\"#\" data-bs-toggle=\"dropdown\"><i class=\"d-inline-block px-3 pt-2 bi bi-three-dots\"></i></a>");
		out.append("<ul class=\"dropdown-menu dropdown-menu-end dropdown-menu-arrow\">");
		out.append("<li class=\"dropdown-header text-start\">");
		out.append("<h6>Lọc</h6>");
		out.append("</li>");
		out.append("");
		out.append("<li><a id=\"product-today\" class=\"dropdown-item\" href=\"#\">Hôm nay</a></li>");
		out.append("<li><a id=\"product-month\" class=\"dropdown-item\" href=\"#\">Tháng này</a></li>");
		out.append("</ul>");
		out.append("</div>");
		out.append("<div class=\"card-body\">");
		out.append(
				"<h5 class=\"card-title\"><a href=\"/shop360/chart/drchart\">Sản phẩm</a> |<span id=\"product-filter\"> Tháng này</span></h5>");
		out.append("");
		out.append("<div class=\"d-flex align-items-center\">");
		out.append("<div class=\"card-icon rounded-circle d-flex align-items-center justify-content-center\">");
		out.append("<i class=\"bi bi-box-seam\"></i>");
		out.append("</div>");
		out.append("<div class=\"ps-3\">");
		out.append("<h6 id=\"product-text\">" + result2.get("this_quantity") + "</h6>");
		out.append(
				"<span id=\"product-increase\" class=\"small pt-1 fw-bold\">" + result2.get("increase") + "%</span>");
		out.append("");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("");
		out.append("</div>");
		out.append("</div><!-- End Products Card -->");
		out.append("");

		out.append("<!-- Invested Card -->");
		out.append("<div class=\"col-xxl-4 col-md-3\">");
		out.append("<div class=\"card info-card\">");
		out.append("<div class=\"filter\">");
		out.append(
				"<a class=\"icon\" href=\"#\" data-bs-toggle=\"dropdown\"><i class=\"d-inline-block px-3 pt-2 bi bi-three-dots\"></i></a>");
		out.append("<ul class=\"dropdown-menu dropdown-menu-end dropdown-menu-arrow\">");
		out.append("<li class=\"dropdown-header text-start\">");
		out.append("<h6>Lọc</h6>");
		out.append("</li>");
		out.append("");
		out.append("<li><a id=\"invest-today\" class=\"dropdown-item\" href=\"#\">Hôm nay</a></li>");
		out.append("<li><a id=\"invest-month\" class=\"dropdown-item\" href=\"#\">Tháng này</a></li>");
		out.append("</ul>");
		out.append("</div>");
		out.append("<div class=\"card-body\">");
		out.append(
				"<h5 class=\"card-title\"><a href=\"/shop360/chart/invested\">Đầu tư</a> |<span id=\"invested-filter\"> Tháng này</span></h5>");
		out.append("");
		out.append("<div class=\"d-flex align-items-center\">");
		out.append("<div class=\"card-icon rounded-circle d-flex align-items-center justify-content-center\">");
		out.append("<i class=\"bi bi-wallet\"></i>");
		out.append("</div>");
		out.append("<div class=\"ps-3\">");
		out.append("<h6 id=\"invest-text\">" + result4.get("this_invested") + "</h6>");
		out.append("<span id=\"invest-increase\" class=\"small pt-1 fw-bold\">" + result4.get("increase") + "%</span>");
		out.append("");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("");
		out.append("</div>");
		out.append("</div><!-- End Invested Card -->");
		out.append("");

		out.append("<!-- Revenue Card -->");
		out.append("<div class=\"col-xxl-4 col-md-3\">");
		out.append("<div class=\"card info-card\">");
		out.append("<div class=\"filter\">");
		out.append(
				"<a class=\"icon\" href=\"#\" data-bs-toggle=\"dropdown\"><i class=\"d-inline-block px-3 pt-2 bi bi-three-dots\"></i></a>");
		out.append("<ul class=\"dropdown-menu dropdown-menu-end dropdown-menu-arrow\">");
		out.append("<li class=\"dropdown-header text-start\">");
		out.append("<h6>Lọc</h6>");
		out.append("</li>");
		out.append("");
		out.append("<li><a id=\"revenue-today\" class=\"dropdown-item\" href=\"#\">Hôm nay</a></li>");
		out.append("<li><a id=\"revenue-month\" class=\"dropdown-item\" href=\"#\">Tháng này</a></li>");
		out.append("</ul>");
		out.append("</div>");
		out.append("<div class=\"card-body\">");
		out.append(
				"<h5 class=\"card-title\"><a href=\"/shop360/chart/revenue\">Doanh thu</a> |<span id=\"revenue-filter\"> Tháng này</span></h5>");
		out.append("");
		out.append("<div class=\"d-flex align-items-center\">");
		out.append("<div class=\"card-icon rounded-circle d-flex align-items-center justify-content-center\">");
		out.append("<i class=\"bi bi-cash-stack\"></i>");
		out.append("</div>");
		out.append("<div class=\"ps-3\">");
		out.append("<h6 id=\"revenue-text\">" + result6.get("this_revenue") + "</h6>");
		out.append(
				"<span id=\"revenue-increase\" class=\"small pt-1 fw-bold\">" + result6.get("increase") + "%</span>");
		out.append("");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("");
		out.append("</div>");
		out.append("</div><!-- End Revenue Card -->");
		out.append("");

		out.append("<!-- Revenue Card -->");
		out.append("<div class=\"col-xxl-4 col-md-3\">");
		out.append("<div class=\"card info-card\">");
		out.append("<div class=\"filter\">");
		out.append(
				"<a class=\"icon\" href=\"#\" data-bs-toggle=\"dropdown\"><i class=\"d-inline-block px-3 pt-2 bi bi-three-dots\"></i></a>");
		out.append("<ul class=\"dropdown-menu dropdown-menu-end dropdown-menu-arrow\">");
		out.append("<li class=\"dropdown-header text-start\">");
		out.append("<h6>Lọc</h6>");
		out.append("</li>");
		out.append("");
		out.append("<li><a id=\"profit-today\" class=\"dropdown-item\" href=\"#\">Hôm nay</a></li>");
		out.append("<li><a id=\"profit-month\" class=\"dropdown-item\" href=\"#\">Tháng này</a></li>");
		out.append("</ul>");
		out.append("</div>");
		out.append("<div class=\"card-body\">");
		out.append(
				"<h5 class=\"card-title\"><a href=\"/shop360/chart/profit\">Lợi nhuận</a> |<span id=\"profit-filter\"> Tháng này</span></h5>");
		out.append("");
		out.append("<div class=\"d-flex align-items-center\">");
		out.append("<div class=\"card-icon rounded-circle d-flex align-items-center justify-content-center\">");
		out.append("<i class=\"bi bi-currency-dollar\"></i>");
		out.append("</div>");
		out.append("<div class=\"ps-3\">");

		out.append("<h6 id=\"profit-text\">" + result8.get("this_profit") + "</h6>");
		out.append("<span id=\"profit-increase\" class=\"small pt-1 fw-bold\">" + result8.get("increase") + "%</span>");
		out.append("");
		out.append("</div>");
		out.append("</div>");
		out.append("</div>");
		out.append("");
		out.append("</div>");
		out.append("</div><!-- End Revenue Card -->");
		out.append("");
		out.append("</div>");
		out.append("</section>");
		out.append(this.viewDoubleLineChart(months, invested, revenue));
		out.append("</main><!-- End #main -->");

		// Tham chiếu tìm kiếm footer
		RequestDispatcher footer = request.getRequestDispatcher("/footer");
		if (footer != null) {
			footer.include(request, response);
		}

		// javascript
		out.append("<script>");
		out.append("document.addEventListener(\"DOMContentLoaded\", function () {");
		out.append("var filters = {");
		out.append("productToday: document.getElementById('product-today'),");
		out.append("productMonth: document.getElementById('product-month'),");
		out.append("investToday: document.getElementById('invest-today'),");
		out.append("investMonth: document.getElementById('invest-month'),");
		out.append("revenueToday: document.getElementById('revenue-today'),");
		out.append("revenueMonth: document.getElementById('revenue-month'),");
		out.append("profitToday: document.getElementById('profit-today'),");
		out.append("profitMonth: document.getElementById('profit-month')");
		out.append("};");

		out.append("var texts = {");
		out.append("productText: document.getElementById('product-text'),");
		out.append("productIncrease: document.getElementById('product-increase'),");
		out.append("investText: document.getElementById('invest-text'),");
		out.append("investIncrease: document.getElementById('invest-increase'),");
		out.append("revenueText: document.getElementById('revenue-text'),");
		out.append("revenueIncrease: document.getElementById('revenue-increase'),");
		out.append("profitText: document.getElementById('profit-text'),");
		out.append("profitIncrease: document.getElementById('profit-increase'),");
		out.append("productFilter: document.getElementById('product-filter'),");
		out.append("investedFilter: document.getElementById('invested-filter'),");
		out.append("revenueFilter: document.getElementById('revenue-filter'),");
		out.append("profitFilter: document.getElementById('profit-filter')");
		out.append("};");

		out.append("filters.productToday.addEventListener(\'click\', function() {");
		out.append("texts.productText.textContent =" + result1.get("this_quantity") + ";");
		out.append("texts.productIncrease.textContent ='" + result1.get("increase") + "%';");
		out.append("texts.productFilter.textContent = ' Hôm nay';");
		out.append("});");

		out.append("filters.productMonth.addEventListener(\'click\', function() {");
		out.append("texts.productText.textContent =" + result2.get("this_quantity") + ";");
		out.append("texts.productIncrease.textContent ='" + result2.get("increase") + "%';");
		out.append("texts.productFilter.textContent = ' Tháng này';");
		out.append("});");

		out.append("filters.investToday.addEventListener(\'click\', function() {");
		out.append("texts.investText.textContent =" + result3.get("this_invested") + ";");
		out.append("texts.investIncrease.textContent ='" + result3.get("increase") + "%';");
		out.append("texts.investedFilter.textContent = ' Hôm nay';");
		out.append("});");

		out.append("filters.investMonth.addEventListener(\'click\', function() {");
		out.append("texts.investText.textContent =" + result4.get("this_invested") + ";");
		out.append("texts.investIncrease.textContent ='" + result4.get("increase") + "%';");
		out.append("texts.investedFilter.textContent = ' Tháng này';");
		out.append("});");

		out.append("filters.revenueToday.addEventListener(\'click\', function() {");
		out.append("texts.revenueText.textContent =" + result5.get("this_revenue") + ";");
		out.append("texts.revenueIncrease.textContent ='" + result5.get("increase") + "%';");
		out.append("texts.revenueFilter.textContent = ' Hôm nay';");
		out.append("});");

		out.append("filters.revenueMonth.addEventListener(\'click\', function() {");
		out.append("texts.revenueText.textContent =" + result6.get("this_revenue") + ";");
		out.append("texts.revenueIncrease.textContent ='" + result6.get("increase") + "%';");
		out.append("texts.revenueFilter.textContent = ' Tháng này';");
		out.append("});");

		out.append("filters.profitToday.addEventListener(\'click\', function() {");
		out.append("texts.profitText.textContent =" + result7.get("this_profit") + ";");
		out.append("texts.profitIncrease.textContent ='" + result7.get("increase") + "%';");
		out.append("texts.profitFilter.textContent = ' Hôm nay';");
		out.append("});");

		out.append("filters.profitMonth.addEventListener(\'click\', function() {");
		out.append("texts.profitText.textContent =" + result8.get("this_profit") + ";");
		out.append("texts.profitIncrease.textContent ='" + result8.get("increase") + "%';");
		out.append("texts.profitFilter.textContent = ' Tháng này';");
		out.append("});");

		out.append("});");
		out.append("</script>");
	}

	private StringBuilder viewDoubleLineChart(StringBuilder months, StringBuilder invested, StringBuilder revenue) {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h5 class=\"card-title\">Thống kê đầu tư và doanh thu</h5>");
		tmp.append("");
		tmp.append("<!-- Line Chart -->");
		tmp.append("<div id=\"reportsChart\"></div>");
		tmp.append("");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#reportsChart\"), {");
		tmp.append("series: [{");
		tmp.append("name: 'Đầu tư',");
		tmp.append("data: [" + invested + "]");
		tmp.append("}, {");
		tmp.append("name: 'Doanh thu',");
		tmp.append("data: [" + revenue + "]");
		tmp.append("}],");
		tmp.append("chart: {");
		tmp.append("height: 350,");
		tmp.append("type: 'area',");
		tmp.append("toolbar: {");
		tmp.append("show: false");
		tmp.append("},");
		tmp.append("},");
		tmp.append("markers: {");
		tmp.append("size: 4");
		tmp.append("},");
		tmp.append("colors: ['#4154f1', '#2eca6a'],");
		tmp.append("fill: {");
		tmp.append("type: \"gradient\",");
		tmp.append("gradient: {");
		tmp.append("shadeIntensity: 1,");
		tmp.append("opacityFrom: 0.3,");
		tmp.append("opacityTo: 0.4,");
		tmp.append("stops: [0, 90, 100]");
		tmp.append("}");
		tmp.append("},");
		tmp.append("dataLabels: {");
		tmp.append("enabled: false");
		tmp.append("},");
		tmp.append("stroke: {");
		tmp.append("curve: 'smooth',");
		tmp.append("width: 2");
		tmp.append("},");
		tmp.append("xaxis: {");
		tmp.append("type: 'datetime',");
		tmp.append("categories: [" + months + "]");
		tmp.append("},");
		tmp.append("tooltip: {");
		tmp.append("x: {");
		tmp.append("format: 'MM/yyyy'");
		tmp.append("},");
		tmp.append("}");
		tmp.append(",yaxis: {");
		tmp.append("labels: {");
		tmp.append("formatter: function (val) {");
		tmp.append("return new Intl.NumberFormat('de-DE').format(val);");
		tmp.append("}");
		tmp.append("}");
		tmp.append("},");
		tmp.append("tooltip: {");
		tmp.append("y: {");
		tmp.append("formatter: function (val) {");
		tmp.append("return new Intl.NumberFormat('de-DE').format(val);");
		tmp.append("}");
		tmp.append("}");
		tmp.append("}");
		tmp.append("}).render();");
		tmp.append("});");
		tmp.append("</script>");
		tmp.append("<!-- End Line Chart -->");
		tmp.append("");
		tmp.append("</div>");
		tmp.append("</div>");
		return tmp;
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
