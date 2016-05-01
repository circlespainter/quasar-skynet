package co.paralleluniverse.quasartkb;

final class Util {
    // START customizable

    // Regular skynet conf
    static final int RUNS = 10; // 100_000 // 1
    static final int BRANCH_SPAWN = 10;
    static final int PER_CHANNEL_BUFFER = 0; // >= 0 (fully sync), <= BRANCH_SPAWN (fully async)
    static final int TOTAL_COUNT_OF_LEAF_FIBERS = 1_000_000; // 1_000_000 // >= BRANCH_SPAWN
    static final boolean DEBUG = false;

    // Channel stress conf. with minimal logging
//    static final int RUNS = 100_000; // 100_000 // 1
//    static final int BRANCH_SPAWN = 10;
//    static final int PER_CHANNEL_BUFFER = -1; // >= 0 (fully sync), <= BRANCH_SPAWN (fully async)
//    static final int TOTAL_COUNT_OF_LEAF_FIBERS = 10; // 1_000_000 // >= BRANCH_SPAWN
//    static final boolean DEBUG = true;

    // END customizable

    static final int ROOT_FIBER_NUM = 0;

    static void printSchedulingStats() {
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

    private Util() {}
}
