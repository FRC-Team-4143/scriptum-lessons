# Inheritance

:::tip[Read this first: Inheritance]

The team docs cover this in the Inheritance section of [Classes and
Objects](https://frc-team-4143.github.io/docs/software/java/classes-objs#inheritance).
Read it first - it explains `extends`, `super`, and overriding with the same
ideas you'll use here. The [Programming
Standards](https://frc-team-4143.github.io/docs/software/java/standards)
page has the naming rules (like `trailing_snake_case_` fields) this lesson
follows.

:::

This lesson extends the `BankAccount` class you built in Classes & Objects
- it's given to you complete in `src/BankAccount.java` this time, because
this lesson is about building *on top of* a class, not rewriting it.

## What inheritance is

`class SavingsAccount extends BankAccount` means every `SavingsAccount`
*is a* `BankAccount` - it automatically has `owner_`, `balance_`,
`deposit`, `withdraw`, `getBalance`, and `transfer`, without you retyping
any of it. A subclass can do two things with what it inherits:

- **Add** something new (a field, a method) that the parent didn't have -
  `SavingsAccount.applyInterest()` below.
- **Override** a method the parent already has, replacing its behavior for
  this subclass specifically - `CheckingAccount.withdraw()` below.

A subclass's constructor has to call the parent's constructor first (with
`super(...)`) to set up the fields it inherited, before touching anything
of its own.

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
