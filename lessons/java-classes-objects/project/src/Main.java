public class Main {
    public static void main(String[] args) {
        BankAccount checking = new BankAccount("Alex", 100.0);
        BankAccount savings = new BankAccount("Alex", 50.0);

        checking.deposit(25.0);
        checking.withdraw(10.0);
        checking.transfer(savings, 40.0);

        System.out.println("Checking balance: " + checking.getBalance());
        System.out.println("Savings balance: " + savings.getBalance());
    }
}
