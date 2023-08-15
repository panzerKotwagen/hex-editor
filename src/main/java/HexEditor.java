import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The main class for working with files in binary format.
 */
public class HexEditor {

    /**
     * A channel for working with current file.
     */
    private SeekableByteChannel currentFileChannel = null;


    /**
     *
     * @param filename Name of the file.
     * @return true if the file was opened.
     */
    public boolean openFile(String filename) {
        Path filepath;

        if (currentFileChannel != null) {
            System.err.println("Error open file: last file was not closed");
            return false;
        }

        try {
            filepath = Paths.get(filename);
        } catch (InvalidPathException e) {
            System.out.println("Path Error " + e);
            return false;
        }

        try {
            currentFileChannel = Files.newByteChannel(filepath);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    /**
     * Prints all the file on console. If there is no file to work with
     * then doing nothing
     */
    public void readAll() {
        if (currentFileChannel == null) return;

        ByteBuffer mBuf = ByteBuffer.allocate(4096);
        int count;
        int offset = 0;

        do {
            try {
                count = currentFileChannel.read(mBuf);
            } catch (IOException e) {
                System.out.println(e);
                return;
            }

            if (count != -1) {
                mBuf.rewind();

                for (int i = 0; i < count; i++) {
                    if (offset % 16 == 0) {
                        String hexAddress = String.format("%1$08X", offset);
                        System.out.print("\n" + hexAddress + " ");
                    }

                    byte b = mBuf.get();
                    System.out.print(String.format("%1$02X", b) + " ");
                    offset++;
                }
            }
        } while (count != -1);
        System.out.println();
    }

    /**
     * Close the channel connected with current file
     * @return true if the connection was closed, returns false
     * if there is no file or IOException was raised
     */
    public boolean closeCurrentFile() {
        if (currentFileChannel == null)
            return false;

        try {
            currentFileChannel.close();
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        currentFileChannel = null;
        return true;
    }
}
