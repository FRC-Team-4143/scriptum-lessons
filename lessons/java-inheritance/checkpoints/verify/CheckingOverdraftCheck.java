public class CheckingOverdraftCheck {
    public static void main(String[] args) {
        CheckingAccount account = new CheckingAccount("Test", 100.0, 50.0);

        boolean withinOverdraft = account.withdraw(120.0);
        if (!withinOverdraft) {
            System.out.println(
                "withdraw(120.0) from a 100.0 balance with a 50.0 overdraft limit returned false, expected true."
            );
            System.exit(1);
        }
        if (Math.abs(account.getBalance() - (-20.0)) > 0.0001) {
            System.out.println(
                "After withdraw(120.0) from 100.0 (50.0 overdraft), getBalance() was "
                    + account.getBalance() + ", expected -20.0."
            );
            System.exit(1);
        }

        boolean beyondOverdraft = account.withdraw(1000.0);
        if (beyondOverdraft) {
            System.out.println(
                "withdraw(1000.0) beyond the overdraft limit returned true - it should fail (return false)."
            );
            System.exit(1);
        }
        if (Math.abs(account.getBalance() - (-20.0)) > 0.0001) {
            System.out.println(
                "A failed overdraft withdrawal still changed the balance to " + account.getBalance()
                    + " - it should be untouched."
            );
            System.exit(1);
        }

        System.out.println("CheckingAccount overdraft looks good.");
    }
}
