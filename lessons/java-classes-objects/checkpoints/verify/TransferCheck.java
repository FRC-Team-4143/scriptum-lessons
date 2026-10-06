public class TransferCheck {
    public static void main(String[] args) {
        BankAccount sender = new BankAccount("Sender", 100.0);
        BankAccount recipient = new BankAccount("Recipient", 20.0);

        boolean result = sender.transfer(recipient, 30.0);
        if (!result) {
            System.out.println("transfer(recipient, 30.0) from a 100.0 balance returned false, expected true.");
            System.exit(1);
        }
        if (Math.abs(sender.getBalance() - 70.0) > 0.0001) {
            System.out.println(
                "After the transfer, the sender's balance was " + sender.getBalance() + ", expected 70.0."
            );
            System.exit(1);
        }
        if (Math.abs(recipient.getBalance() - 50.0) > 0.0001) {
            System.out.println(
                "After the transfer, the recipient's balance was " + recipient.getBalance() + ", expected 50.0."
            );
            System.exit(1);
        }

        boolean failed = sender.transfer(recipient, 1000.0);
        if (failed) {
            System.out.println(
                "transfer(recipient, 1000.0) with insufficient funds returned true - it should fail (return false)."
            );
            System.exit(1);
        }
        if (Math.abs(sender.getBalance() - 70.0) > 0.0001 || Math.abs(recipient.getBalance() - 50.0) > 0.0001) {
            System.out.println("A failed transfer still changed a balance - both accounts should be untouched.");
            System.exit(1);
        }

        System.out.println("transfer looks good.");
    }
}
