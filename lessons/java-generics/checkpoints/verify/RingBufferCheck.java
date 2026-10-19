public class RingBufferCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static String contents(RingBuffer<String> buffer) {
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < buffer.size(); i++) {
            out.append(i == 0 ? "" : ", ").append(buffer.get(i));
        }
        return out.append("]").toString();
    }

    static void expect(int capacity, String[] added, String want) {
        RingBuffer<String> buffer = new RingBuffer<>(capacity);
        StringBuilder what = new StringBuilder();
        for (String item : added) {
            buffer.add(item);
            what.append(what.length() == 0 ? "" : ", ").append(item);
        }
        String got = contents(buffer);
        String call = "A RingBuffer<String> of capacity " + capacity
            + (added.length == 0 ? " with nothing added" : " after adding " + what);
        if (!got.equals(want)) {
            fail(call + " held " + got + " (size " + buffer.size() + "), expected " + want
                + ". Index 0 is the oldest item still kept.");
        }
        String latest = buffer.latest();
        String want_latest = added.length == 0 ? null : added[added.length - 1];
        if (want_latest == null ? latest != null : !want_latest.equals(latest)) {
            fail(call + ": latest() was " + latest + ", expected " + want_latest + ".");
        }
    }

    public static void main(String[] args) {
        if (RingBuffer.class.getTypeParameters().length != 1) {
            fail("RingBuffer should have one type parameter: public class RingBuffer<T>.");
        }

        expect(3, new String[0], "[]");
        expect(3, new String[] { "a" }, "[a]");
        expect(4, new String[] { "a", "b" }, "[a, b]");
        expect(3, new String[] { "a", "b", "c" }, "[a, b, c]");
        expect(3, new String[] { "a", "b", "c", "d" }, "[b, c, d]");
        expect(2, new String[] { "a", "b", "c", "d", "e" }, "[d, e]");
        expect(1, new String[] { "x", "y" }, "[y]");

        RingBuffer<Integer> ticks = new RingBuffer<>(2);
        ticks.add(100);
        ticks.add(250);
        ticks.add(-40);
        Integer newest = ticks.latest();
        Integer oldest = ticks.get(0);
        if (ticks.size() != 2 || oldest == null || oldest != 250 || newest == null || newest != -40) {
            fail("A RingBuffer<Integer> of capacity 2 after adding 100, 250, -40 should hold 250 then -40, but get(0) was "
                + oldest + " and latest() was " + newest + ".");
        }

        System.out.println("RingBuffer<T> keeps the newest items of any type.");
    }
}
