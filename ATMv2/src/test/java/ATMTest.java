import domain.ATM;
import domain.AtmState;
import domain.Bill;
import domain.BillType;
import exception.InsufficientATMBalanceException;
import exception.InsufficientBillQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ATMTest {

    ATM atm;
    AtmState atmState;
    private static final int MAX_COIN_VALUE = 2;

    private static final Bill note_500 = new Bill(BillType.NOTE, 500);
    private static final Bill note_200 = new Bill(BillType.NOTE, 200);
    private static final Bill note_100 = new Bill(BillType.NOTE, 100);
    private static final Bill note_50 = new Bill(BillType.NOTE, 50);
    private static final Bill note_20 = new Bill(BillType.NOTE, 20);
    private static final Bill note_10 = new Bill(BillType.NOTE, 10);
    private static final Bill note_5 = new Bill(BillType.NOTE, 5);
    private static final Bill coin_2 = new Bill(BillType.COIN, 2);
    private static final Bill coin_1 = new Bill(BillType.COIN, 1);


    @BeforeEach
    void initialize(){
        Map<Bill, Integer> cashReserves = new HashMap<>();
        cashReserves.put(note_500, 2);
        cashReserves.put(note_200, 3);
        cashReserves.put(note_100, 5);
        cashReserves.put(note_50, 12);
        cashReserves.put(note_20, 20);
        cashReserves.put(note_10, 50);
        cashReserves.put(note_5, 100);
        cashReserves.put(coin_2, 250);
        cashReserves.put(coin_1, 500);
        atmState = new AtmState(cashReserves);
        atm = new ATM(atmState);
    }

    @Test
    void withdrawZeroAmount_ShouldThrowIllegalArgException(){

        assertThatThrownBy(() -> atm.withdraw(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Withdrawal amount must be greater than 0");
    }


    @ParameterizedTest
    @ValueSource(ints = {500, 200, 100, 50, 20, 10, 5, 2, 1})
    void withdrawPrimitiveBill_ShouldReturnPrimitiveBill(int amount) throws InsufficientBillQuantityException, InsufficientATMBalanceException {
        Bill primitiveBill = amount > MAX_COIN_VALUE ? new Bill(BillType.NOTE, amount): new Bill(BillType.COIN, amount);
        assertThat(atm.withdraw(amount)).isEqualTo(Map.of(primitiveBill, 1));
    }

    @ParameterizedTest
    @MethodSource("withdrawComplexMoneyParams")
    void withdrawComplexMoney_WhenSufficientBillTypesAvailable_ShouldReturnDefaultBreakdown(
            int amount, Map<Bill, Integer> expectedMoney ) throws InsufficientBillQuantityException, InsufficientATMBalanceException {
        assertThat(atm.withdraw(amount))
                .as("check %d withdrawal amount", amount)
                .isEqualTo(expectedMoney);

    }

    private static Stream<Arguments> withdrawComplexMoneyParams() {

        return Stream.of(
                Arguments.of(434, Map.of(note_200, 2, note_20, 1, note_10, 1, coin_2, 2)),
                Arguments.of(700, Map.of(note_500, 1, note_200, 1)),
                Arguments.of(888, Map.of(note_500, 1, note_200, 1, note_100, 1, note_50, 1, note_20, 1, note_10, 1, note_5, 1, coin_2, 1, coin_1, 1)),
                Arguments.of(1234, Map.of(note_500, 2, note_200, 1, note_20, 1, note_10, 1, coin_2, 2)),
                Arguments.of(998, Map.of(note_500, 1, note_200, 2, note_50, 1, note_20, 2, note_5, 1, coin_2, 1, coin_1, 1)),
                Arguments.of(93, Map.of(note_50, 1, note_20, 2, coin_2, 1, coin_1, 1)),
                Arguments.of(28, Map.of(note_20, 1, note_5, 1, coin_2, 1, coin_1, 1)),
                Arguments.of(3, Map.of(coin_2, 1, coin_1, 1)),
                Arguments.of(35, Map.of(note_20, 1, note_10, 1, note_5, 1)),
                Arguments.of(55, Map.of(note_50, 1, note_5, 1))
        );
    }

    @Test
    void withdraw_WhenATMBalanceInsufficient_ShouldThrowInsufficientATMBalanceException(){
        assertThatThrownBy(() -> atm.withdraw(10_000))
                .isInstanceOf(exception.InsufficientATMBalanceException.class)
                .hasMessage("The ATM machine does not have enough money, please go to the nearest atm machine");
    }

}

