package libraries;

import javax.servlet.*;

public class Utilities {

	/**
	 * Phương thức lấy giá trị Byte của tham số
	 * 
	 * @param request - lưu trữ dữ liệu đầu vào
	 * @param name    - tên tham số cần lấy
	 * @return
	 */
	public static byte getByteParam(ServletRequest request, String name) {
		byte value = -1;
		try {
			String str_value = request.getParameter(name);
			if (str_value != null && !str_value.equalsIgnoreCase("")) {
				value = Byte.parseByte(str_value);
			}
		} catch (NumberFormatException ex) {
			System.out.println("Lỗi giá trị tham số");
			ex.printStackTrace();
		}

		return value;
	}

	public static short getShortParam(ServletRequest request, String name) {
		short value = -1;
		try {
			String str_value = request.getParameter(name);
			if (str_value != null && !str_value.equalsIgnoreCase("")) {
				value = Short.parseShort(str_value);
			}
		} catch (NumberFormatException ex) {
			System.out.println("Lỗi giá trị tham số");
			ex.printStackTrace();
		}

		return value;
	}

	public static short getPageParam(ServletRequest request, String name) {
		short page = 1;
		try {
			String str_value = request.getParameter(name);
			if (str_value != null && !str_value.equalsIgnoreCase("")) {
				page = Short.parseShort(str_value);
			}
		} catch (NumberFormatException ex) {
			System.out.println("Lỗi giá trị tham số phân trang");
			ex.printStackTrace();
		}

		return page;
	}

	public static int getIntParam(ServletRequest request, String name) {
		int value = -1;
		try {
			String str_value = request.getParameter(name);
			if (str_value != null && !str_value.equalsIgnoreCase("")) {
				value = Integer.parseInt(str_value);
			}
		} catch (NumberFormatException ex) {
			System.out.println("Lỗi giá trị tham số");
			ex.printStackTrace();
		}

		return value;
	}
}
