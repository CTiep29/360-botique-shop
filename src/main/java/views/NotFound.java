package views;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Error
 */
@WebServlet("/notfound")
public class NotFound extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NotFound() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.append(" <!doctype html>");
		out.append(" <html lang=\"en\">");
		out.append(" <head>");
		out.append(" <meta charset=\"utf-8\">");
		out.append(" <title>Page Not Found</title>");
		out.append("<link href=\"/shop360/img/favicon.png\" rel=\"icon\">");
		out.append("<!-- Google Fonts -->");
		out.append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">");
		out.append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>");
		out.append(
				"<link href=\"https://fonts.googleapis.com/css2?family=Source+Sans+3:ital,wght@0,200..900;1,200..900&display=swap\" rel=\"stylesheet\">");
		out.append(" <link href=\"/shop360/css/all.min.css\" rel=\"stylesheet\" type=\"text/css\" />");
		out.append(" <link href=\"/shop360/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\" />");
		out.append("<link href=\"/shop360/css/bi/bootstrap-icons.css\" rel=\"stylesheet\">");
		out.append(" <link href=\"/shop360/css/basic.css\" rel=\"stylesheet\" type=\"text/css\" />");
		out.append(" </head>");
		out.append(" <body>");
		out.append(" <div class=\"bg-error\">");
		out.append(" <div class=\"d-flex flex-column vh-100 align-items-center justify-content-center\">");
		out.append(" <h1 class=\"display-1\">404</h1>");
		out.append(" <h2>Không tìm thấy trang</h2>");
		out.append(" <a class=\"btn btn-outline-dark\" href=\"/shop360/dashboard\">Về trang chủ</a>");
		out.append(" </div>");
		out.append(" </div>");
		out.append(" </body>");
		out.append(" </html>");
		out.close();
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
