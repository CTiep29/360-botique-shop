package util;

public interface ShareControl {
	// Chia sẻ Bộ quản lý kết nối giữa các Basic
	public ConnectionPool getCP();

	// Yêu cầu các đối tượng trả lại Connection khi đã xong nhiệm vụ
	public void releaseConnection();
}
