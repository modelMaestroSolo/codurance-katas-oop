import java.util.HashMap;
import java.util.Map;

public class ATM {

    private final Map<Money, Integer> balance;
    private final int MAX_COIN_VALUE = 2;

    public ATM(Map<Money, Integer> initialState) {
        this.balance = initialState;
    }

    public Map<Money, Integer> withdraw(int amount) {
        String stringAmount;
        try {
            stringAmount = amount > MAX_COIN_VALUE ? "BILL_" + amount: "COIN_" + amount;
            Money withdrawalAmount = Money.valueOf(stringAmount);
            return Map.of(withdrawalAmount, 1);
        } catch (IllegalArgumentException e) {
            return processWithdrawal(amount); //handle exception properly
        }
    }


    public Map<Money, Integer> processWithdrawal(int amount){
        Map<Money, Integer> breakdown = new HashMap<>();
        int remainder;
        int quantity;

        for(Money money : Money.values()){ //sort values
            if(amount == 0)
                return breakdown;
            quantity = amount / money.getValue();
            if(quantity ==  0)
                continue;

            remainder = balance.get(money) - quantity;
            if(remainder < 0) {
                amount -= money.getValue() * balance.get(money);
                breakdown.put(money, balance.get(money));
                balance.replace(money, 0);
                continue;
            }
            amount -= money.getValue() * quantity;
            balance.replace(money, balance.get(money) - quantity);
            breakdown.put(money, quantity);
        }
        if(amount != 0)
            throw new IllegalArgumentException("The ATM machine has not enough money, please go to the nearest atm machine");
        return breakdown;
    }

}
