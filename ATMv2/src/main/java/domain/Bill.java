package domain;

import java.util.Objects;

public class Bill implements Comparable<Bill> {
    private final int value;
    private final BillType billType;

    public Bill(BillType billType, int value) {
        this.billType = billType;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public BillType getBillType() {
        return billType;
    }

    @Override
    public int compareTo(Bill that) {
        return Integer.compare(this.value, that.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return value == bill.value && billType == bill.billType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, billType);
    }

    @Override
    public String toString() {
        return billType + "_" + value;
    }
}
