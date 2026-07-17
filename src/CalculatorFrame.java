import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class CalculatorFrame extends JFrame {

    private JLabel displayLabel;
    private JPanel keypadPanel;
    private CalculatorController c;

    public CalculatorFrame() {
        // ウィンドウ設定

        // タイトル
        setTitle("Calculator");

        // 画面サイズ
        setSize(400, 600);

        // ウィンドウを閉じる際の動作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 表示欄設定
        displayLabel = new JLabel("0", SwingConstants.RIGHT);
        displayLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 35));
        displayLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 20));
        add(displayLabel, BorderLayout.NORTH);

        // パネル設定
        keypadPanel = new JPanel(new BorderLayout());
        keypadPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(keypadPanel, BorderLayout.CENTER);

        // ボタンパネル設定
        keypadPanel.setLayout(new GridLayout(5, 4, 7, 7));

        String[] buttons = {
                "7", "8", "9", "÷",
                "4", "5", "6", "×",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C", "", "", ""
        };

        for (String text : buttons) {

            if (text.isEmpty()) {
                keypadPanel.add(new JLabel());
            } else {
                JButton button = new JButton(text);
                button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
                button.setFocusPainted(false);
                button.addActionListener(e -> {

                    if (text.matches("[0-9]")){
                        // 数字ボタン押下時の処理
                        c.onDigit(text.charAt(0));

                    } else if (text.equals(".")){
                        // 小数点ボタン押下時の処理
                        c.onDot();

                    } else if (text.equals("=")){
                        // イコールボタン押下時の処理
                        c.onEquals();

                    }else if (text.equals("C")){
                        // クリアボタン押下時の処理
                        c.onClear();

                    }else if (text.equals("+")){
                        //足し算ボタン押下時の処理
                        c.onOperator(Operator.ADD);

                    }else if (text.equals("-")){
                        //引き算ボタン押下時の処理
                        c.onOperator(Operator.SUB);

                    }else if (text.equals("×")){
                        //掛け算ボタン押下時の処理
                        c.onOperator(Operator.MUL);

                    }else if (text.equals("÷")){
                        //割り算ボタン押下時の処理
                        c.onOperator(Operator.DIV);
                    }
                });
                keypadPanel.add(button);
            }
        }

        add(keypadPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void setDisplay(String text) {
        displayLabel.setText(text);
    }

    public void bindController(CalculatorController c) {
        // Controllerをバインドする処理
        this.c = c;
    }

    public String getDisplay(){
        return displayLabel.getText(); //表示欄のテキストを取得する
    }
}