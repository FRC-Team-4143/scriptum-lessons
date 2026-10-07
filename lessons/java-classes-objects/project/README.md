# Classes & Objects

:::tip[Read this first: Classes and Objects]

The team docs page for this lesson is the best place to learn the ideas
before you write any code: [Classes and
Objects](https://frc-team-4143.github.io/docs/software/java/classes-objs) -
Classes, Objects, the Example, and Access Modifiers (why `protected`) map
directly onto this lesson. Come back here when you're ready to apply it. If
a step below feels unfamiliar, that page is where to look first.

:::

This lesson builds a small `BankAccount` class instead of the docs page's
`MotorController` example - same ideas (fields, a constructor,
encapsulation), a different object. Fill in `src/BankAccount.java`;
`src/Main.java` already creates a couple of accounts and moves money
between them, so click **Run** any time to see it in action.

## What you need to do

`src/BankAccount.java` is an empty class, and you turn it into something
that protects its own money. `src/Main.java` is already written - it makes
two accounts and moves money around, so it will print real numbers once
your class works. Team standard: class member fields use
`trailing_snake_case_`.

1. **Give each account its data.** Add the fields `owner_` (`String`) and
   `balance_` (`double`). Make them `protected`, not `private` - the next
   lesson builds subclasses that need to reach them (see the Bonus
   **Inheritance** lesson), but outside code still can't.
2. **Write the constructor** `BankAccount(String owner, double initial_balance)`
   so every new account starts in a valid state: it sets `owner_` and
   `balance_`, and a negative `initial_balance` is clamped to `0.0`.
3. **Write the rules for changing the balance.** Both should reject nonsense
   amounts, and `withdraw` reports success or failure through its return
   value so callers can react.
   - `void deposit(double amount)` - adds `amount` to `balance_` if it's
     positive; otherwise does nothing.
   - `boolean withdraw(double amount)` - if `amount` is positive and no
     more than `balance_`, subtracts it and returns `true`; otherwise leaves
     `balance_` unchanged and returns `false`.
4. **Add `double getBalance()`** - returns `balance_`, so other code can
   read the balance without touching the field.
5. **Write `boolean transfer(BankAccount recipient, double amount)` by
   reusing `withdraw` and `deposit`.** Withdraw from this account by calling
   `withdraw` and, if that succeeds, deposit into `recipient` by calling its
   `deposit` - don't duplicate the rules, and don't reach into the other
   account's fields. Return whether the transfer went through; a failed
   withdrawal leaves both accounts untouched.
6. **Run it**: `Main` should print `Checking balance: 75.0` and
   `Savings balance: 90.0`. Then verify.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
