package co.paralleluniverse.quasartkb;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.channels.Channel;

import static co.paralleluniverse.quasartkb.Util.*;
import static co.paralleluniverse.strands.channels.Channels.newChannel;

public final class SkynetObject {
    private static void skynet(Channel<Long> c, int num, final int size, final int div) throws SuspendExecution, InterruptedException {
        if (size == 1) {
            c.send((long) num);
        } else {
            final Channel<Long> rc = newChannel(PER_CHANNEL_BUFFER);
            long sum = 0L;
            for (int i = 0; i < div; i++) {
                final int subNum = num + i * (size / div);
                new Fiber<Void>(() -> skynet(rc, subNum, size / div, div)).start();
            }
            for (int i = 0; i < div; i++)
                sum += rc.receive();
            c.send(sum);
        }
    }

    public static void main(String[] args) throws Exception {
        final Channel<Long> c = newChannel(PER_CHANNEL_BUFFER);

        long start; long result; long elapsed;
        for (int i = 0 ; i < RUNS ; i++) {
            lStart((i+1) + ": ");
            start = System.nanoTime();
            new Fiber(() -> skynet(c, ROOT_FIBER_NUM, TOTAL_COUNT_OF_LEAF_FIBERS, BRANCH_SPAWN)).start();
            result = c.receive();
            elapsed = (System.nanoTime() - start) / 1_000_000;
            lEnd(result + " (" + elapsed + " ms)");
        }
    }

    private SkynetObject() {}
}
