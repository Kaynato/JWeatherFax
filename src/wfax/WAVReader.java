package wfax;

import java.io.File;
import java.io.IOException;

import wfax.wav.WavFile;
import wfax.wav.WavFileException;

public class WAVReader {

	private WaveActor actor;
	private WavFile wavFile;
	private int numChannels;
	private double[] buffer;
	private int frameSize;
	
	public static WAVReader open(String filename, WaveActor actor, int p2size) {
		if (p2size < 2) {
			System.err.println("Buffer too small. Enter 2 or greater.");
			return null;
		}
		
		try {
			return new WAVReader(filename, actor, p2size);
		} catch (Exception e) {
			System.err.println("Could not open " + filename);
			e.printStackTrace();
			return null;
		}
	}

	private WAVReader(String filename, WaveActor actor, int p2size) throws IOException, WavFileException {
		this.actor = actor;
		
		wavFile = WavFile.openWavFile(new File(filename));
		wavFile.display();
		numChannels = wavFile.getNumChannels();
		
		int bufsize = 1;
		
		frameSize = 1 << p2size;
		
		while (bufsize < numChannels * frameSize)
			bufsize <<= 1;
		
		buffer = new double[bufsize];
	}
	
	public long samprate() { 
		return wavFile.getSampleRate();
	}
	
	public int frames() {
		return buffer.length / numChannels;
	}
	
	public int channels() {
		return numChannels;
	}

	public void read() {
		int framesRead = 0;

		do {
			try {
				framesRead = wavFile.readFrames(buffer, frameSize);
			} catch (Exception e) {
				System.err.println("Error occurred while reading " + framesRead + " frames.");
				e.printStackTrace();
			}
			actor.apply(buffer, framesRead);
		}
		while (framesRead != 0);
	}

	public void close() {
		try {
			wavFile.close();
		} catch (IOException e) {
			System.err.println("Error occurred while closing " + wavFile.toString());
		}
	}
}