# Inheritance

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

This lesson extends the `BankAccount` class you built in Classes & Objects
- it's given to you complete in `src/BankAccount.java` this time, because
this lesson is about building *on top of* a class, not rewriting it.

## What you need to do

`BankAccount` is finished - don't edit it. You'll build two specialized
accounts on top of it, each in its own file, by filling in the skeleton
classes `SavingsAccount` and `CheckingAccount`.

1. **Make both classes subclasses of `BankAccount`** by adding
   `extends BankAccount` to their declarations.
2. **Give each one its new field and constructor.** The first line of each
   constructor must call `super(owner, initial_balance)` to set up the
   inherited fields before you touch your own. Team standard: fields use
   `trailing_snake_case_`.
   - `SavingsAccount(String owner, double initial_balance, double interest_rate)`
     - stores `interest_rate_` (e.g. `0.05` means 5%).
   - `CheckingAccount(String owner, double initial_balance, double overdraft_limit)`
     - stores `overdraft_limit_`.
3. **`SavingsAccount`: add new behavior.** `applyInterest()` deposits
   `getBalance() * interest_rate_` into the account. Reuse the inherited
   `deposit` and `getBalance` instead of changing `balance_` yourself.
4. **`CheckingAccount`: override existing behavior.** `withdraw(double amount)`
   replaces `BankAccount`'s version: it's allowed even if it takes the
   balance negative, as long as it doesn't go below `-overdraft_limit_`.
   Return `true` if it went through, or leave the balance alone and return
   `false`. Everything else (`deposit`, `getBalance`, `transfer`) should keep
   working with no code from you.
5. **Run it**; `Main` exercises both subclasses and prints the resulting
   balances. Verify when they add up.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
