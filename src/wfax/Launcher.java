package wfax;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import wfax.util.Utils;

// Portability reasons.
public class Launcher {

	public static void main(String[] args) {
		WFWindow window = new WFWindow();

		int countdown = 0;

		String inputFileName = Globals.inputName;
		String outputFileName = Globals.outputName;

		outputFileName = "";
		
		if (outputFileName.length() > 0) {
			try {
				System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFileName)), true));
			} catch (FileNotFoundException e) {
				System.err.println("output file not found! using standard output");
			}
		}

		for (int i = 0; i < countdown; i++) {
			System.out.println(1000*(countdown-i) + "ms left");
			Utils.msleep(1000);
		}

		//		window.getPanel().getController().readWAV("D:\\Users\\Brian\\Music\\Myda.wav", 4, 10);
		//		window.getPanel().getController().readWAV("D:\\Users\\Brian\\Music\\The Hands that Grab.wav", 9, 0);
		//		window.getPanel().getController().readWAV("D:\\Users\\Brian\\Music\\The Hands that Grab.wav", 4, 10);
		//		window.getPanel().getController().readWAV("D:\\Users\\Brian\\Music\\wfa\\WEATHERFAX.wav", 4, 10);
//		window.getPanel().getController().readWAV(inputFileName, 4, 10);
//		window.getPanel().getController().readWAV(inputFileName, 7, 2);
//		window.getPanel().getController().readWAV(inputFileName, 5, 0);
		window.getPanel().getController().readWAV(inputFileName, Globals.WINDOWSIZE, Globals.CARRYSIZE);
		//		window.getPanel().getController().readWAV("D:\\Users\\Brian\\Music\\250Hz_44100Hz_16bit_05sec.wav", 4, 10);
		//		window.getPanel().getController().readWAV("D:\\Users\\Brian\\Music\\440Hz_44100Hz_16bit_05sec.wav", 4, 10);
		//		window.getPanel().getController().readWAV("D:\\Users\\Brian\\Music\\250Hz_44100Hz_16bit_05sec.wav", 4, 12);
	}

}
