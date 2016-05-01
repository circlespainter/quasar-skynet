package co.paralleluniverse.quasartkb;

import co.paralleluniverse.fibers.*;
import co.paralleluniverse.strands.channels.*;

import static co.paralleluniverse.strands.channels.Channels.*;

import static co.paralleluniverse.quasartkb.Util.*;

public final class SkynetNative {
    private static void skynet(LongChannel c, int num, final int size, final int div) throws SuspendExecution, InterruptedException {
        try {
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
                    sum += rc.receiveLong();
                c.send(sum);
            }
        } catch (final Throwable e) {
            throw new AssertionError(e);
        }
    }

    public static void main(String[] args) throws Exception {
        final LongChannel c = newLongChannel(PER_CHANNEL_BUFFER);
        final boolean gc = args != null && args.length > 0 && "gc".equals(args[0].toLowerCase());

        // If we need to synchronize a lot, better do it in a fiber.
        new Fiber(() -> {
            long start; long result; long elapsed;
            for (int i = 0 ; i < RUNS ; i++) {
                if (gc) {
                    l("GC");
                    System.gc();
                }
                lStart((i+1) + ": ");
                start = System.nanoTime();
                new Fiber(() -> skynet(c, ROOT_FIBER_NUM, TOTAL_COUNT_OF_LEAF_FIBERS, BRANCH_SPAWN)).start();
                try {
                    result = c.receiveLong();
                } catch (final ReceivePort.EOFException e) {
                    throw new AssertionError(e);
                }
                elapsed = (System.nanoTime() - start) / 1_000_000;
                lEnd(result + " (" + elapsed + " ms)");
            }
        }).start().join();
    }

    private SkynetNative() {}
}
