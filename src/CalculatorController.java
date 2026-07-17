public class CalculatorController {
    private CalculatorModel model;
    private CalculatorFrame view;

    public CalculatorController(CalculatorFrame view, CalculatorModel model){
        this.view = view;
        this.model = model;
    }

    /**
     * 数値ボタン押下時の処理
     * @param ch
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

    public void onOperator(Operator op){
        model.inputOperator(op);
        view.setDisplay(model.getDisplayText());
    }

    public void onEquals(){
        model.equalsOp();
        view.setDisplay(model.getDisplayText());
    }

    public void onClear(){
        model.clearAll();
        view.setDisplay(model.getDisplayText());
    }
}