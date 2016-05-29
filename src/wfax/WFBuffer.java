package wfax;

import java.nio.BufferOverflowException;


public class WFBuffer {
	
	private int[] queue;
	private int   size;
	private int   readptr = 0;
	private int   writeptr = 0;
	
	public WFBuffer(int size) {
		queue = new int[size];
		this.size = size;
		for (int i = 0; i < queue.length; i++)
			queue[i] = WFSignal.EMPTY;
	}

	public WFBuffer push(int signal) {
		if (queue[writeptr] != WFSignal.EMPTY)
			throw new BufferOverflowException();
		queue[writeptr++] = signal;
		writeptr %= size;
		return this;
	}
	
	public int pop() {
		int out = queue[readptr];
		if (out != WFSignal.EMPTY) {
			queue[readptr++] = WFSignal.EMPTY;
			readptr %= size;
		}
		return out;
	}

}
