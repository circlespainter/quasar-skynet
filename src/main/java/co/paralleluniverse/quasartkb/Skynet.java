package co.paralleluniverse.quasartkb;

import co.paralleluniverse.fibers.*;
import static co.paralleluniverse.strands.channels.Channels.*;
import co.paralleluniverse.strands.channels.*;

public class Skynet {
    private static final int BUFFER = -1;

    private static void skynet(LongChannel c, int num, int size, int div) throws SuspendExecution, InterruptedException {
        try {
            if (size == 1)
                c.send(num);
            else {
                final LongChannel rc = newLongChannel(BUFFER);
                long sum = 0L;
                for (int i = 0; i < div; i++) {
                    final int subNum = num + i * (size / div);
                    new Fiber<Void>(/* null, 8, */ () -> skynet(rc, subNum, size / div, div)).start();
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
        final LongChannel c = newLongChannel(BUFFER);
        final boolean gc = args != null && args.length > 0 && "gc".equals(args[0].toLowerCase());

        long start; long result; long elapsed;
        for (int i = 0 ; i < 10 ; i++) {
            if (gc) {
                System.out.println("GC");
                System.gc();
            }
            System.out.print((i+1) + ": ");
            start = System.nanoTime();
            new Fiber(/* null, 8, */ () -> skynet(c, 0, 1000000, 10)).start();
            result = c.receiveLong();
            elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.println(result + " (" + elapsed + " ms)");
        }
    }
}
