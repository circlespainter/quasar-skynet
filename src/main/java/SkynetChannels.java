import co.paralleluniverse.fibers.*;
import co.paralleluniverse.strands.channels.Channel;
import static co.paralleluniverse.strands.channels.Channels.*;
import java.time.*;

public class SkynetChannels {
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
            Instant start = Instant.now();

            Channel<Long> c = newChannel(BUFFER);
            new Fiber(() -> skynet(c, 0, 1_000_000, 10)).start();
            long result = c.receive();

            Duration elapsed = Duration.between(start, Instant.now());
            System.out.println((i + 1) + ": " + result + " (" + elapsed.toMillis() + " ms)");
        }
    }

    static final int RUNS = 4;
    static final int BUFFER = 10; // = 0 unbufferd, > 0 buffered ; < 0 unlimited
}
