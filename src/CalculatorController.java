public class CalculatorController {
    private CalculatorModel model;
    private CalculatorFrame view;

    public CalculatorController(CalculatorFrame view, CalculatorModel model){
        this.view = view;
        this.model = model;
    }

    /**
     * 数値ボタン押下時の処理
     * @param ch 押下された数値
     */
    public void onDigit(char ch){
        model.appendDigit(ch);
        view.setDisplay(model.getDisplayText());
    }

    /**
     * 小数点ボタン押下時の処理
     */
    public void onDot(){
        model.appendDot();
        view.setDisplay(model.getDisplayText());
    }

    /**
     * 演算ボタン押下時の処理
     * @param op 押下された演算子
     */
    public void onOperator(Operator op){
        model.inputOperator(op);
        view.setDisplay(model.getDisplayText());
    }

    /**
     * イコールボタン押下時の処理
     */
    public void onEquals(){
        model.equalsOp();
        view.setDisplay(model.getDisplayText());
    }

    /**
     * クリアボタン押下時の処理
     */
    public void onClear(){
        model.clearAll();
        view.setDisplay(model.getDisplayText());
    }
}