package wfax;

import java.awt.Color;
import java.util.Arrays;

import org.jtransforms.fft.DoubleFFT_1D;

import wfax.util.ArrayUtil;
import wfax.util.Utils;

public class WFController implements WaveActor {

	/*
	 * also use SAMPLE RATE
	 * and AMOUNT OF FRAMES READ
	 * to make also "Real time" thing
	 */

	// Activity
	private boolean active;

	// Signal receiver and signal buffer
	private WFReceiver receiver;

	private WFBuffer buffer;

	private WFPanel panel;

	// Cursor
	private int y = 0, x = 0;

	// Linking constructor
	public WFController(WFPanel panel) {
		this.panel = panel;
		active = false;
	}

	public void activate() {
		active = true;
	}

	public void deactivate() {
		active = false;
	}

	public boolean isActive() {
		return active;
	}



	public void black() {
		tick(0);
	}

	public void white() {
		tick(255);
	}

	public void tick(int value) {
		panel.image().setPixel(x++, y, value);
		if (x > panel.image().getWidth())
			linefeed();
		if (y > panel.image().getHeight())
			pagefeed();
	}
	
	public void tickRGB(int rgb) {
		panel.image().setRGB(x++, y, rgb);
		if (x > panel.image().getWidth())
			linefeed();
		if (y > panel.image().getHeight())
			pagefeed();
	}






	public void poll() {
		int value = buffer.pop();

		if (value == WFSignal.EMPTY)
			return;

		// If a signal and not a control signal and this is active
		if ((value & 0xFF00) == 0 && isActive())
			tick(value);
		else if (value == WFSignal.START && !isActive())
			activate();
		else if (value == WFSignal.END && isActive())
			deactivate();


	}




	private void linefeed() {
		x = 0;
		y++;
	}

	private void pagefeed() {
		y = 0;
		x = 0;
	}

	
	
	// Apply filter to thing TODO


	public void readWAV(String filename, int sp2, int bufp2) {
		WAVReader reader = WAVReader.open(filename, this, sp2);

		totalFramesRead = 0;
		timeElapsed = 0; 
		timeCount = Globals.TIME_OFFSET;
		fftsize = reader.frames() << bufp2;
		numChannels = reader.channels();

		fft = new DoubleFFT_1D(fftsize);
		waves = new double[numChannels][fftsize];
		freqs = new double[numChannels][waves[0].length];
		peak_inds = new double[numChannels][][];
		buf_i = new int[numChannels];
		peakLast = new double[numChannels];
		peakFreq = new double[numChannels];
		
		Arrays.fill(peakLast, 0);

		for (int i = 0; i < buf_i.length; i++)
			buf_i[i] = 0;

		System.out.println("New FFT 1D with size " + fftsize);

		if (fftsize > panel.image().getWidth() && Globals.RESIZE_IMAGE_BOUNDS)
			panel.resizeImage(fftsize, panel.image().getHeight());
		
		double secPerLine = (60.0/Globals.LPM);
		secPerPixel =  secPerLine / (double)panel.image().getWidth(); // Divide by pixels per line
		System.out.println(secPerPixel + " seconds per pixel");
		sampleRate = reader.samprate();
		System.out.println("Reading " + ((double)(1<<sp2)/(double)sampleRate) + " seconds per pixel.");
		reader.read();
		reader.close();
	}

	private DoubleFFT_1D fft;
	private int fftsize;
	private int numChannels;
	private int[] buf_i;
	private long sampleRate;
	// Channel, Index
	private double[][] waves;
	// Channel, Index
	private double[][] freqs;
	// Channel
	private double[] peakLast;
	private double[] peakFreq;
	// Channel, Peaknum, Peak or Index
	private double[][][] peak_inds;
	
	private long totalFramesRead;
	private double timeElapsed;
	private double timeCount;
	
	private double secPerPixel;

	private void wrapBuf() {
		for (int channel = 0; channel < numChannels; channel++)
			if (buf_i[channel] >= waves[channel].length)
				buf_i[channel] %= waves[channel].length;
	}

	@Override
	public void apply(double[] wave, int framesRead) {
		int channel = 0;
		totalFramesRead += framesRead;
		timeElapsed = (double)totalFramesRead / (double)sampleRate;
		
		wrapBuf();
		//			int[] buf_s = Arrays.copyOf(buf_i, buf_i.length);
		int len = framesRead * numChannels;
		// Split the wave data by channel and run it into the running circular buffer
		for (int i = 0; i < len; i++) {
			waves[channel][buf_i[channel]++] = wave[i];
			if (++channel >= numChannels)
				channel = 0;
			wrapBuf();
		}

//		drawWave(framesRead, numChannels, buf_s);
		peak_inds = freqExtract(framesRead);
		
		for (int ch = 0; ch < numChannels; ch++)
			peakFreq[ch] = freqFromIndex(peak_inds[ch][0][1]);
			
		decodeFax();
		
		
		

//		for (int f_ch = 0; f_ch < peaks.length; f_ch++)
//			for (int i = 0; i < peaks[f_ch].length; i++)
//				freqPrint(peaks, f_ch, i, numChannels);
	}

	private void decodeFax() {
		// 120 LPM
		if (timeElapsed > timeCount) {
			int signal;
			if (peakFreq[0] > Globals.FREQ_WHITE - Globals.FREQ_W_THRESHOLD)
				signal = (0xFFFFFF);
			else if (peakFreq[0] < Globals.FREQ_BLACK + Globals.FREQ_B_THRESHOLD) {
				int value = (int)(255.0*(Globals.FREQ_BLACK - peakFreq[0])/100.0);
				value = value < 0 ? 0 : value > 255 ? 255 : value;
				signal = (0x000000 | value << 24);
			}
			else
				signal = ((int)(255*(peakFreq[0] - Globals.FREQ_BLACK)/(Globals.FREQ_WHITE - Globals.FREQ_BLACK)));
			tick(signal);
			timeCount += secPerPixel;
			while (timeElapsed > timeCount) {
				tick(signal);
				timeCount += secPerPixel;
			}
		}
		else {
			
		}
	}
	
	private double freqFromIndex(double index) {
		return Math.round((0.5 * index * (double)sampleRate) / (double)fftsize);
	}

	private double[][][] freqExtract(int framesRead) {

		// Perform FFT and obtain maximum index and strength
		for (int ch = 0; ch < numChannels; ch++) {
			freqs[ch] = Arrays.copyOf(waves[ch], waves[ch].length);
			fft.realForward(freqs[ch]);
			peak_inds[ch] = ArrayUtil.normalizePeaksABS(freqs[ch], Globals.PEAK_NUM);
		}

		if (Globals.DO_DRAW)
			if (Globals.RESIZE_IMAGE)
				drawFreqFlo();
			else
				drawFreqInt();

		return peak_inds;
	}

	// Draw the fourier output, aliased
	protected void drawFreqInt() {
		int channel;
		int w = panel.image().getWidth();
		int h = panel.image().getHeight();
		int displayWidth = Globals.WAVEFORM_CUTOFF ? fftsize > w ? w : fftsize : fftsize;
		for (int i = 0; i < displayWidth; i++) {
			for (channel = 0; channel < numChannels; channel++) {
				// Box it for channels - vertical boxing
				y = (int)(h*(1+channel-freqs[channel][i])/(double)numChannels);
				int pix = panel.image().getPixel(i, y);
				int k = y;
				// If white, draw black down until hits black
				if (pix != 0) for (;k < h*(1+channel)/numChannels && panel.image().getPixel(i,k) != 0; k++)
					panel.image().setPixel(i, k, 0);
				// If black, draw white up until hits up
				else for (; k > (h*(channel)/numChannels)-1 && panel.image().getPixel(i,k) == 0; k--)
					panel.image().setPixel(i, k, 0xFF);
			}
			channel = 0;
		}
		Utils.msleep(Globals.DRAW_TICK_MS);
	}

	// Draw the fourier output aliased
	protected void drawFreqFlo() {
		int channel;
		WFImage img = panel.image();
		int w = img.getWidth();
		int h = img.getHeight();
		int xpos = 0;
		int xlast = 0;
		boolean clearln = false;
		
		int displaywidth = (int)(Globals.RESIZE_IMAGE_RATIO * fftsize);

		// Half of fftsize is presumably the nyquist frequency
		for (int i = 0; i < displaywidth; i++) {
			xpos = (int)(w*(float)i/(float)displaywidth);
			
			if (xpos > xlast)
				clearln = true;
			xlast = xpos;
			
			// For channels
			for (channel = 0; channel < numChannels; channel++) {
				// Box it for channels - vertical boxing
				y = (int)(h*(1+channel-freqs[channel][i])/(double)numChannels);
				
				// For pixel(s)
				for (int k = clearln ? h*channel/numChannels : y; k < h*(1+channel)/numChannels; k++) {
					if (k < y)
						img.setPixel(xpos, k, 0xFF);
					else {
						int value = img.getPixel(i, k);
						img.setPixel(xpos, k, (4*value)/5);
					}
				}
			}
			clearln = false;
			channel = 0;
		}
		Utils.msleep(Globals.DRAW_TICK_MS);
	}

	// Print the fourier peak
	protected void freqPrint(double[][][] peak_inds, int f_ch, int i, int numChannels) {
		double index = peak_inds[f_ch][i][1];
		peak_inds[f_ch][i][1] = Math.round((0.5 * peak_inds[f_ch][i][1] * (double)sampleRate) / (double)fftsize);

		StringBuilder output = new StringBuilder();
		if (Globals.DO_WRITE_PEAK_CHANNEL)
			output.append("C").append(f_ch).append(" ");
		if (Globals.PEAK_NUM > 1)
			output.append("Peak ").append(i).append(": ");
		output.append(peak_inds[f_ch][i][1]);
		if (Globals.VERBOSE_PEAK)
			output.append("\t Hz @ \ti ").append(index);
		
		if (f_ch == numChannels - 1) {
			if (Globals.DO_WRITE_PEAK_FRAMENUM)
				output.append("\t\tF").append(totalFramesRead);
			output.append('\n');
		}
		else
			output.append('\t');		
		System.out.print(output.toString());
	}

	// Draw audio data itself
	protected void drawWave(int framesRead, int numChannels, int[] buf_s) {
		int channel = 0;

		for (int i = 0; i < framesRead; i++) {
			panel.image().rect(x+2, 0, 1, panel.image().getHeight(), Color.RED);
			panel.image().rect(x+1, 0, 1, panel.image().getHeight(), Color.WHITE);

			for (; channel < numChannels; channel++) {

				int hlfheight = panel.image().getHeight() / 2;

				y = hlfheight - (int)(hlfheight*waves[channel][buf_s[channel] + i]);

				panel.image().setPixel(x, y  , 0x00);
				for (int k = 0; k < Globals.BAR_WIDTH; k++) {
					panel.image().setPixel(x, y+k, (k*0xFF) / Globals.BAR_WIDTH);
					panel.image().setPixel(x, y-k, (k*0xFF) / Globals.BAR_WIDTH);
				}
				Utils.msleep(2);
			}
			channel = 0;

			if (++x >= panel.image().getWidth())
				x = 0;
		}
	}







}
