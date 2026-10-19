import java.nio.file.Files;
import java.nio.file.Path;

public class IndexerCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expectFire(IndexerQueue q, String want, String what) {
        String got = q.fire();
        if (!want.equals(got)) {
            fail(what + ": fire() was \"" + got + "\", expected \"" + want + "\".");
        }
    }

    static void expectLoad(IndexerQueue q, String piece, boolean want, String what) {
        boolean got = q.load(piece);
        if (got != want) {
            fail(what + ": load(\"" + piece + "\") was " + got + ", expected " + want + ".");
        }
    }

    static void expectSize(IndexerQueue q, int want, String what) {
        int got = q.size();
        if (got != want) {
            fail(what + ": size() was " + got + ", expected " + want + ".");
        }
    }

    static void run() throws Exception {
        // The code without its comments, so a comment that says "no ArrayDeque" is fine.
        String source = Files.readString(Path.of("IndexerQueue.java"))
            .replaceAll("(?s)/\\*.*?\\*/", "")
            .replaceAll("//[^\\n]*", "");
        for (String banned : new String[] {"java.util", "ArrayDeque", "ArrayList", "LinkedList"}) {
            if (source.contains(banned)) {
                fail("IndexerQueue.java uses " + banned + ". Build this one by hand with the slots_ array.");
            }
        }

        IndexerQueue q = new IndexerQueue(3);
        expectSize(q, 0, "A new indexer");
        expectFire(q, "empty", "A new, empty indexer");
        if (q.isFull()) {
            fail("A new indexer with 3 slots says isFull() is true, expected false.");
        }
        expectLoad(q, "cube", true, "Loading into an empty indexer");
        expectLoad(q, "cone", true, "Loading a second piece");
        expectLoad(q, "ball", true, "Loading a third piece into 3 slots");
        if (!q.isFull()) {
            fail("3 pieces in 3 slots, but isFull() was false, expected true.");
        }
        expectLoad(q, "extra", false, "Loading into a full indexer");
        expectSize(q, 3, "A full indexer after one load was refused");
        expectFire(q, "cube", "After loading cube, cone, ball (the first piece loaded fires first)");
        expectLoad(q, "disk", true, "Loading after one slot was freed (wrap round to slot 0)");
        expectFire(q, "cone", "Firing again");
        expectFire(q, "ball", "Firing again");
        expectLoad(q, "ring", true, "Loading while the front has moved round");
        expectFire(q, "disk", "The piece loaded after wrapping round");
        expectFire(q, "ring", "The last piece");
        expectFire(q, "empty", "Firing with nothing left");
        expectSize(q, 0, "After firing everything");

        // Round and round a tiny indexer many times.
        IndexerQueue one = new IndexerQueue(1);
        for (int i = 0; i < 7; i++) {
            expectLoad(one, "p" + i, true, "A 1-slot indexer, load number " + (i + 1));
            expectLoad(one, "x", false, "A full 1-slot indexer");
            expectFire(one, "p" + i, "A 1-slot indexer, shot number " + (i + 1));
        }

        System.out.println("Your indexer fires pieces in the order they were loaded.");
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            fail("Your code crashed: " + e + ". Look for a pop, an index or a null that isn't checked first.");
        }
    }
}
