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
     * The Path associated with the current opened file.
     */
    private Path sourceFilePath = null;

    /**
     * The Path associated with the copy of the current opened file.
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
            e.printStackTrace();
            return false;
        }

        try {
            tempFilePath = Files.createTempFile("~", ".tmp");
            Files.copy(sourceFilePath, tempFilePath, REPLACE_EXISTING);
            tempFilePath.toFile().deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Sets paths to null and deletes the temporary file.
     *
     * @return true if the operation was successful and false
     * otherwise
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
     * Copies the temporary file data to the current opened file.
     *
     * @return true if changes were successfully saved to the source
     * file
     */
    public boolean saveFile() {
        try {
            Files.copy(tempFilePath, sourceFilePath, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Creates a new file with the temporary file data.
     *
     * @return true if a new file was successfully created
     */
    public boolean saveAsNewFile(String filename) {
        Path newFile;
        try {
            newFile = Paths.get(filename);
        } catch (InvalidPathException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Files.copy(tempFilePath, newFile, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
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
        try (FileChannel tempFileChannel = (FileChannel) Files.newByteChannel(
                tempFilePath, WRITE)) {
            ByteBuffer mBuf = ByteBuffer.wrap(newBytes);
            mBuf.rewind();
            tempFileChannel.write(mBuf, position);
        } catch (IOException e) {
            e.printStackTrace();
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
    public long findBytesByMask(long position, byte... mask) {
        byte[] readBytes;
        int bufferSize = 1024 * 1024;
        long bytesToRead;
        int res;

        try (FileChannel tempFileChannel = (FileChannel) Files.newByteChannel(
                tempFilePath, READ)) {

            bytesToRead = tempFileChannel.size();

            while (bytesToRead > 0) {
                if (bytesToRead < bufferSize)
                    readBytes = read(position, (int) bytesToRead);
                else
                    readBytes = read(position, bufferSize);

                try {
                    res = ByteSequence.find(mask, readBytes);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return -1;
                }

                if (res != -1) return position + res;

                // The mask length is subtracted to consider the case
                // when the required sequence is divided between two
                // buffers
                position += bufferSize - mask.length;
                bytesToRead -= bufferSize - mask.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    /**
     * Inserts bytes to the offset position without replacement. The data
     * after the inserted block is shifted towards large addresses.
     *
     * @param offset     the file position at which the adding is to begin
     * @param addedBytes added bytes
     * @return true if the operation was successful and false otherwise
     */
    public boolean add(long offset, byte... addedBytes) {
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
            r.write(addedBytes);

            long newOffset = r.getFilePointer();
            targetChannel.position(0L);
            sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));

            sourceChannel.close();
            targetChannel.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads the specified number of bytes from the specified position
     *
     * @param position the file position at which the reading is to
     *                 begin
     * @param count    read byte count
     * @return the read bytes or null if the read occurred with an
     * error
     */
    public byte[] read(long position, int count) {
        byte[] readBytes;

        try (FileChannel tempFileChannel = (FileChannel) Files.newByteChannel(
                tempFilePath, READ)) {
            MappedByteBuffer mappedByteBuffer = tempFileChannel.map(
                    READ_ONLY, position, count);
            mappedByteBuffer.rewind();

            readBytes = new byte[count];
            mappedByteBuffer.get(readBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return readBytes;
    }
}