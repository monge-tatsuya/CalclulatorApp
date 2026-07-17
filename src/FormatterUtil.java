import java.math.BigDecimal;
import java.text.DecimalFormat;

public class FormatterUtil {

    public String formatForDisplay(BigDecimal v, int maxDigits) {
        String text = v.toPlainString(); // BigDecimalを見字列に変換する。

        String replaceDot = text.replace(".", ""); // 小数点を桁数に含めないための変数。

        if (replaceDot.length() > maxDigits) {
            // 最大8桁を超える場合は指数表示にする。
            DecimalFormat format = new DecimalFormat("0.0000000E0");
            return format.format(v).replace("E", "e");
        }

        return text;
    }
}
