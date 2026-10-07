public class SavingsAccount extends BankAccount {
    protected double interest_rate_;

    public SavingsAccount(String owner, double initial_balance, double interest_rate) {
        super(owner, initial_balance);
        interest_rate_ = interest_rate;
    }

    public void applyInterest() {
        deposit(getBalance() * interest_rate_);
    }
}
