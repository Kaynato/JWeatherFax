package wfax.util;

public class Utils {

	public static void msleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {}
	}

	/**
	 * Is the change enough?
	 * @param factor curr/prev must be more than 1+factor or less than 1-factor
	 * @param threshold diff of curr and prev must be greater than this
	 * @return
	 */
	public static boolean trigger(double prev, double curr, double factor, double threshold) {
		double diff = Math.abs(curr - prev);
		double ratio = prev != 0 ? curr/prev : 1000;
		return (diff > threshold) && (ratio>1?ratio>1+factor:ratio<1-factor);
	}
	
}
