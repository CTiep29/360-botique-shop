package views;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.google.gson.Gson;
import objectcontrol.DeliveryNoteControl;
import objectcontrol.ReceivedNoteControl;
import objects.UserObject;
import util.ConnectionPool;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class chart
 */
@WebServlet("/chart/drchart")
public class DRChart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DRChart() {
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
		ArrayList<HashMap<String, Object>> chart1 = rnc.getReceivedQuantityByCategory();
		rnc.releaseConnection();
		ArrayList<HashMap<String, Object>> chart2 = dnc.getDeliveryQuantityByCategory();
		dnc.releaseConnection();
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
		StringBuilder received = new StringBuilder();
		StringBuilder delivery = new StringBuilder();
		StringBuilder profit = new StringBuilder();
		recentMonths.forEach(item -> {
			received.append("" + rnc.getQuantityByMonth(item));
			delivery.append("" + dnc.getQuantityByMonth(item));
			profit.append("" + dnc.getProfitByMonth(item));
			months.append("" + gson.toJson(item));
			if (recentMonths.indexOf(item) < recentMonths.size() - 1) {
				received.append(",");
				delivery.append(",");
				profit.append(",");
				months.append(",");
			}
		});

		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header?pos=drchart");
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
		out.append("<li class=\"breadcrumb-item active\">Sản phẩm</li>");
		out.append("</ol>");
		out.append("</nav>");
		out.append("</div><!-- End Page Title -->");

		out.append(this.viewColumnChart(months, received, delivery));
		out.append(this.viewPieChart(chart1, chart2));

		// Tham chiếu tìm kiếm footer
		RequestDispatcher footer = request.getRequestDispatcher("/footer");
		if (footer != null) {
			footer.include(request, response);
		}

	}

	private StringBuilder viewColumnChart(StringBuilder months, StringBuilder received, StringBuilder delivery) {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h5 class=\"card-title\">Biểu đồ so sánh số lượng sản phẩm xuất/nhập</h5>");
		tmp.append("<div id=\"columnChart\"></div>");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#columnChart\"), {");
		tmp.append("series: [{");
		tmp.append("name: 'Nhập',");
		tmp.append("data: [" + received + "]");
		tmp.append("}, {");
		tmp.append("name: 'Xuất',");
		tmp.append("data: [" + delivery + "]");
		tmp.append("}],");
		tmp.append("chart: {");
		tmp.append("type: 'bar',");
		tmp.append("height: 350");
		tmp.append("},");
		tmp.append("plotOptions: {");
		tmp.append("bar: {");
		tmp.append("horizontal: false,");
		tmp.append("columnWidth: '55%',");
		tmp.append("endingShape: 'rounded'");
		tmp.append("},");
		tmp.append("},");
		tmp.append("dataLabels: {");
		tmp.append("enabled: false");
		tmp.append("},");
		tmp.append("stroke: {");
		tmp.append("show: true,");
		tmp.append("width: 2,");
		tmp.append("colors: ['transparent']");
		tmp.append("},");
		tmp.append("xaxis: {");
		tmp.append("categories: [" + months + "],");
		tmp.append("},");
		tmp.append("yaxis: {");
		tmp.append("title: {");
		tmp.append("text: '(Sản phẩm)',");
		tmp.append("style: {fontFamily: \"Source Sans 3, sans-serif\"}");
		tmp.append("}");
		tmp.append("},");
		tmp.append("fill: {");
		tmp.append("opacity: 1");
		tmp.append("},");
		tmp.append("tooltip: {");
		tmp.append("y: {");
		tmp.append("formatter: function(val) {");
		tmp.append("return val + \" sản phẩm\"");
		tmp.append("}");
		tmp.append("}");
		tmp.append("}");
		tmp.append("}).render();");
		tmp.append("});");
		tmp.append("</script>");
		tmp.append("</div>");
		tmp.append("</div>");

		return tmp;
	}

	private StringBuilder viewPieChart(ArrayList<HashMap<String, Object>> chart1,
			ArrayList<HashMap<String, Object>> chart2) {
		StringBuilder categories1 = new StringBuilder();
		StringBuilder quantity1 = new StringBuilder();
		chart1.forEach(item -> {
			quantity1.append(item.get("total"));
			categories1.append("'" + item.get("category_name") + "'");
			if (chart1.indexOf(item) < chart1.size() - 1) {
				quantity1.append(",");
				categories1.append(",");
			}
		});
		StringBuilder categories2 = new StringBuilder();
		StringBuilder quantity2 = new StringBuilder();
		chart2.forEach(item -> {
			quantity2.append(item.get("total"));
			categories2.append("'" + item.get("category_name") + "'");
			if (chart2.indexOf(item) < chart2.size() - 1) {
				quantity2.append(",");
				categories2.append(",");
			}
		});
		StringBuilder tmp = new StringBuilder();
		tmp.append("<div class=\"row\">");
		tmp.append("<div class=\"col-sm-6\">");
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h6 class=\"card-title\">Biểu đồ nhập theo danh mục</h6>");
		tmp.append("");
		tmp.append("<!-- Pie Chart -->");
		tmp.append("<div id=\"pieChart1\"></div>");
		tmp.append("");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#pieChart1\"), {");
		tmp.append("series: [" + quantity1 + "],");
		tmp.append("chart: {");
		tmp.append("height: 350,");
		tmp.append("type: 'pie',");
		tmp.append("toolbar: {");
		tmp.append("show: true");
		tmp.append("}");
		tmp.append("},");
		tmp.append("labels: [" + categories1 + "]");
		tmp.append("}).render();");
		tmp.append("});");
		tmp.append("</script>");
		tmp.append("<!-- End Pie Chart -->");
		tmp.append("");
		tmp.append("</div>");
		tmp.append("</div>");
		tmp.append("</div>");
		tmp.append("<div class=\"col-sm-6\">");
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h6 class=\"card-title\">Biểu đồ xuất theo danh mục</h6>");
		tmp.append("");
		tmp.append("<!-- Pie Chart -->");
		tmp.append("<div id=\"pieChart2\"></div>");
		tmp.append("");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#pieChart2\"), {");
		tmp.append("series: [" + quantity2 + "],");
		tmp.append("chart: {");
		tmp.append("height: 350,");
		tmp.append("type: 'pie',");
		tmp.append("toolbar: {");
		tmp.append("show: true");
		tmp.append("}");
		tmp.append("},");
		tmp.append("labels: [" + categories2 + "]");
		tmp.append("}).render();");
		tmp.append("});");
		tmp.append("</script>");
		tmp.append("<!-- End Pie Chart -->");
		tmp.append("");
		tmp.append("</div>");
		tmp.append("</div>");
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
