public class IndependentObjectsCheck {
    public static void main(String[] args) {
        BankAccount alice = new BankAccount("Alice", 100.0);
        BankAccount bob = new BankAccount("Bob", 100.0);

        alice.deposit(50.0);
        alice.withdraw(20.0);
        // bob was never touched - it must still be at its starting balance.
        if (Math.abs(bob.getBalance() - 100.0) > 0.0001) {
            System.out.println(
                "A second BankAccount's balance changed to " + bob.getBalance()
                    + " just from creating and using the first one - each object needs its own fields."
            );
            System.exit(1);
        }
        if (Math.abs(alice.getBalance() - 130.0) > 0.0001) {
            System.out.println("alice.getBalance() was " + alice.getBalance() + ", expected 130.0.");
            System.exit(1);
        }
        System.out.println("Independent objects look good.");
    }
}
