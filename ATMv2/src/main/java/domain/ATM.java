package domain;

import exception.InsufficientATMBalanceException;
import exception.InsufficientBillQuantityException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ATM {

    private final AtmState atmState;
    private final int MAX_COIN_VALUE = 2;

    public ATM(AtmState atmState) {
        this.atmState = atmState;
    }

    public Map<Bill, Integer> withdraw(int amount) throws InsufficientATMBalanceException, InsufficientBillQuantityException {
        if(amount <= 0){
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0");
        }
        if(isPrimitiveCurrencyValue(amount)){
            Bill bill = convertToBill(amount);
            if(atmState.hasCurrency(bill)) {
                atmState.updateBillQuantity(bill, 1);
                return Map.of(bill, 1);
            }
        }
        return processComplexWithdrawal(amount);
    }


    private Map<Bill, Integer> processComplexWithdrawal(int amountToWithdraw) throws InsufficientATMBalanceException, InsufficientBillQuantityException {
        Map<Bill, Integer> breakdown = new LinkedHashMap<>();
        int remainingQuantity;
        int quantityOfBillsRequired;
        List<Bill> bills = atmState.getCurrencyInventory();

        for(Bill bill : bills){
            quantityOfBillsRequired = amountToWithdraw / bill.getValue();
            if(quantityOfBillsRequired ==  0)
                continue;

            remainingQuantity = atmState.getBillQuantity(bill) - quantityOfBillsRequired;
            if(remainingQuantity < 0) {
                amountToWithdraw -= bill.getValue() * atmState.getBillQuantity(bill);
                breakdown.put(bill, atmState.getBillQuantity(bill));
                atmState.updateBillQuantity(bill, 0);
                continue;
            }
            amountToWithdraw -= bill.getValue() * quantityOfBillsRequired;
            atmState.updateBillQuantity(bill, quantityOfBillsRequired);
            breakdown.put(bill, quantityOfBillsRequired);

            if(amountToWithdraw == 0){
                System.out.println(breakdown);
                return breakdown;
            }

        }

        throw new InsufficientATMBalanceException("The ATM machine does not have enough money, " +
                "please go to the nearest atm machine");
    }

    private boolean isPrimitiveCurrencyValue(int amount){
        return atmState.getCurrencyInventory().contains(convertToBill(amount));
    }

    private Bill convertToBill(int amount){
        return amount > MAX_COIN_VALUE ? new Bill(BillType.NOTE, amount): new Bill(BillType.COIN, amount);
    }
}
