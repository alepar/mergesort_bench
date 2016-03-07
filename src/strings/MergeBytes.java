package strings;

import java.io.*;

public class MergeBytes {

    public static void main(String[] args) throws Exception {
        final String filenameA = args[0];
        final String filenameB = args[1];
        final String filenameC = args[2];

        final BufferedFile il = new BufferedFile(new FileInputStream(filenameA));
        final BufferedFile ir = new BufferedFile(new FileInputStream(filenameB));
        final OutputStream w = new BufferedOutputStream(new FileOutputStream(filenameC));

        final long startNanos = System.nanoTime();

        BufferedChar sl = il.firstChar();
        BufferedChar sr = ir.firstChar();

        BufferedChar el = il.firstChar();
        BufferedChar er = ir.firstChar();

        while (!sl.eof() && !sr.eof() && el.nextNewLine() && er.nextNewLine()) {
            if (compare(sl, sr) < 0) {
                write(w, sl, el);
                sl.rewind(el);
                sl.nextChar();
            } else {
                write(w, sr, er);
                sr.rewind(er);
                sr.nextChar();
            }
        }

        //todo write tail

        w.close();
        il.close();
        ir.close();

        final long endNanos = System.nanoTime();

        System.out.println((endNanos-startNanos)/1000_000/100/10.0);
    }

    private static BufferedChar pos = new BufferedChar(null);
    private static void write(OutputStream os, BufferedChar start, BufferedChar end) {
        try {
            pos.rewind(start);
            while (pos.block != end.block) {
                os.write(pos.block.buf, pos.offset, pos.block.buf.length-pos.offset);
                pos.block = pos.block.next;
                pos.offset = 0;
            }

            os.write(pos.block.buf, pos.offset, end.offset-pos.offset+1);
        } catch (IOException e) {
            throw new RuntimeException("woops", e);
        }
    }

    private static BufferedChar left = new BufferedChar(null);
    private static BufferedChar right = new BufferedChar(null);
    private static int compare(BufferedChar sl, BufferedChar sr) {
        left.rewind(sl);
        right.rewind(sr);

        while (left.get() == right.get() && left.get() != '\n') {
            left.nextChar();
            right.nextChar();
        }

        if (left.get() == right.get()) {
            return 0;
        }
        if (left.get() == '\n') {
            return -1;
        }
        if (right.get() == '\n') {
            return 1;
        }

        return Byte.compare(left.get(), right.get());
    }

    public static class BufferedFile {

        private final InputStream is;

        private BufferedBlock lastBlock;

        public BufferedFile(InputStream is) {
            this.is = is;
        }

        public void close() throws IOException {
            is.close();
        }

        public BufferedChar firstChar() {
            readNextBlock();
            return new BufferedChar(lastBlock);
        }

        private void readNextBlock() {
            try {
                final byte[] buf = new byte[128 * 1024];
                final int read = is.read(buf);

                if (read == -1 && lastBlock.length == -1) {
                    return;
                }

                final BufferedBlock readBlock = new BufferedBlock(this, buf, read);
                if (lastBlock != null) {
                    lastBlock.next = readBlock;
                }
                lastBlock = readBlock;
            } catch (Exception e) {
                throw new RuntimeException("woops", e);
            }
        }
    }

    private static class BufferedChar {

        private BufferedBlock block;
        private int offset;

        private BufferedChar(BufferedBlock block) {
            this.block = block;
        }

        public void nextChar() {
            offset++;

            if (offset == block.length) {
                block.readNextBlock();

                if (block.next != null) {
                    block = block.next;
                    offset = 0;
                }
            }
        }

        public boolean eof() {
            return block.length == -1 || (offset == block.length && block.next == null);
        }

        public void rewind(BufferedChar dst) {
            this.block = dst.block;
            this.offset = dst.offset;
        }

        public boolean nextNewLine() {
            while (!eof()) {
                for (offset = offset+1; offset<block.length; offset++) {
                    if (block.buf[offset] == '\n') {
                        return true;
                    }
                }

                block.readNextBlock();
                block = block.next;
                offset = 0;
            }

            return false;
        }

        public byte get() {
            return block.buf[offset];
        }
    }

    private static class BufferedBlock {

        private final BufferedFile file;
        private final byte[] buf;
        private final int length;

        private BufferedBlock next;

        private BufferedBlock(BufferedFile file, byte[] buf, int length) {
            this.file = file;
            this.buf = buf;
            this.length = length;
        }

        public void readNextBlock() {
            file.readNextBlock();
        }
    }
}
