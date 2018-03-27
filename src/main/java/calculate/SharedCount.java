package calculate;

/**
 *
 * @author Peter Boots
 * Modified for FUN3 by Gertjan Schouten
 */
class SharedCount {
    private long count = 0;
    public synchronized void increase() {
        count++;
    }
    public synchronized long getCount() {
        return count;
    }
}
