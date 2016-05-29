package wfax.unused;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.*;

public class MP3Reader {

	public MP3Reader() {
		// TODO Auto-generated constructor stub
	}

	public short[] read() {

		boolean condition = true;

		Bitstream bitStream = null;
		
		try {
			bitStream = new Bitstream(new FileInputStream("path/to/audio.mp3"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while(condition){
			Decoder decoder = new Decoder();
			try {
				SampleBuffer samples = (SampleBuffer)decoder.decodeFrame(bitStream.readFrame(), bitStream);
				return samples.getBuffer();
			} catch (DecoderException | BitstreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //returns the next 2304 samples
			bitStream.closeFrame();

			//do whatever with your samples
		}
		
		return null;
	}

}
