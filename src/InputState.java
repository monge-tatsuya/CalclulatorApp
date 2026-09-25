public enum InputState {
    READY, //初期状態
    INPUT_NUMBER, //数値入力中
    INPUT_OPERATOR, //演算子入力中
    RESULT, //計算結果表示中(クラス図には記載されていません。)
    ERROR //エラー状態
}