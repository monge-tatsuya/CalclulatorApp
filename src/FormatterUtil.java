import java.math.BigDecimal;
import java.text.DecimalFormat;

public class FormatterUtil {

    public String formatForDisplay(BigDecimal v, int maxDigits) {
        String text = v.stripTrailingZeros().toPlainString(); // 不要な末尾の0を削除して文字列に変換する。

        String replaceDot = text.replace(".", "").replace("-", ""); // 小数点と負号を桁数に含めないための変数。

        if (replaceDot.length() > maxDigits) {
            // 最大8桁を超える場合は指数表示にする。
            DecimalFormat format = new DecimalFormat("0.0000000E0");
            return format.format(v).replace("E", "e");
        }

        return text;
    }
}
