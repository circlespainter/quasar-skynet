package co.paralleluniverse.quasartkb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

final class Util {
    // START customizable

    // Regular skynet conf
    static final int RUNS = 10; // 100_000 // 1
    static final int BRANCH_SPAWN = 10;
    static final int PER_CHANNEL_BUFFER = 10; // >= 0 (fully sync), <= BRANCH_SPAWN (fully async), -1 = +inf
    static final int TOTAL_COUNT_OF_LEAF_FIBERS = 1_000_000; // 1_000_000 // >= BRANCH_SPAWN

    // END customizable

    static final int ROOT_FIBER_NUM = 0;

    static void l(String s) {
        lStart(s);
        lEnd("");
    }

    static void lStart(String s) {
        final Date now = new Date();
        System.err.print("[" + fmt(now) + " " + Thread.currentThread().getName() + "(" + Thread.currentThread().getClass().getName() + ")" + "] " + s);
    }

    static void lEnd(String s) {
        System.err.print(s);
        System.err.println();
    }

    static String fmt(Date d) {
        return d != null ? df.format(d) : null;
    }

    private static final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static final SimpleDateFormat df = new SimpleDateFormat(/* yyyy-MM-dd */ "HH:mm:ss.SSS");

    static {
        df.setCalendar(c);
    }

    private Util() {}
}
