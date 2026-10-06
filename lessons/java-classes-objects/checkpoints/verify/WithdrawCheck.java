public class WithdrawCheck {
    public static void main(String[] args) {
        BankAccount account = new BankAccount("Test", 100.0);

        boolean result = account.withdraw(40.0);
        if (!result) {
            System.out.println("withdraw(40.0) from a 100.0 balance returned false, expected true.");
            System.exit(1);
        }
        if (Math.abs(account.getBalance() - 60.0) > 0.0001) {
            System.out.println(
                "After withdraw(40.0) from 100.0, getBalance() was " + account.getBalance() + ", expected 60.0."
            );
            System.exit(1);
        }

        boolean overdraft = account.withdraw(1000.0);
        if (overdraft) {
            System.out.println(
                "withdraw(1000.0) from a 60.0 balance returned true - it should fail (return false) instead."
            );
            System.exit(1);
        }
        if (Math.abs(account.getBalance() - 60.0) > 0.0001) {
            System.out.println(
                "A failed withdraw(1000.0) still changed the balance to " + account.getBalance()
                    + " - it should be untouched."
            );
            System.exit(1);
        }

        boolean negative = account.withdraw(-5.0);
        if (negative) {
            System.out.println("withdraw(-5.0) returned true - a negative withdrawal should fail (return false).");
            System.exit(1);
        }

        System.out.println("withdraw looks good.");
    }
}
