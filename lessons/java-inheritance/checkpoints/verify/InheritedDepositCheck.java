public class InheritedDepositCheck {
    public static void main(String[] args) {
        // deposit(), getBalance(), and transfer() aren't redefined by
        // either subclass - they should work exactly as inherited from
        // BankAccount, with no extra code required.
        SavingsAccount savings = new SavingsAccount("Test", 100.0, 0.05);
        savings.deposit(50.0);
        if (Math.abs(savings.getBalance() - 150.0) > 0.0001) {
            System.out.println(
                "SavingsAccount.deposit(50.0) on a 100.0 balance left getBalance() at "
                    + savings.getBalance() + ", expected 150.0 - deposit should be inherited unchanged."
            );
            System.exit(1);
        }

        CheckingAccount checking = new CheckingAccount("Test", 100.0, 50.0);
        BankAccount other = new BankAccount("Other", 0.0);
        boolean transferred = checking.transfer(other, 30.0);
        if (!transferred || Math.abs(checking.getBalance() - 70.0) > 0.0001
                || Math.abs(other.getBalance() - 30.0) > 0.0001) {
            System.out.println(
                "CheckingAccount.transfer(...) didn't behave like the inherited BankAccount version."
            );
            System.exit(1);
        }

        System.out.println("Inherited deposit/transfer look good.");
    }
}
