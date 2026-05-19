import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class CalculatorFrame extends JFrame {

    private JLabel displayLabel;
    private JPanel keypadPanel;
    private JPanel buttonPanel;

    public CalculatorFrame() {
        // ウィンドウ設定

        // タイトル
        setTitle("Calculator");

        // 画面サイズ
        setSize(400, 500);

        // ウィンドウを閉じる際の動作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 表示欄設定
        displayLabel = new JLabel("0", SwingConstants.RIGHT);
        displayLabel.setFont(new Font("Arial", Font.BOLD, 30));
        displayLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(displayLabel, BorderLayout.NORTH);

        // パネル設定
        keypadPanel = new JPanel(new BorderLayout());
        add(keypadPanel, BorderLayout.CENTER);

        // ボタンパネル設定
        buttonPanel = new JPanel(new GridLayout(5, 4));

        String[] buttons = {
                "7", 
        }
    }
}