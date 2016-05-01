package co.paralleluniverse.quasartkb;

import co.paralleluniverse.fibers.*;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.*;

import static co.paralleluniverse.strands.channels.Channels.*;

import static co.paralleluniverse.quasartkb.Util.*;

public class SkynetNative {
    private static void skynet(LongChannel c, int num, final int size, final int div) throws SuspendExecution, InterruptedException {
        try {
            if (size == 1) {
                d("Leaf fiber " + num + ", sending num");
                c.send(num);
                d("Leaf fiber " + num + ", sent num");
            } else {
                final LongChannel rc = newLongChannel(PER_CHANNEL_BUFFER);
                long sum = 0L;
                for (int i = 0; i < div; i++) {
                    final int subNum = num + i * (size / div);
                    if (DEBUG) {
                        l("Branch fiber " + num + ", spawning sub " + subNum + " of " + div);
                        new Fiber<Void>(Integer.toString(subNum), (SuspendableRunnable) () -> skynet(rc, subNum, size / div, div)).start();
                    } else {
                        new Fiber<Void>(/* null, 8, */(SuspendableRunnable) () -> skynet(rc, subNum, size / div, div)).start();
                    }
                }
                for (int i = 0; i < div; i++) {
                    d("Branch fiber " + num + ", receiving #" + i + " of " + div);
                    sum += rc.receiveLong();
                    d("Branch fiber " + num + ", received #" + i + " of " + div);
                }

                d("Branch fiber " + num + ", sending sum " + sum);
                c.send(sum);
                d("Branch fiber " + num + ", sent sum " + sum);
            }
        } catch (final Throwable e) {
            throw new AssertionError(e);
        }
    }

    public static void main(String[] args) throws Exception {
        final LongChannel c = newLongChannel(PER_CHANNEL_BUFFER);
        final boolean gc = args != null && args.length > 0 && "gc".equals(args[0].toLowerCase());

        long start; long result; long elapsed;
        for (int i = 0 ; i < RUNS ; i++) {
            if (gc) {
                l("GC");
                System.gc();
            }
            lStart((i+1) + ": ");
            start = System.nanoTime();
            if (DEBUG) {
                l("Spawning root fiber");
                new Fiber(Integer.toString(ROOT_FIBER_NUM), (SuspendableRunnable) () -> skynet(c, ROOT_FIBER_NUM, TOTAL_COUNT_OF_LEAF_FIBERS, BRANCH_SPAWN)).start();
            } else {
                new Fiber(/* null, 8, */ (SuspendableRunnable) () -> skynet(c, ROOT_FIBER_NUM, TOTAL_COUNT_OF_LEAF_FIBERS, BRANCH_SPAWN)).start();
            }
            d("Receiving from root fiber");
            result = c.receiveLong();
            d("Received " + result + " from root fiber");
            elapsed = (System.nanoTime() - start) / 1_000_000;
            lEnd(result + " (" + elapsed + " ms)");
        }

        printSchedulingStats();
    }
}
