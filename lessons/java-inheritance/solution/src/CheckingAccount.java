public class CheckingAccount extends BankAccount {
    protected double overdraft_limit_;

    public CheckingAccount(String owner, double initial_balance, double overdraft_limit) {
        super(owner, initial_balance);
        overdraft_limit_ = overdraft_limit;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && (balance_ - amount) >= -overdraft_limit_) {
            balance_ -= amount;
            return true;
        }
        return false;
    }
}
