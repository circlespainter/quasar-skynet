import co.paralleluniverse.fibers.*;
import co.paralleluniverse.strands.channels.Channel;
import static co.paralleluniverse.strands.channels.Channels.*;

public class Skynet {
    static void skynet(Channel<Long> c, long num, int size, int div) throws SuspendExecution, InterruptedException {
        if (size == 1) {
            c.send(num);
            return;
        }

        Channel<Long> rc = newChannel(BUFFER);
        long sum = 0L;
        for (int i = 0; i < div; i++) {
            long subNum = num + i * (size / div);
            new Fiber(() -> skynet(rc, subNum, size / div, div)).start();
        }
        for (int i = 0; i < div; i++)
            sum += rc.receive();
        c.send(sum);
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0 ; i < RUNS ; i++) {
            Channel<Long> c = newChannel(BUFFER);
            System.out.print((i+1) + ": ");
            long start = System.nanoTime();
            new Fiber(() -> skynet(c, 0, TOTAL, BRANCH)).start();
            long result = c.receive();
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.println(result + " (" + elapsed + " ms)");
        }
    }

    static final int RUNS = 4;
    static final int BRANCH = 10;
    static final int BUFFER = -1; // >= 0 (fully sync), <= BRANCH (fully async) ; < 0 means unlimited
    static final int TOTAL = 1_000_000; // >= BRANCH
}
