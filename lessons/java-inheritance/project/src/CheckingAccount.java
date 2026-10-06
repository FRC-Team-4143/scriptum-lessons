// TODO: CheckingAccount extends BankAccount - add "extends BankAccount" to
// the class declaration below.
public class CheckingAccount {
    // TODO: add one more field (team standard: trailing_snake_case_):
    //   overdraft_limit_ (double)

    // TODO: constructor CheckingAccount(String owner, double initial_balance, double overdraft_limit)
    //   - call the BankAccount constructor with owner and initial_balance:
    //     super(owner, initial_balance)
    //   - set overdraft_limit_

    // TODO: override withdraw(double amount) -> boolean
    //   BankAccount's withdraw refuses anything over the current balance.
    //   Override it here so this account can go negative, but only down
    //   to -overdraft_limit_:
    //   - if amount is positive and (balance_ - amount) >= -overdraft_limit_,
    //     subtract amount from balance_ and return true
    //   - otherwise, leave balance_ unchanged and return false
    //   balance_ is protected, not private, so you can reach it directly
    //   here - that's exactly what protected is for.
}
