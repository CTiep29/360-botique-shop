package libraries;

import net.htmlparser.jericho.CharacterReference;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Utilities_Helper {
	public static String encode(String unicode) {
		return CharacterReference.encode(unicode);
	}

	public static String decode(String html) {
		return CharacterReference.decode(html);
	}
	
	public static String formatNumber(long number) {
        // Định dạng số với dấu chấm phân cách hàng nghìn
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
        decimalFormat.setDecimalFormatSymbols(symbols);

        return decimalFormat.format(number);
    }
}
