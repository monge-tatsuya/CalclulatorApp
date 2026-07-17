public class CalculatorApp{
    public static void main(String[] args){
        CalculatorFrame frame = new CalculatorFrame();
        CalculatorModel model = new CalculatorModel();
        CalculatorController controller = new CalculatorController(frame, model);
        frame.bindController(controller);
    }
}
