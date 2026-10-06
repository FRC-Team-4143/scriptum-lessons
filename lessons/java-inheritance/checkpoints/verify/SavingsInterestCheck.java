public class SavingsInterestCheck {
    public static void main(String[] args) {
        SavingsAccount account = new SavingsAccount("Test", 1000.0, 0.05);
        account.applyInterest();
        if (Math.abs(account.getBalance() - 1050.0) > 0.0001) {
            System.out.println(
                "After applyInterest() on a 1000.0 balance at 5% interest, getBalance() was "
                    + account.getBalance() + ", expected 1050.0."
            );
            System.exit(1);
        }

        SavingsAccount another = new SavingsAccount("Test", 200.0, 0.1);
        another.applyInterest();
        if (Math.abs(another.getBalance() - 220.0) > 0.0001) {
            System.out.println(
                "After applyInterest() on a 200.0 balance at 10% interest, getBalance() was "
                    + another.getBalance() + ", expected 220.0."
            );
            System.exit(1);
        }

        System.out.println("SavingsAccount.applyInterest looks good.");
    }
}
