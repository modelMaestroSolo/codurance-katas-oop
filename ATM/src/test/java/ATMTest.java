import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ATMTest {

    ATM atm;

    Map<Money, Integer> initialState = new HashMap<>();


    @BeforeEach
    void initialize(){
        initialState.put(Money.BILL_500, 2);
        initialState.put(Money.BILL_200, 3);
        initialState.put(Money.BILL_100, 5);
        initialState.put(Money.BILL_50, 12);
        initialState.put(Money.BILL_20, 20);
        initialState.put(Money.BILL_10, 50);
        initialState.put(Money.BILL_5, 100);
        initialState.put(Money.COIN_2, 250);
        initialState.put(Money.COIN_1, 500);
        atm = new ATM(initialState);
    }

    @ParameterizedTest
    @CsvSource(
            {
            "500, BILL_500",
            "200, BILL_200",
            "100, BILL_100",
            "50, BILL_50",
            "10, BILL_10",
            "5, BILL_5",
            "2, COIN_2",
            "1, COIN_1"
            }
    )
    void withdrawPrimitiveMoney_ShouldReturnPrimitiveMoney(int amount, String expected){
        Money expectedMoney = Money.valueOf(expected);
        assertThat(atm.withdraw(amount)).isEqualTo(Map.of(expectedMoney, 1));
    }

    @Test
    void withdraw700_ShouldReturnOne500AndOne200Bill(){
        Map<Money, Integer> expectedMoney = new HashMap<>();
        expectedMoney.put(Money.BILL_500, 1);
        expectedMoney.put(Money.BILL_200, 1);

        assertThat(atm.withdraw(700))
                .isEqualTo(expectedMoney);
    }

    @ParameterizedTest
    @MethodSource("withdrawComplexMoneyParams")
    void withdrawComplexMoney_WhenSufficientMoneyTypesAvailable_ShouldReturnDefaultBreakdown(
            int amount, Map<Money, Integer> expectedMoney ) {
        assertThat(atm.withdraw(amount))
                .as("check %d withdrawal amount", amount)
                .isEqualTo(expectedMoney);

    }

    private static Stream<Arguments> withdrawComplexMoneyParams() {
        return Stream.of(
                Arguments.of(434, Map.of(Money.BILL_200, 2, Money.BILL_20, 1, Money.BILL_10, 1, Money.COIN_2, 2)),
                Arguments.of(700, Map.of(Money.BILL_500, 1, Money.BILL_200, 1)),
                Arguments.of(888, Map.of(Money.BILL_500, 1, Money.BILL_200, 1, Money.BILL_100, 1, Money.BILL_50, 1, Money.BILL_20, 1, Money.BILL_10, 1, Money.BILL_5, 1, Money.COIN_2, 1, Money.COIN_1, 1)),
                Arguments.of(1234, Map.of(Money.BILL_500, 2, Money.BILL_200, 1, Money.BILL_20, 1, Money.BILL_10, 1, Money.COIN_2, 2)),
                Arguments.of(998, Map.of(Money.BILL_500, 1, Money.BILL_200, 2, Money.BILL_50, 1, Money.BILL_20, 2, Money.BILL_5, 1, Money.COIN_2, 1, Money.COIN_1, 1)),
                Arguments.of(93, Map.of(Money.BILL_50, 1, Money.BILL_20, 2, Money.COIN_2, 1, Money.COIN_1, 1)),
                Arguments.of(28, Map.of(Money.BILL_20, 1, Money.BILL_5, 1, Money.COIN_2, 1, Money.COIN_1, 1)),
                Arguments.of(3, Map.of(Money.COIN_2, 1, Money.COIN_1, 1)),
                Arguments.of(35, Map.of(Money.BILL_20, 1, Money.BILL_10, 1, Money.BILL_5, 1)),
                Arguments.of(55, Map.of(Money.BILL_50, 1, Money.BILL_5, 1))
        );
    }

    @Test
    void withdraw_WhenATMBalanceInsufficient_ShouldPrintErrorMessage(){
        assertThatThrownBy(() -> atm.withdraw(10_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The ATM machine has not enough money, please go to the nearest atm machine");
    }

}

