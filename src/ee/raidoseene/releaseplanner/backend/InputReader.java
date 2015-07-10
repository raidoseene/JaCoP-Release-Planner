/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Raido Seene
 */
public final class InputReader {

    private static long counter = 0L;
    private final Thread thread;

    public InputReader(InputStream input, InputListener listener) {
        InputReader.Processor reader = new InputReader.Processor(input, listener);
        this.thread = new Thread(reader, "InputReader thread " + (counter++));
        this.thread.setPriority(Thread.MIN_PRIORITY);
        this.thread.setDaemon(true);

        this.thread.start();
    }
    
    public boolean isAlive() {
        return this.thread.isAlive();
    }

    private final class Processor implements Runnable {

        private final InputListener listener;
        private final InputStream stream;

        private Processor(InputStream in, InputListener l) {
            this.listener = l;
            this.stream = in;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.stream))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    try {
                        this.listener.lineRead(line);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            } catch (Throwable th) {
                try {
                    this.listener.errorThrown(th);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            try {
                this.listener.finishedReading();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }

}
