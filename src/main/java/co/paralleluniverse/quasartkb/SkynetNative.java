package co.paralleluniverse.quasartkb;

import co.paralleluniverse.fibers.*;
import co.paralleluniverse.strands.channels.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static co.paralleluniverse.strands.channels.Channels.*;
import static co.paralleluniverse.quasartkb.Util.*;

public final class SkynetNative {
    private static void skynet(LongChannel c, int num, final int size, final int div) throws SuspendExecution, InterruptedException {
        if (size == 1) {
            c.send(num);
        } else {
            final LongChannel rc = newLongChannel(PER_CHANNEL_BUFFER);
            long sum = 0L;
            for (int i = 0; i < div; i++) {
                final int subNum = num + i * (size / div);
                new Fiber<Void>(() -> skynet(rc, subNum, size / div, div)).start();
            }
            for (int i = 0; i < div; i++)
                try {
                    sum += rc.receiveLong();
                } catch (final ReceivePort.EOFException ignored) {}
            c.send(sum);
        }
    }

    public static void main(String[] args) throws Exception {
        final LongChannel c = newLongChannel(PER_CHANNEL_BUFFER);

        long start; long result; long elapsed;
        for (int i = 0 ; i < RUNS ; i++) {
            lStart((i+1) + ": ");
            start = System.nanoTime();
            new Fiber(() -> skynet(c, ROOT_FIBER_NUM, TOTAL_COUNT_OF_LEAF_FIBERS, BRANCH_SPAWN)).start();
            result = c.receiveLong();
            elapsed = (System.nanoTime() - start) / 1_000_000;
            lEnd(result + " (" + elapsed + " ms)");
        }
    }

    private SkynetNative() {}
}

final class Util {
    // START customizable

    // Regular skynet conf
    static final int RUNS = 10; // 100_000 // 1
    static final int BRANCH_SPAWN = 10;
    static final int PER_CHANNEL_BUFFER = 10; // >= 0 (fully sync), <= BRANCH_SPAWN (fully async) ; < 0 means +inf
    static final int TOTAL_COUNT_OF_LEAF_FIBERS = 1_000_000; // 1_000_000 // >= BRANCH_SPAWN

    // END customizable

    static final int ROOT_FIBER_NUM = 0;

    static void lStart(String s) {
        final Date now = new Date();
        System.err.print("[" + fmt(now) + " " + Thread.currentThread().getName() + "(" + Thread.currentThread().getClass().getName() + ")" + "] " + s);
    }

    static void lEnd(String s) {
        System.err.print(s);
        System.err.println();
    }

    private static String fmt(Date d) {
        return d != null ? df.format(d) : null;
    }

    private static final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static final SimpleDateFormat df = new SimpleDateFormat(/* yyyy-MM-dd */ "HH:mm:ss.SSS");

    static {
        df.setCalendar(c);
    }

    private Util() {}
}
