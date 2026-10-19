public class Main {
    public static void main(String[] args) {
        // The drive coach tweaks the max speed between matches, then undoes.
        SettingHistory max_speed = new SettingHistory(4.0);
        max_speed.set(4.5);
        max_speed.set(5.0);
        max_speed.set(3.5);
        System.out.println("Max speed after three changes: " + max_speed.current());
        max_speed.undo();
        System.out.println("After one undo: " + max_speed.current());
        max_speed.undo();
        System.out.println("After two undos: " + max_speed.current()
            + " (" + max_speed.undoCount() + " undo left)");

        // Two autos typed by hand. One has a typo.
        String good = "seq(drive[2.0], par{intake, shoot})";
        String typo = "seq(drive[2.0), shoot)";
        System.out.println(good + " balanced? " + AutoParser.isBalanced(good));
        System.out.println(typo + " balanced? " + AutoParser.isBalanced(typo));

        // An auto routine, run step by step.
        AutoQueue auto = new AutoQueue();
        auto.addStep("drive to note");
        auto.addStep("intake");
        auto.addStep("drive to speaker");
        auto.addStep("shoot");
        System.out.println("Auto has " + auto.stepsLeft() + " steps");
        for (int i = 0; i < 5; i++) {
            System.out.println("  step: " + auto.nextStep());
        }

        // The indexer holds two pieces.
        IndexerQueue indexer = new IndexerQueue(2);
        System.out.println("Load note A: " + indexer.load("note A"));
        System.out.println("Load note B: " + indexer.load("note B"));
        System.out.println("Load note C: " + indexer.load("note C") + " (full)");
        System.out.println("Fire: " + indexer.fire());
        System.out.println("Load note C: " + indexer.load("note C"));
        System.out.println("Fire: " + indexer.fire());
        System.out.println("Fire: " + indexer.fire());
        System.out.println("Fire: " + indexer.fire());
    }
}
