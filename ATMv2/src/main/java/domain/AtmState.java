package domain;

import exception.InsufficientBillQuantityException;

import java.util.*;

public class AtmState {
    ;
    private final Map<Bill, Integer> cashReserves;

    public AtmState(Map<Bill, Integer> cashReserves) {
        this.cashReserves = cashReserves;
    }

    public int getBillQuantity(Bill bill){
        return cashReserves.get(bill);
    }

    public List<Bill> getCurrencyInventory(){
        List<Bill> sortedKeys = new ArrayList<>(cashReserves.keySet());
        sortedKeys.sort(Collections.reverseOrder());
        return sortedKeys;
    }

    public void updateBillQuantity(Bill bill, int quantity) throws InsufficientBillQuantityException {
        if(cashReserves.get(bill) - quantity < 0)
            throw new InsufficientBillQuantityException("The Atm does not have sufficient quantity of "
                    + bill);
        cashReserves.replace(bill, cashReserves.get(bill) - quantity);
    }

    public boolean hasCurrency(Bill bill){
        return cashReserves.get(bill) > 0;
    }

    public Map<Bill, Integer> getCashReserves() {
        return cashReserves;
    }

}
