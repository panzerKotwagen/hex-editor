import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardOpenOption.READ;

/**
 * The class that provides work with a file in binary format.
 */
public class HexEditor {

    /**
     * .
     */
    private Path sourceFilePath = null;

    /**
     * .
     */
    private Path tempFilePath = null;


    /**
     * Opens the file at the specified path.
     *
     * @param path string file path
     * @return true if the file was opened and false otherwise
     */
    public boolean openFile(String path) {
        if (sourceFilePath != null) {
            return false;
        }

        try {
            sourceFilePath = Paths.get(path);
        } catch (InvalidPathException e) {
            System.out.println("Path Error: " + e);
            return false;
        }

        String filename = sourceFilePath.getFileName().toString();
        filename = filename.substring(0, filename.lastIndexOf("."));

        try {
            tempFilePath = Files.createTempFile("~" + filename, ".tmp");
            Files.copy(sourceFilePath, tempFilePath, REPLACE_EXISTING);
            tempFilePath.toFile().deleteOnExit();
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    /**
     * Sets paths to null and deletes the temporary file.
     *
     * @return true if the connection was closed and false is there is
     * not opened file.
     */
    public boolean closeFile() {
        if (sourceFilePath == null)
            return false;

        tempFilePath.toFile().delete();

        sourceFilePath = null;
        tempFilePath = null;

        return true;
    }

    /**
     * Copies temporary file data to current file.
     *
     * @return true if changes were successfully saved to the source file
     */
    public boolean saveFile() {
        try {
            Files.copy(tempFilePath, sourceFilePath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    /**
     * Insert the new byte values on a given position with replacement.
     *
     * @param position the file position at which the replacement is to begin
     * @param newBytes new byte values
     * @return true if the operation was successful and false otherwise
     */
    public boolean insert(long position, byte... newBytes) {
        ByteBuffer mBuf = ByteBuffer.wrap(newBytes);
        FileChannel tempFileChannel;

        try {
            tempFileChannel = (FileChannel) Files.newByteChannel(tempFilePath, WRITE);
            mBuf.rewind();
            tempFileChannel.write(mBuf, position);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    /**
     * Replaces a block of bytes with zeros.
     *
     * @param byteCount number of bytes to replace
     * @param position  the file position at which the replacement is to begin
     * @return true if the operation was successful and false otherwise
     */
    public boolean insertZeros(int byteCount, long position) {
        byte[] zeros = new byte[byteCount];
        return insert(position, zeros);
    }

    /**
     * Finds some sequence of bytes specified by the exact value or by
     * some mask.
     *
     * @param position the file position at which the searching is to begin
     * @return match position or -1 if it was not found
     */
    public long findBytesByMask(long position, byte ... mask) {
        MappedByteBuffer mappedByteBuffer;
        long fileSize;
        byte[] readBytes;
        final int bufferSize = 1024;
        FileChannel tempFileChannel;

        try {
            tempFileChannel = (FileChannel) Files.newByteChannel(
                    tempFilePath, READ);
            fileSize = tempFileChannel.size();
        } catch (IOException e) {
            System.out.println(e);
            return -1;
        }

        for (int i = 0; i < fileSize / bufferSize + 1; i++) {
            try {
                mappedByteBuffer = tempFileChannel.map(
                        READ_ONLY, position, bufferSize);
                mappedByteBuffer.rewind();
            } catch (IOException e) {
                System.out.println(e);
                return -1;
            }

            readBytes = new byte[bufferSize];
            mappedByteBuffer.get(readBytes);

            int res = ByteSequence.find(mask, readBytes);

            if (res != -1) return position + res;

            position += bufferSize;
        }

        return -1;
    }

    /**
     * Inserts bytes to the offset position without replacement. The data
     * after the inserted block is shifted towards large addresses.
     *
     * @param offset the file position at which the adding is to begin
     * @param content added bytes
     * @return true if the operation was successful and false otherwise
     */
    public boolean add(long offset, byte... content) {
        try {
            RandomAccessFile r = new RandomAccessFile(tempFilePath.toFile(), "rw");

            Path path = Paths.get("~temp");
            RandomAccessFile rtemp = new RandomAccessFile(path.toFile(), "rw");

            long fileSize = r.length();

            FileChannel sourceChannel = r.getChannel();
            FileChannel targetChannel = rtemp.getChannel();

            sourceChannel.transferTo(offset, (fileSize - offset), targetChannel);
            sourceChannel.truncate(offset);

            r.seek(offset);
            r.write(content);

            long newOffset = r.getFilePointer();
            targetChannel.position(0L);
            sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));

            sourceChannel.close();
            targetChannel.close();

            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Deletes a block of bytes with a data shift after the cut block towards
     * smaller addresses.
     *
     * @param offset the file position at which the deleting is to begin
     * @param count  deleted byte count
     * @return true if the operation was successful and false otherwise
     */
    public boolean delete(long offset, long count) {
        try {
            RandomAccessFile r = new RandomAccessFile(tempFilePath.toFile(), "rw");

            Path path = Paths.get("~temp");
            RandomAccessFile rtemp = new RandomAccessFile(path.toFile(), "rw");

            long fileSize = r.length();

            FileChannel sourceChannel = r.getChannel();
            FileChannel targetChannel = rtemp.getChannel();

            sourceChannel.transferTo(offset + count, (fileSize - offset - count), targetChannel);
            sourceChannel.truncate(offset);

            r.seek(offset);

            long newOffset = r.getFilePointer();
            targetChannel.position(0L);
            sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset - count));

            sourceChannel.close();
            targetChannel.close();
            path.toFile().delete();

            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }
}
