public class DepositCheck {
    public static void main(String[] args) {
        BankAccount negativeStart = new BankAccount("Test", -50.0);
        if (Math.abs(negativeStart.getBalance() - 0.0) > 0.0001) {
            System.out.println(
                "new BankAccount(\"Test\", -50.0).getBalance() was " + negativeStart.getBalance()
                    + ", expected 0.0 - a negative initial balance should be clamped to 0."
            );
            System.exit(1);
        }

        BankAccount account = new BankAccount("Test", 100.0);
        account.deposit(25.0);
        if (Math.abs(account.getBalance() - 125.0) > 0.0001) {
            System.out.println(
                "After depositing 25.0 into a 100.0 balance, getBalance() was "
                    + account.getBalance() + ", expected 125.0."
            );
            System.exit(1);
        }

        account.deposit(-10.0);
        if (Math.abs(account.getBalance() - 125.0) > 0.0001) {
            System.out.println(
                "deposit(-10.0) changed the balance to " + account.getBalance()
                    + " - negative deposits should do nothing."
            );
            System.exit(1);
        }

        account.deposit(0.0);
        if (Math.abs(account.getBalance() - 125.0) > 0.0001) {
            System.out.println(
                "deposit(0.0) changed the balance to " + account.getBalance()
                    + " - a zero deposit should do nothing."
            );
            System.exit(1);
        }

        System.out.println("deposit looks good.");
    }
}
