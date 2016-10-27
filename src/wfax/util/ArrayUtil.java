package wfax.util;

public class ArrayUtil {

	public static void normalize(double[] arr) {
		double max = max(arr);

		max = Math.abs(max);

		for (int i = 0; i < arr.length; i++)
			arr[i] /= max;
	}

	public static void normalizeABS(double[] arr) {
		double max = max(arr);

		max = Math.abs(max);

		for (int i = 0; i < arr.length; i++) {
			arr[i] /= max;
			arr[i] = Math.abs(arr[i]);
		}
	}
	
	public static double[][] normalizePeaksABS(double[] arr, int num) {
		double[][] max = maxpeaksABS(arr, num);

		for (int i = 0; i < arr.length; i++)
			arr[i] /= max[0][0];
		
		return max;
	}
	
	public static int peakABS_tentFilter(double[] arr, int lower, int upper, double strength, boolean normalize) {
		double max = 0;
		int mxi = 0;
		
		for (int i = 0; i < arr.length; i++) {
			// Filter
			if (strength != 0) {
				
				double diff = 0;
				if (i < lower) {
					diff = i - lower;
					arr[i] = arr[i] / strength*diff;
				}
				else if (i > upper) {
					diff = upper - i;
					arr[i] = arr[i] / strength*diff;
				}
			}
			
			if (Double.isNaN(arr[i]))
				arr[i] = 0.0;
			else
				arr[i] = Math.abs(arr[i]);
			
			// Max
			if (arr[i] > max) {
				max = arr[i];
				mxi = i;
			}
		}
		
		if (normalize) for (int i = 0; i < arr.length; i++)
				arr[i] = arr[i] / max;
		
		return mxi;
	}

	public static double max(double[] arr) {
		double max = 0;

		for (int i = 0; i < arr.length; i++) {
			if (Math.abs(arr[i]) > Math.abs(max))
				max = arr[i];
		}

		return max;
	}

	/**
	 * Find maximum peaks and apply ABS to array.
	 * @param arr array to operate on
	 * @param num number of peaks to find 
	 * @return biarray containing arr of peaks from greatest to least and arr of their indices 
	 */
	public static double[][] maxpeaksABS(double[] arr, int num) {
		// peaks[x][0] is peaks, peaks[x][1] is indices
		double[][] peaks = new double[num][2];
		peaks[0][0] = 0.0;

		int si = 0;
		int ei;

		double[] h = new double[2];
		boolean found = false;

		for (int i = 0; i < arr.length; i++) {
			arr[i] = Math.abs(arr[i]);

			// Seek for maximum
			for (int j = 0; !found && j < peaks.length; j++)
				if (arr[i] > peaks[j][0]) {
					// Hold the existing pair
					h[0] = peaks[j][0];
					h[1] = peaks[j][1];
					
					// Assign peak and index
					peaks[j][0] = arr[i];
					peaks[j][1] = i;
					
					// Assign index holders
					si = j;
					found = true;
				}
			
			// If found, move over other seats
			if (found)
				for (ei = peaks.length - 1; ei > si; ei--)
					if (ei - 1 != si) {
						peaks[ei][0] = peaks[ei-1][0];
						peaks[ei][1] = peaks[ei-1][1];
					}
					else {
						peaks[ei][0] = h[0];
						peaks[ei][1] = h[1];
					}
			
			found = false;
		}
		
		if (peaks[0][0] == 0.0) {
			peaks[0][0] = 1;
		}
		
		return peaks;
	}

}
