import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculatorModel {

    /**
     * 割り算の計算結果の小数点以下の桁数を指定する。
     */
    private static final int DIV_SCALE = 10;

    /**
     * 演算子が押下された時の左側の数値を保存する。
     */
    private BigDecimal leftOperand;

    /**
     * 入力されている数値を保存する。
     */
    private StringBuilder currentInput;

    /**
     * 押下された演算子を保存する。
     */
    private Operator pendingOp;

    /**
     * 入力状態を管理する。
     */
    private InputState state;

    /**
     * 最大入力・表示桁数を管理する。
     * 8桁を超える計算結果となる場合には指数表示となる。
     */
    private int maxDigits = 8;

    public CalculatorModel() {
        leftOperand = BigDecimal.ZERO;
        currentInput = new StringBuilder("0");
        pendingOp = null;
        state = InputState.READY;
    }

    /**
     * 数値ボタン押下時の処理
     * @param ch 押下された数値
     * @return 数値が正常に追加されたかどうか
     */
    public boolean appendDigit(char ch) {
        //小数点と負号は桁数に含まないようにする。
        String digitOnly = currentInput.toString().replace(".", "").replace("-", "");

        //エラー状態時に数値ボタンは押下できない。
        if (state == InputState.ERROR) {
            return false;
        }

        //初期状態で「0」が押下された場合は状態を変化しない。
        if (state == InputState.READY && currentInput.toString().equals("0") && ch == '0') {
            return false;
        }

        //計算結果表示中に数値ボタンを押下すると、計算結果を削除し新しい数値の入力に遷移する。
        if (state == InputState.RESULT) {
            currentInput.setLength(0);
        }

        //最大8桁を超えないようにする。（小数点と負号は桁数に含まない。）
        if (digitOnly.length() >= maxDigits) {
            return false;
        }

        //-0の状態では数字の押下を無視する。
        if (currentInput.toString().equals("-0")) {
            return false;
        }

        //最初に表示されている0を削除する。
        if (currentInput.toString().equals("0")){
            currentInput.setLength(0);
        }

        //右辺側の数値を入力するために、currentInputを空にする。
        if (state == InputState.INPUT_OPERATOR) {
            currentInput.setLength(0);
        }

        currentInput.append(ch);
        state = InputState.INPUT_NUMBER; //数値が入力された状態へ遷移する。
        return true;
    }

    /**
     * 小数点ボタン押下時の処理
     * @return 小数点の追加が正常に成功したかどうか
     */
    public boolean appendDot() {

        //エラー状態時に小数点ボタンは押下できない。
        if (state == InputState.ERROR) {
            return false;
        }
        //演算子の次に小数点が押下された場合無視する。
        if (state == InputState.INPUT_OPERATOR) {
            return false;
        }

        //計算結果表示中は小数点は押下できない。
        if (state == InputState.RESULT) {
            return false;
        }
        //負号のみが入力されている状況で小数点が押下された場合無視する。
        if (currentInput.toString().equals("-")) {
            return false;
        }
        //小数点がないときに小数点ボタン押下で小数点を追加する。
        if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
            state = InputState.INPUT_NUMBER;
            return true;
        }
        //すでに小数点がある場合には小数点ボタン押下を無視する。
        return false;
    }

    /**
     * 演算子ボタン押下時の処理
     * @param op 押下された演算子
     */
    public void inputOperator(Operator op) {

        //エラー状態時には、どのボタン押下も受け付けない。
        if (state == InputState.ERROR) {
            return;
        }

        //負の数を入力する。
        if (op == Operator.SUB && state == InputState.READY) {
            currentInput.setLength(0);
            currentInput.append("-");
            state = InputState.INPUT_NUMBER;
            return;
        }

        //負号のみ、「-0」、「-0.」が入力されている状況で演算子を受け付けない。
        if (currentInput.toString().equals("-") || currentInput.toString().equals("-0") || currentInput.toString().equals("-0.") || currentInput.toString().equals("0.")) {
            return;
        }

        //初期状態時に負号以外の演算子を受け付けない。
        if (state == InputState.READY && op != Operator.SUB) {
            return;
        }

        //演算子が既に入力されている時に、再度演算子が押下された場合に演算子を更新する。
        if (state == InputState.INPUT_OPERATOR) {
            pendingOp = op;
            return;
        }

        //連続計算時の処理。「
        if (state == InputState.INPUT_NUMBER && pendingOp != null) {
            BigDecimal result;//計算結果を実行

            //連続計算時に0除算が発生した場合のエラー処理。
            try {
                result = apply();
            } catch (ArithmeticException e) {
                ErrorHandler errorHandler = new ErrorHandler();
                errorHandler.handle(e);
                state = InputState.ERROR;
                return;
            }
            leftOperand = result; //計算結果を左側の数値とする。
            pendingOp = op; //押下された演算子を保存する。
            state = InputState.INPUT_OPERATOR; //演算子が入力された状態へ遷移する。
            currentInput.setLength(0);
            return;
        }

        leftOperand = new BigDecimal(currentInput.toString());
        pendingOp = op; //押下された演算子を保存する。
        currentInput.setLength(0); //右側の数字を入力するときに、currentInputを空にする。
        state = InputState.INPUT_OPERATOR; //演算子が入力された状態へ遷移する。
    }

    /**
     * 実際に計算を実行する。
     */
    public BigDecimal apply() {

        BigDecimal rightOperand = new BigDecimal(currentInput.toString());

        switch (pendingOp) {
            case ADD: //足し算
                return leftOperand.add(rightOperand);

            case SUB: //引き算
                return leftOperand.subtract(rightOperand);

            case MUL: //掛け算
                return leftOperand.multiply(rightOperand);

            case DIV: //割り算

                //0除算時のエラー処理
                if (rightOperand.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("0で割ることはできません。");
                }
                //割り算の計算結果の小数点以下の桁数を指定する。
                return leftOperand.divide(rightOperand, DIV_SCALE, RoundingMode.HALF_UP);

            default:
                state = InputState.ERROR; //上記以外の演算子を受け取ることはあり得ませんが念のため書いています。
                return null;
        }
    }

    /**
     * イコールボタン押下時に計算結果を表示する。
     */
    public void equalsOp() {
        //エラー状態時の時にイコールボタンは押下できない。
        if (state == InputState.ERROR) {
            return;
        }

        //演算子が押下されていない場合に＝ボタンを押下しても計算は行わない。
        if (pendingOp == null) {
            return;
        }

        //「〇＋＝」の状態では押下は無視される。
        if (state == InputState.INPUT_OPERATOR) {
            return;
        }

        BigDecimal result;

        //計算結果をエラーと正常に分岐させて処理する。
        try {
            result = apply();
        } catch (ArithmeticException e) {
            //エラー処理
            ErrorHandler errorHandler = new ErrorHandler();
            errorHandler.handle(e);
            state = InputState.ERROR;
            //エラー時にはこれ以上何もせずに処理を終了する。
            return;
        }

        //計算結果を続けて計算に使用できるようにするために、左側の数値とする。
        leftOperand = result;

        currentInput = new StringBuilder(result.toPlainString());
        pendingOp = null; //演算子をリセットする。
        state = InputState.RESULT; //計算結果表示中へ遷移する。
    }

    /**
     * クリアボタン押下時に計算結果をリセットする。
     */
    public void clearAll() {
        leftOperand = BigDecimal.ZERO;
        currentInput.setLength(0);
        currentInput.append("0"); //初期値に戻す。
        pendingOp = null; //演算子をリセットする。

        //初期状態へ遷移する。
        state = InputState.READY;
    }

    /**
     * 現在の入力内容を表示する。
     * @return 表示欄に表示する文字列。
     */
    public String getDisplayText() {

        FormatterUtil formatter = new FormatterUtil();

        if (state == InputState.ERROR) {
            return "エラー"; //エラー状態の時に表示する文字。
        }

        if (state == InputState.RESULT) {
            return formatter.formatForDisplay(leftOperand, maxDigits); //計算結果の表示。
        }

        if (pendingOp != null) {

            String symbol = " "; //演算子を表示するための変数。

            switch (pendingOp) {
                case ADD: //足し算
                    symbol = "+";
                    break;

                case SUB: //引き算
                    symbol = "-";
                    break;

                case MUL: //掛け算
                    symbol = "×";
                    break;

                case DIV: //割り算
                    symbol = "÷";
                    break;
            }

            return formatter.formatForDisplay(leftOperand, maxDigits) + " " + symbol + " " + currentInput.toString();
        }
        return currentInput.toString();
    }
}