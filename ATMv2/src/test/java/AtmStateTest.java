import domain.AtmState;
import domain.Bill;
import domain.BillType;
import exception.InsufficientBillQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AtmStateTest {

    AtmState atmState;
    
    @BeforeEach
    void initialize(){
        Map<Bill, Integer> cashReserves = new HashMap<>();
        cashReserves.put(new Bill(BillType.NOTE, 500), 2);
        cashReserves.put(new Bill(BillType.NOTE, 200), 3);
        cashReserves.put(new Bill(BillType.NOTE, 100), 5);
        cashReserves.put(new Bill(BillType.NOTE, 50), 12);
        cashReserves.put(new Bill(BillType.NOTE, 20), 20);
        cashReserves.put(new Bill(BillType.NOTE, 10), 50);
        cashReserves.put(new Bill(BillType.NOTE, 5), 100);
        cashReserves.put(new Bill(BillType.COIN, 2), 250);
        cashReserves.put(new Bill(BillType.COIN, 1), 500);
        atmState = new AtmState(cashReserves);
    }

    @ParameterizedTest
    @CsvSource({"500, 2", "200, 3", "100, 5", "50, 12", "20, 20", "10, 50", "5, 100"})
    void getBillQuantity_Note(int value, int expectedQuantity) {
        Bill bill = new Bill(BillType.NOTE, value);
        assertThat(atmState.getBillQuantity(bill))
                .as("check %d value", value)
                .isEqualTo(expectedQuantity);
    }
    
    @ParameterizedTest
    @CsvSource({ "2, 250", "1, 500"})
    void getBillQuantity_Coin(int value, int expectedQuantity) {
        Bill bill = new Bill(BillType.COIN, value);
        assertThat(atmState.getBillQuantity(bill))
                .as("check %d value", value)
                .isEqualTo(expectedQuantity);
    }

    @Test
    void getCurrencyInventory() {

        assertThat(atmState.getCurrencyInventory())
                .isInstanceOf(List.class)
                .hasSize(9)
                .contains(new Bill(BillType.NOTE, 500))
                .contains(new Bill(BillType.NOTE, 200))
                .contains(new Bill(BillType.NOTE, 100))
                .contains(new Bill(BillType.NOTE, 50))
                .contains(new Bill(BillType.NOTE, 20))
                .contains(new Bill(BillType.NOTE, 10))
                .contains(new Bill(BillType.NOTE, 5))
                .contains(new Bill(BillType.COIN, 2))
                .contains(new Bill(BillType.COIN, 1))
                .isSorted();
    }

    @Test
    void updateBillQuantity_InsufficientBillQuantity_ThrowsInsufficientBillQuantityException(){
        Bill bill = new Bill(BillType.NOTE, 500);
        assertThatThrownBy(() -> atmState.updateBillQuantity(bill,3 ))
                .isInstanceOf(InsufficientBillQuantityException.class);
    }

    @ParameterizedTest
    @CsvSource({"500, 1, 1","200, 2, 1", "100, 1, 4", "50, 7, 5", "20, 20, 0", "10, 10, 40", "5, 50, 50",})
    void updateBillQuantity_Note(int value, int quantityDeducted, int quantityExpected) throws InsufficientBillQuantityException {
        Bill bill = new Bill(BillType.NOTE, value);
        atmState.updateBillQuantity(bill, quantityDeducted);

        assertThat(atmState.getBillQuantity(bill))
                .as("check %d value", value)
                .isEqualTo(quantityExpected);
    }

    @ParameterizedTest
    @CsvSource({ "2, 50, 200", "1, 499, 1"})
    void updateBillQuantity_Coin(int value, int quantityDeducted, int quantityExpected) throws InsufficientBillQuantityException {
        Bill bill = new Bill(BillType.COIN, value);
        atmState.updateBillQuantity(bill, quantityDeducted);

        assertThat(atmState.getBillQuantity(bill))
                .as("check %d value", value)
                .isEqualTo(quantityExpected);
    }

    @Test
    void hasCurrency_WhenBillQuantityNotZero_ShouldReturnTrue() {
        Bill bill = new Bill(BillType.NOTE, 500);
        assertThat(atmState.hasCurrency(bill)).isTrue();
    }

    @Test
    void hasCurrency_WhenBillQuantityIsZero_ShouldReturnFalse() throws InsufficientBillQuantityException {
        Bill bill = new Bill(BillType.NOTE, 500);
        atmState.updateBillQuantity(bill, atmState.getBillQuantity(bill));
        assertThat(atmState.hasCurrency(bill)).isFalse();
    }
}

