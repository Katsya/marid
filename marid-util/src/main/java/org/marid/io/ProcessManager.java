package org.marid.io;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Dmitry Ovchinnikov
 */
public class ProcessManager implements Closeable {

    private final String name;
    private final Process process;
    private final long timeout;
    private final TimeUnit timeUnit;
    private final InputStream out;
    private final InputStream err;
    private final Thread outConsumer;
    private final Thread errConsumer;

    public ProcessManager(String name,
                          Process process,
                          Consumer<String> outLineConsumer,
                          Consumer<String> errLineConsumer,
                          int bufferSize,
                          long timeout,
                          TimeUnit timeUnit) {
        this.name = name;
        this.process = process;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.out = process.getInputStream();
        this.err = process.getErrorStream();
        this.outConsumer = new ConsumerThread(name + "(out)", out, bufferSize, outLineConsumer);
        this.errConsumer = new ConsumerThread(name + "(err)", err, bufferSize, errLineConsumer);
        outConsumer.start();
        errConsumer.start();
    }

    private void awaitThreads(IOException iox) throws IOException {
        final IOException ex = iox != null ? iox : new IOException();
        try (final InputStream e = err; final InputStream i = out) {
            assert e != null;
            assert i != null;
            try {
                errConsumer.join();
                outConsumer.join();
            } catch (InterruptedException ix) {
                ex.addSuppressed(ix);
            }
        } catch (Exception x) {
            ex.addSuppressed(x);
        }
        if (ex.getMessage() != null || ex.getSuppressed().length > 0) {
            throw ex;
        }
    }

    @Override
    public void close() throws IOException {
        if (process.isAlive()) {
            process.destroy();
            try {
                if (!process.waitFor(timeout, timeUnit) || !process.destroyForcibly().waitFor(timeout, timeUnit)) {
                    awaitThreads(new StreamCorruptedException(name));
                }
            } catch (InterruptedException x) {
                awaitThreads(new InterruptedIOException(process.toString()));
            }
        } else {
            awaitThreads(null);
        }
    }

    private static class ConsumerThread extends Thread {

        private final InputStream inputStream;
        private final Consumer<String> lineConsumer;
        private final int bufferSize;

        private ConsumerThread(String name, InputStream inputStream, int bufferSize, Consumer<String> lineConsumer) {
            super(null, null, name, 96L * 1024L);
            this.inputStream = inputStream;
            this.lineConsumer = lineConsumer;
            this.bufferSize = bufferSize;
        }

        @Override
        public void run() {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), bufferSize)) {
                while (true) {
                    final String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    lineConsumer.accept(line);
                }
            } catch (IOException x) {
                throw new UncheckedIOException("Unexpected exception: " + getName(), x);
            }
        }
    }
}
