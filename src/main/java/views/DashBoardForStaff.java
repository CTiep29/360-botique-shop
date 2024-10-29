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

import com.google.gson.Gson;

import objectcontrol.DeliveryNoteControl;
import objectcontrol.ReceivedNoteControl;
import objects.UserObject;
import util.ConnectionPool;

/**
 * Servlet implementation class DashBoardForStaff
 */
@WebServlet("/dashboardforstaff")
public class DashBoardForStaff extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DashBoardForStaff() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UserObject user = (UserObject) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect("/shop360/user/login");
		} else {
			if (user.getUser_role() == 2) {
				view(request, response, user.getUser_id());
			} else {
				response.sendRedirect("/shop360/notfound");
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void view(HttpServletRequest request, HttpServletResponse response, int userId)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType(CONTENT_TYPE);
		ConnectionPool cp = (ConnectionPool) getServletContext().getAttribute("CPool");
		ReceivedNoteControl rnc = new ReceivedNoteControl(cp);
		DeliveryNoteControl dnc = new DeliveryNoteControl(cp);
		ArrayList<HashMap<String, String>> receivedQuantityChart = rnc.getReceivedQuantityByUser(userId);
		ArrayList<HashMap<String, String>> receivedValueChart = rnc.getReceivedValueByUser(userId);
		rnc.releaseConnection();
		ArrayList<HashMap<String, String>> deliveryQuantityChart = dnc.getDeliveryQuantityByUser(userId);
		ArrayList<HashMap<String, String>> deliveryValueChart = dnc.getDeliveryValueByUser(userId);
		dnc.releaseConnection();

		PrintWriter out = response.getWriter();

		// Tham chiếu tìm kiếm header
		RequestDispatcher header = request.getRequestDispatcher("/header");
		if (header != null) {
			header.include(request, response);
		}

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
		out.append(this.viewQuantityCharts(receivedQuantityChart, deliveryQuantityChart));
		out.append(this.viewValueCharts(receivedValueChart, deliveryValueChart));
		out.append("</main");

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

	private StringBuilder viewValueCharts(ArrayList<HashMap<String, String>> receivedChart,
			ArrayList<HashMap<String, String>> deliveryChart) {
		Gson gson = new Gson();
		StringBuilder receivedMonths = new StringBuilder();
		StringBuilder receivedValue = new StringBuilder();
		receivedChart.forEach(item -> {
			receivedValue.append(item.get("total_value"));
			receivedMonths.append(gson.toJson(item.get("month")));
			if (receivedChart.indexOf(item) < receivedChart.size() - 1) {
				receivedValue.append(",");
				receivedMonths.append(",");
			}
		});

		StringBuilder deliveryMonths = new StringBuilder();
		StringBuilder deliveryValue = new StringBuilder();
		deliveryChart.forEach(item -> {
			deliveryValue.append(item.get("total_value"));
			deliveryMonths.append(gson.toJson(item.get("month")));
			if (deliveryChart.indexOf(item) < receivedChart.size() - 1) {
				deliveryValue.append(",");
				deliveryMonths.append(",");
			}
		});
		StringBuilder tmp = new StringBuilder();
		tmp.append("<div class=\"row\">");
		tmp.append("<div class=\"col-sm-6\">");
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h5 class=\"card-title\">Biểu đồ giá trị hàng nhập của bạn</h5>");
		tmp.append("");
		tmp.append("<!-- Line Chart -->");
		tmp.append("<div id=\"lineChart1\"></div>");
		tmp.append("");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#lineChart1\"), {");
		tmp.append("series: [{");
		tmp.append("name: \"Tổng\",");
		tmp.append("data: [" + receivedValue + "]");
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
		tmp.append("categories: [" + receivedMonths + "],");
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
		tmp.append("</div>");
		tmp.append("<div class=\"col-sm-6\">");
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h5 class=\"card-title\">Biểu đồ giá trị hàng xuất kho của bạn</h5>");
		tmp.append("");
		tmp.append("<!-- Line Chart -->");
		tmp.append("<div id=\"lineChart2\"></div>");
		tmp.append("");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#lineChart2\"), {");
		tmp.append("series: [{");
		tmp.append("name: \"Tổng\",");
		tmp.append("data: [" + deliveryValue + "]");
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
		tmp.append("categories: [" + deliveryMonths + "],");
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
		tmp.append("</div>");
		tmp.append("</div>");
		return tmp;
	}

	private StringBuilder viewQuantityCharts(ArrayList<HashMap<String, String>> receivedChart,
			ArrayList<HashMap<String, String>> deliveryChart) {
		Gson gson = new Gson();
		StringBuilder receivedMonths = new StringBuilder();
		StringBuilder receivedQuantity = new StringBuilder();
		receivedChart.forEach(item -> {
			receivedQuantity.append(item.get("total_received_quantity"));
			receivedMonths.append(gson.toJson(item.get("month")));
			if (receivedChart.indexOf(item) < receivedChart.size() - 1) {
				receivedQuantity.append(",");
				receivedMonths.append(",");
			}
		});

		StringBuilder deliveryMonths = new StringBuilder();
		StringBuilder deliveryQuantity = new StringBuilder();
		deliveryChart.forEach(item -> {
			deliveryQuantity.append(item.get("total_delivery_quantity"));
			deliveryMonths.append(gson.toJson(item.get("month")));
			if (deliveryChart.indexOf(item) < receivedChart.size() - 1) {
				deliveryQuantity.append(",");
				deliveryMonths.append(",");
			}
		});
		StringBuilder tmp = new StringBuilder();
		tmp.append("<div class=\"row\">");
		tmp.append("<div class=\"col-sm-6\">");
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h5 class=\"card-title\">Biểu đồ số lượng hàng nhập kho của bạn</h5>");
		tmp.append("");
		tmp.append("<!-- Radar Chart -->");
		tmp.append("<div id=\"radarChart1\"></div>");
		tmp.append("");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#radarChart1\"), {");
		tmp.append("series: [{");
		tmp.append("name: 'Số lượng',");
		tmp.append("data: [" + receivedQuantity + "],");
		tmp.append("}],");
		tmp.append("chart: {");
		tmp.append("height: 350,");
		tmp.append("type: 'radar',");
		tmp.append("},");
		tmp.append("xaxis: {");
		tmp.append("categories: [" + receivedMonths + "]");
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
		tmp.append("<!-- End Radar Chart -->");
		tmp.append("");
		tmp.append("</div>");
		tmp.append("</div>");
		tmp.append("</div>");
		tmp.append("<div class=\"col-sm-6\">");
		tmp.append("<div class=\"card\">");
		tmp.append("<div class=\"card-body\">");
		tmp.append("<h5 class=\"card-title\">Biểu đồ số lượng hàng xuất kho của bạn</h5>");
		tmp.append("");
		tmp.append("<!-- Radar Chart -->");
		tmp.append("<div id=\"radarChart2\"></div>");
		tmp.append("");
		tmp.append("<script>");
		tmp.append("document.addEventListener(\"DOMContentLoaded\", () => {");
		tmp.append("new ApexCharts(document.querySelector(\"#radarChart2\"), {");
		tmp.append("series: [{");
		tmp.append("name: 'Số lượng',");
		tmp.append("data: [" + deliveryQuantity + "],");
		tmp.append("}],");
		tmp.append("chart: {");
		tmp.append("height: 350,");
		tmp.append("type: 'radar',");
		tmp.append("},");
		tmp.append("xaxis: {");
		tmp.append("categories: [" + deliveryMonths + "]");
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
		tmp.append("<!-- End Radar Chart -->");
		tmp.append("");
		tmp.append("</div>");
		tmp.append("</div>");
		tmp.append("</div>");
		tmp.append("</div>");
		return tmp;
	}
}
