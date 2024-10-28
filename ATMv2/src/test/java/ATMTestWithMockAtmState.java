import domain.ATM;
import domain.AtmState;
import domain.Bill;
import domain.BillType;
import exception.InsufficientATMBalanceException;
import exception.InsufficientBillQuantityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ATMTestWithMockAtmStateTest {

    int MAX_COIN_VALUE = 2;

    @Mock
    List<Bill> currencyInventory;

    @Mock
    AtmState atmState;

    @InjectMocks
    ATM atm;


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

        /* the question about dealing with simple and complex helpers which are private but needed
         * in your test to preprocess your inputs. eg: convertToBill could have been used here but it's
         * private. what if it contained complex logic? could extract it to a utility class? how about if it
         * also depends on the dependencies of the class? do I have to pass all 10 dependencies to it? */

        // is it best practice to mock a collection dependency?
        // should I use the actual dependencies and test the main business logic for the sake of time?
        // eg: don't write test for atmState. use if directly in testing my ATM.

        when(atmState.hasCurrency(any(Bill.class))).thenReturn(true);
        when(atmState.getCurrencyInventory()).thenReturn(currencyInventory);
        when(atmState.getCurrencyInventory().contains(any(Bill.class))).thenReturn(true);

        assertThat(atm.withdraw(amount)).isEqualTo(Map.of(primitiveBill, 1));
        verify(atmState).hasCurrency(any(Bill.class));
    }

//    @ParameterizedTest
//    @MethodSource("withdrawComplexMoneyParams")
//    void withdrawComplexMoney_WhenSufficientBillTypesAvailable_ShouldReturnDefaultBreakdown(
//            int amount, Map<domain.Bill, Integer> expectedMoney ) throws InsufficientBillQuantityException, InsufficientATMBalanceException {
//        assertThat(atm.withdraw(amount))
//                .as("check %d withdrawal amount", amount)
//                .isEqualTo(expectedMoney);
//
//    }

//    private static Stream<Arguments> withdrawComplexMoneyParams() {
//        domain.Bill bill_500 = new domain.Bill(domain.BillType.NOTE, 500);
//        domain.Bill bill_200 = new domain.Bill(domain.BillType.NOTE, 200);
//        domain.Bill bill_100 = new domain.Bill(domain.BillType.NOTE, 100);
//        domain.Bill bill_50 = new domain.Bill(domain.BillType.NOTE, 50);
//        domain.Bill bill_20 = new domain.Bill(domain.BillType.NOTE, 20);
//        domain.Bill bill_10 = new domain.Bill(domain.BillType.NOTE, 10);
//        domain.Bill bill_5 = new domain.Bill(domain.BillType.NOTE, 5);
//        domain.Bill coin_2 = new domain.Bill(domain.BillType.COIN, 2);
//        domain.Bill coin_1 = new domain.Bill(domain.BillType.COIN, 1);
//
//        return Stream.of(
//                Arguments.of(434, Map.of(bill_200, 2, bill_20, 1, bill_10, 1, coin_2, 2)),
//                Arguments.of(700, Map.of(bill_500, 1, bill_200, 1)),
//                Arguments.of(888, Map.of(bill_500, 1, bill_200, 1, bill_100, 1, bill_50, 1, bill_20, 1, bill_10, 1, bill_5, 1, coin_2, 1, coin_1, 1)),
//                Arguments.of(1234, Map.of(bill_500, 2, bill_200, 1, bill_20, 1, bill_10, 1, coin_2, 2)),
//                Arguments.of(998, Map.of(bill_500, 1, bill_200, 2, bill_50, 1, bill_20, 2, bill_5, 1, coin_2, 1, coin_1, 1)),
//                Arguments.of(93, Map.of(bill_50, 1, bill_20, 2, coin_2, 1, coin_1, 1)),
//                Arguments.of(28, Map.of(bill_20, 1, bill_5, 1, coin_2, 1, coin_1, 1)),
//                Arguments.of(3, Map.of(coin_2, 1, coin_1, 1)),
//                Arguments.of(35, Map.of(bill_20, 1, bill_10, 1, bill_5, 1)),
//                Arguments.of(55, Map.of(bill_50, 1, bill_5, 1))
//        );
//    }
//
//    @Test
//    void withdraw_WhenATMBalanceInsufficient_ShouldThrowInsufficientATMBalanceException(){
//        assertThatThrownBy(() -> atm.withdraw(10_000))
//                .isInstanceOf(exception.InsufficientATMBalanceException.class)
//                .hasMessage("The domain.ATM machine has not enough money, please go to the nearest atm machine");
//    }

}

