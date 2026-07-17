public enum InputState {
    READY, //初期状態 or 計算結果表示中
    INPUT_NUMBER, //数値入力中
    INPUT_OPERATOR, //演算子入力中
    ERROR //エラー状態
}