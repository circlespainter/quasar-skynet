package co.paralleluniverse.quasartkb;

import co.paralleluniverse.fibers.*;
import static co.paralleluniverse.strands.channels.Channels.*;
import co.paralleluniverse.strands.channels.*;

public class Skynet {
    // START customizable
    private static final int RUNS = 10;
    private static final int BRANCH_SPAWN = 10;
    private static final int PER_CHANNEL_BUFFER = 1; // >= 0 (fully sync), <= BRANCH_SPAWN (fully async)
    private static final int TOTAL_COUNT_OF_LEAF_FIBERS = 1_000_000; // >= BRANCH_SPAWN;
    private static final boolean DEBUG = false;
    // END customizable

    private static final int ROOT_FIBER_NUM = 0;

    private static void skynet(LongChannel c, int num, int size, int div) throws SuspendExecution, InterruptedException {
        try {
            if (size == 1) {
                if (DEBUG)
                    System.err.println("Leaf fiber " + num + ", just sending num");
                c.send(num);
            } else {
                final LongChannel rc = newLongChannel(PER_CHANNEL_BUFFER);
                long sum = 0L;
                for (int i = 0; i < div; i++) {
                    final int subNum = num + i * (size / div);
                    if (DEBUG)
                        System.err.println("Branch fiber " +  num + ", spawning sub " + subNum + " of " + div);
                    new Fiber<Void>(/* null, 8, */ () -> skynet(rc, subNum, size / div, div)).start();
                }
                for (int i = 0; i < div; i++) {
                    if (DEBUG)
                        System.err.println("Branch fiber " + num + ", receive #" + i + " of " + div);
                    sum += rc.receiveLong();
                }

                if (DEBUG)
                    System.err.println("Branch fiber " + num + ", sending sum " + sum);
                c.send(sum);
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
                System.err.println("GC");
                System.gc();
            }
            System.err.print((i+1) + ": ");
            start = System.nanoTime();
            if (DEBUG)
                System.err.println("Spawning root fiber");
            new Fiber(/* null, 8, */ () -> skynet(c, ROOT_FIBER_NUM, TOTAL_COUNT_OF_LEAF_FIBERS, BRANCH_SPAWN)).start();
            if (DEBUG)
                System.err.println("Receiving from root fiber");
            result = c.receiveLong();
            if (DEBUG)
                System.err.println("Received " + result + " from root fiber");
            elapsed = (System.nanoTime() - start) / 1_000_000;
            System.err.println(result + " (" + elapsed + " ms)");
        }

        printSchedulingStats();
    }

    private static void printSchedulingStats() {
//        System.err.println("parkLoops: " + ParkableForkJoinTask.parkLoops.longValue());
//        System.err.println("parkLeased: " + ParkableForkJoinTask.parkLeased.longValue());
//        System.err.println("parkRunnable: " + ParkableForkJoinTask.parkRunnable.longValue());
//        System.err.println("unparkLoops: " + ParkableForkJoinTask.unparkLoops.longValue());
//        System.err.println("unparkParked: " + ParkableForkJoinTask.unparkParked.longValue());
//        System.err.println("unparkRunnable: " + ParkableForkJoinTask.unparkRunnable.longValue());
//        System.err.println("unparkParking: " + ParkableForkJoinTask.unparkParking.longValue());
//        System.err.println("unparkLeasedDoNothing: " + ParkableForkJoinTask.unparkLeasedDoNothing.longValue());
//        System.err.println("unparkParkedExclusiveByOtherDoNothing: " + ParkableForkJoinTask.unparkParkedExclusiveByOtherDoNothing.longValue());
    }
}
