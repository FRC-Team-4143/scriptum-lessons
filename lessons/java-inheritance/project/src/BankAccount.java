// This class is complete - it's the same BankAccount you built in the
// Classes & Objects lesson. This lesson is about extending it, not
// rewriting it, so it's given to you working.
public class BankAccount {
    protected String owner_;
    protected double balance_;

    public BankAccount(String owner, double initial_balance) {
        owner_ = owner;
        balance_ = (initial_balance < 0) ? 0.0 : initial_balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance_ += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance_) {
            balance_ -= amount;
            return true;
        }
        return false;
    }

    public double getBalance() {
        return balance_;
    }

    public boolean transfer(BankAccount recipient, double amount) {
        if (!withdraw(amount)) {
            return false;
        }
        recipient.deposit(amount);
        return true;
    }
}
