public class BankAccount {
    // TODO: add these fields (team standard: trailing_snake_case_ for
    // members). Use protected, not private - a later lesson builds a
    // subclass that needs to reach these directly, and protected is what
    // makes that possible while private would block it:
    //   owner_    (String)
    //   balance_  (double)

    // TODO: constructor BankAccount(String owner, double initial_balance)
    //   - owner_ = owner
    //   - balance_ = initial_balance, but never let an account start
    //     negative - clamp it to 0.0 if initial_balance is negative

    // TODO: deposit(double amount)
    //   - if amount is positive, add it to balance_
    //   - otherwise, do nothing

    // TODO: withdraw(double amount) -> boolean
    //   - if amount is positive and no more than balance_, subtract it
    //     from balance_ and return true
    //   - otherwise, leave balance_ unchanged and return false

    // TODO: getBalance() -> returns balance_

    // TODO: transfer(BankAccount recipient, double amount) -> boolean
    //   - withdraw amount from this account by calling the withdraw
    //     method above - if that fails (insufficient funds), return
    //     false and leave both accounts untouched
    //   - otherwise, deposit amount into recipient by calling its
    //     deposit method (don't touch recipient's fields directly),
    //     then return true
}
