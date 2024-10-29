package objects;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvoiceObject {
	private ServletContext servletContext;

	public InvoiceObject(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void createDeliveryInvoice(HashMap<String, Object> deliveryNote, ArrayList<HashMap<String, Object>> details)
			throws IOException {
		PDDocument document = new PDDocument();
		PDPage firstPage = new PDPage(PDRectangle.A4);
		document.addPage(firstPage);

		int pageHeight = (int) firstPage.getMediaBox().getHeight();
		PDPageContentStream contentStream = new PDPageContentStream(document, firstPage);
		MyTextClass myTextClass = new MyTextClass(document, contentStream);

		String fontPath = servletContext.getRealPath("/fonts/font-times-new-roman.ttf");
		PDType0Font font = PDType0Font.load(document, new File(fontPath));

		String store_name = (String) deliveryNote.get("store_name");
		String date = (String) deliveryNote.get("delivery_note_date");

		myTextClass.addSingleLineText("Phiếu xuất", 230, pageHeight - 50, font, 24, Color.BLACK);
		myTextClass.addSingleLineText("Cửa hàng: " + store_name, 25, pageHeight - 100, font, 14, Color.BLACK);
		myTextClass.addSingleLineText("Ngày xuất: " + date, 25, pageHeight - 124, font, 14, Color.BLACK);

		MyTableClass myTable = new MyTableClass(document, contentStream);
		int[] cellWidths = { 30, 230, 50, 70, 70, 90 };
		myTable.setTable(cellWidths, 30, 25, pageHeight - 190);
		myTable.setTableFont(font, 16, Color.BLACK);

		myTable.addCell("ID");
		myTable.addCell("Tên SP");
		myTable.addCell("ĐVT");
		myTable.addCell("Số lượng");
		myTable.addCell("Giá xuất");
		myTable.addCell("Thành tiền");

		details.forEach(item -> {
			try {
				myTable.addCell(item.get("product_id") + "");
				myTable.addCell(item.get("product_name") + "");
				myTable.addCell(item.get("product_unit") + "");
				myTable.addCell(item.get("delivery_quantity") + "");
				myTable.addCell(item.get("delivery_price") + "");
				myTable.addCell(((int) item.get("delivery_price") * (int) item.get("delivery_quantity")) + "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		String total = deliveryNote.get("delivery_note_value").toString();
		myTextClass.addSingleLineText("Tổng: " + total, 450, myTable.yPosition, font, 16, Color.BLACK);

		contentStream.close();
		String pdfPath = "D:\\" + "delivery-note-" + date + ".pdf";
		document.save(pdfPath);
		document.close();
	}

	public void createReceivedInvoice(HashMap<String, Object> receivedNote, ArrayList<HashMap<String, Object>> details)
			throws IOException {
		PDDocument document = new PDDocument();
		PDPage firstPage = new PDPage(PDRectangle.A4);
		document.addPage(firstPage);

		int pageHeight = (int) firstPage.getMediaBox().getHeight();
		PDPageContentStream contentStream = new PDPageContentStream(document, firstPage);
		MyTextClass myTextClass = new MyTextClass(document, contentStream);

		String fontPath = servletContext.getRealPath("/fonts/font-times-new-roman.ttf");
		PDType0Font font = PDType0Font.load(document, new File(fontPath));

		String store_name = (String) receivedNote.get("supplier_name");
		String date = (String) receivedNote.get("received_note_date");

		myTextClass.addSingleLineText("Phiếu nhập", 230, pageHeight - 50, font, 24, Color.BLACK);
		myTextClass.addSingleLineText("Nhà cung cấp: " + store_name, 25, pageHeight - 100, font, 14, Color.BLACK);
		myTextClass.addSingleLineText("Ngày nhập: " + date, 25, pageHeight - 124, font, 14, Color.BLACK);

		MyTableClass myTable = new MyTableClass(document, contentStream);
		int[] cellWidths = { 30, 230, 50, 70, 70, 90 };
		myTable.setTable(cellWidths, 30, 25, pageHeight - 190);
		myTable.setTableFont(font, 16, Color.BLACK);

		myTable.addCell("ID");
		myTable.addCell("Tên SP");
		myTable.addCell("ĐVT");
		myTable.addCell("Số lượng");
		myTable.addCell("Giá nhập");
		myTable.addCell("Thành tiền");

		details.forEach(item -> {
			try {
				myTable.addCell(item.get("product_id") + "");
				myTable.addCell(item.get("product_name") + "");
				myTable.addCell(item.get("product_unit") + "");
				myTable.addCell(item.get("received_quantity") + "");
				myTable.addCell(item.get("received_price") + "");
				myTable.addCell(((int) item.get("received_price") * (int) item.get("received_quantity")) + "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		String total = receivedNote.get("received_note_total").toString();
		myTextClass.addSingleLineText("Tổng: " + total, 450, myTable.yPosition, font, 16, Color.BLACK);

		contentStream.close();
		String pdfPath = "D:\\" + "received-note-" + date + ".pdf";
		document.save(pdfPath);
		document.close();
	}

	private static class MyTextClass {
		PDPageContentStream contentStream;

		public MyTextClass(PDDocument document, PDPageContentStream contentStream) {
			this.contentStream = contentStream;
		}

		void addSingleLineText(String text, int xPosition, int yPosition, PDType0Font font, float fontSize, Color color)
				throws IOException {
			contentStream.beginText();
			contentStream.setFont(font, fontSize);
			contentStream.setNonStrokingColor(color);
			contentStream.newLineAtOffset(xPosition, yPosition);
			contentStream.showText(text);
			contentStream.endText();
			contentStream.moveTo(0, 0);
		}
	}

	private static class MyTableClass {
		PDPageContentStream contentStream;
		private int[] colWidths;
		private int cellHeight;
		private int xPosition;
		private int yPosition;
		private int colPosition;
		private int xInitialPosition;
		private Color fontColor;
		private PDType0Font font;
		private float fontSize;

		public MyTableClass(PDDocument document, PDPageContentStream contentStream) {
			this.contentStream = contentStream;
		}

		void setTable(int[] colWidths, int cellHeight, int xPosition, int yPosition) {
			this.colWidths = colWidths;
			this.cellHeight = cellHeight;
			this.xPosition = xPosition;
			this.yPosition = yPosition;
			this.xInitialPosition = xPosition;
		}

		void setTableFont(PDType0Font font, float fontSize, Color fontColor) {
			this.font = font;
			this.fontSize = fontSize;
			this.fontColor = fontColor;
		}

		void addCell(String text) throws IOException {
			contentStream.setStrokingColor(0, 0, 0);
			contentStream.addRect(xPosition, yPosition, colWidths[colPosition], cellHeight);
			contentStream.stroke();

			// Split text into lines that fit within the cell width
			List<String> lines = splitTextIntoLines(text, colWidths[colPosition] - 4, font, fontSize);

			// Draw each line within the cell
			float leading = 1.2f * fontSize; // line height
			float textYPosition = yPosition + cellHeight - fontSize;
			for (String line : lines) {
				contentStream.beginText();
				contentStream.setNonStrokingColor(fontColor);
				contentStream.setFont(font, fontSize);
				contentStream.newLineAtOffset(xPosition + 2, textYPosition);
				contentStream.showText(line);
				contentStream.endText();
				textYPosition -= leading;
				if (textYPosition < yPosition + 4) {
					break; // Stop drawing if text exceeds cell height
				}
			}

			xPosition += colWidths[colPosition];
			colPosition++;

			if (colPosition == colWidths.length) {
				colPosition = 0;
				xPosition = xInitialPosition;
				yPosition -= cellHeight;
			}
		}

		private List<String> splitTextIntoLines(String text, int maxWidth, PDType0Font font, float fontSize)
				throws IOException {
			List<String> lines = new ArrayList<>();
			StringBuilder currentLine = new StringBuilder();
			StringBuilder currentWord = new StringBuilder();

			for (char c : text.toCharArray()) {
				currentWord.append(c);
				if (c == ' ' || c == '-') {
					if (font.getStringWidth(currentLine.toString() + currentWord.toString()) / 1000
							* fontSize > maxWidth) {
						lines.add(currentLine.toString().trim());
						currentLine.setLength(0);
					}
					currentLine.append(currentWord);
					currentWord.setLength(0);
				}
			}
			currentLine.append(currentWord);
			lines.add(currentLine.toString().trim());

			return lines;
		}
	}
}
