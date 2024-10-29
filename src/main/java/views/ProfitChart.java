package views;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

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
 * Servlet implementation class ProfitChartView
 */
@WebServlet("/chart/profit")
public class ProfitChart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProfitChart() {
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
			if (user.getUser_role() == 1) {
				viewChart(request, response);
			} else {
				response.sendRedirect("/shop360/notfound");
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void viewChart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);

		// Tìm bộ quản lí kết nối
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

		Gson gson = new Gson();
		StringBuilder months = new StringBuilder();
		StringBuilder invested = new StringBuilder();
		StringBuilder revenue = new StringBuilder();
		StringBuilder profit = new StringBuilder();

		recentMonths.forEach(item -> {
			profit.append("" + dnc.getProfitByMonth(item));
			invested.append("" + rnc.getInvestedByMonth(item));
			revenue.append("" + dnc.getRevenueByMonth(item));
			months.append("" + gson.toJson(item));
			if (recentMonths.indexOf(item) < recentMonths.size() - 1) {
				invested.append(",");
				revenue.append(",");
				profit.append(",");
				months.append(",");
			}
		});
		dnc.releaseConnection();
		rnc.releaseConnection();

		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header?pos=profitchart");
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
		out.append("<li class=\"breadcrumb-item\">Biểu đồ</li>");
		out.append("<li class=\"breadcrumb-item active\">Lợi nhuận</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");

		out.append(this.viewDoubleLineChart(months, invested, revenue));
		out.append(this.viewProfitChart(months, profit));

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
		doGet(request, response);
	}

	private StringBuilder viewProfitChart(StringBuilder months, StringBuilder profit) {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h5 class=\"card-title\">Biểu đồ thống kê lợi nhuận</h5>");
		tmp.append("");
		tmp.append("<!-- Line Chart -->");
		tmp.append("<div id=\"lineChart\"></div>");
		tmp.append("");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#lineChart\"), {");
		tmp.append("series: [{");
		tmp.append("name: \"Lợi nhuận\",");
		tmp.append("data: [" + profit + "]");
		tmp.append("}],");
		tmp.append("chart: {");
		tmp.append("height: 350,");
		tmp.append("type: 'line',");
		tmp.append("zoom: {");
		tmp.append("enabled: false");
		tmp.append("}");
		tmp.append("},");
		tmp.append("dataLabels: {");
		tmp.append("enabled: false");
		tmp.append("},");
		tmp.append("stroke: {");
		tmp.append("curve: 'straight'");
		tmp.append("},");
		tmp.append("grid: {");
		tmp.append("row: {");
		tmp.append("colors: ['#f3f3f3', 'transparent'],");
		tmp.append("opacity: 0.5");
		tmp.append("},");
		tmp.append("},");
		tmp.append("xaxis: {");
		tmp.append("categories: [" + months + "],");
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
}