public class Main {
    public static void main(String[] args) {
        SavingsAccount savings = new SavingsAccount("Alex", 1000.0, 0.05);
        savings.applyInterest();
        System.out.println("Savings balance after interest: " + savings.getBalance());

        CheckingAccount checking = new CheckingAccount("Alex", 100.0, 50.0);
        checking.withdraw(120.0);
        System.out.println("Checking balance after overdraft withdrawal: " + checking.getBalance());

        // Inherited straight from BankAccount, unchanged:
        checking.deposit(30.0);
        System.out.println("Checking balance after deposit: " + checking.getBalance());
    }
}
