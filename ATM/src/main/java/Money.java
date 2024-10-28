public enum Money {
    BILL_500(500),
    BILL_200(200),
    BILL_100(100),
    BILL_50(50),
    BILL_20(20),
    BILL_10(10),
    BILL_5(5),
    COIN_2(2),
    COIN_1(1);

    private final int value;

    Money(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
