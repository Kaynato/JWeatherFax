package wfax;

public class Globals {
	
	public static final int WINDOWSIZE = 4,
							CARRYSIZE = 1;
	
	public static String inputName = "D:\\Users\\Brian\\Music\\wfa\\WEATHERFAX.wav";
	public static String outputName = "D:\\Users\\Brian\\Music\\wfa\\PEAKS.txt";
	
	public static final int W_WIDTH = 804,
							W_HEIGHT = 804,
							I_WIDTH = 1810,
							I_HEIGHT = 1200;
	
	public static double TIME_OFFSET = 0.16;
	public static double OFFSET_LPM = 0.073;
	
	public static boolean WAVEFORM_CUTOFF = true;
	public static boolean RESIZE_IMAGE_BOUNDS = false;
	public static boolean RESIZE_IMAGE = true;
	public static double RESIZE_IMAGE_RATIO = 0.2;
	public static double PEAK_DIFF_FACTOR = 0.05;
	
	public static boolean VERBOSE_PEAK = false;
	public static boolean DO_WRITE_PEAK_FRAMENUM = true;
	public static boolean DO_WRITE_PEAK_CHANNEL = true;
	
	public static int BAR_WIDTH = 10;
	public static int PEAK_NUM = 1;
	
	public static boolean DO_WINDOW = true;
	public static boolean DO_DRAW = false;
	
	public static int DRAW_TICK_MS = 0;
	public static int DRAW_TICK_PIX = 0;
	
	public static double LPM = 120 + OFFSET_LPM;
	
	public static double FREQ_BLACK = 1500;
	public static double FREQ_WHITE = 2300;
	
	public static double FREQ_W_THRESHOLD = 000;
	public static double FREQ_B_THRESHOLD = 800;

}
