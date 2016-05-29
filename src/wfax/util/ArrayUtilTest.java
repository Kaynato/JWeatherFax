package wfax.util;

import java.util.Arrays;

public class ArrayUtilTest {

	public static void main(String[] args) {
		double[] arr = new double[]{5, 2, 2, 3, 6, 1, 0, 4};
		double[][] out = ArrayUtil.normalizePeaksABS(arr, 3);
		for (double[] outa : out)
			System.out.println(Arrays.toString(outa));
		System.out.println(Arrays.toString(arr));
	}

}
