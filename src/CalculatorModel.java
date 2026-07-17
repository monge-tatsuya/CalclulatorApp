import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculatorModel {
    private BigDecimal leftOperand; //演算子が押下された時の左側の数値を保存する
    private StringBuilder currentInput; //現在入力中の数値
    private Operator pendingOp; //現在押下された演算子を保存する
    private InputState state; //入力情報を管理する
    private int maxDigits = 8; //最大8桁まで入力可能

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

        if (state == InputState.ERROR) {
            return false; //エラー状態時に数値ボタンは押下できない。
        }
        if (currentInput.length() >= maxDigits) {
            return false; //最大8桁を超えないようにする。
        }

        if (state == InputState.READY) {
            currentInput.setLength(0); //計算結果表示中に数値ボタンを押下すると、計算結果を削除し新しい数値の入力に遷移する。
        }

        if (currentInput.toString().equals("0")){
            currentInput.setLength(0); //最初に表示されている0を削除する。
        }

        if (state == InputState.INPUT_OPERATOR) {
            currentInput.setLength(0); //右辺側の数値を入力するために、currentInputを空にする。
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
        if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
            return true; //小数点がないときに小数点ボタン押下で小数点を追加する。
        }

        if (state == InputState.ERROR) {
            return false; //エラー状態時に小数点ボタンは押下できない。
        }

        return false; //すでに小数点がある場合には小数点ボタン押下を無視する。
    }

    /**
     * 演算子ボタン押下時の処理
     * @param op 押下された演算子
     */
    public void inputOperator(Operator op) {

        if (op == Operator.SUB && state == InputState.READY ) {
            currentInput.setLength(0);
            currentInput.append("-");
            state = InputState.INPUT_NUMBER; //負の数の入力のため
            return;
        }

        if (state == InputState.INPUT_OPERATOR) {
            pendingOp = op;
            return; //演算子が既に入力されている時に、再度演算子が押下された場合に演算子を更新する。
        }
        leftOperand = new BigDecimal(currentInput.toString()); //
        pendingOp = op; //押下された演算子を保存する。
        currentInput.setLength(0); //右側の数字を入力するときに、currentInputを空にする。
        state = InputState.INPUT_OPERATOR; //演算子が入力された状態へ遷移する。
    }

    /**
     * 実際に計算を実行する。
     */
    public void apply() {

        BigDecimal rightOperand = new BigDecimal(currentInput.toString());

        if (pendingOp == null) {
            return; //演算子が押下されていない場合は計算を行わない。
        }

        switch (pendingOp) {
            case ADD: //足し算
                leftOperand = leftOperand.add(rightOperand);
                break;

            case SUB: //引き算
                leftOperand = leftOperand.subtract(rightOperand);
                break;

            case MUL: //掛け算
                leftOperand = leftOperand.multiply(rightOperand);
                break;

            case DIV: //割り算
                if (rightOperand.compareTo(BigDecimal.ZERO) == 0) {
                    state = InputState.ERROR; //0除算時にエラーを表示する。
                    return;
                }

                leftOperand = leftOperand.divide(rightOperand, 10, RoundingMode.HALF_UP);
                break;

            default:
                state = InputState.ERROR; //上記以外の演算子を受け取ることはあり得ませんが念のため書いています。
                return;
        }
    }

    /**
     * イコールボタン押下時に計算結果を表示する。
     */
    public void equalsOp() {

        if (state == InputState.ERROR) {
            return; //エラー状態時にイコールボタンは押下できない。
        }

        if (pendingOp == null) {
            return; //演算子が押下されていない場合に＝ボタンを押下しても計算は行わない。
        }

        if (state == InputState.INPUT_OPERATOR) {
            pendingOp = null;
            state = InputState.READY;
            return; //「〇＋＝」の状態では演算子をリセットして最後の数値を維持して計算は行わない。
        }
        apply(); //計算結果を実行する。

        if (state == InputState.ERROR) {
            return; //計算結果がエラーの際に、エラー表示を行う。
        }

        currentInput = new StringBuilder(leftOperand.toPlainString());
        pendingOp = null; //演算子をリセットする。
        state = InputState.READY; //計算結果表示中へ遷移する。
    }

    /**
     * クリアボタン押下時に計算結果をリセットする。
     */
    public void clearAll() {
        leftOperand = BigDecimal.ZERO;
        currentInput.setLength(0);
        currentInput.append("0"); //初期値に戻す。
        pendingOp = null; //演算子をリセットする。

        state = InputState.READY; //初期状態へ遷移する。
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

        if (state == InputState.READY) {
            return formatter.formatForDisplay(leftOperand, maxDigits); //
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