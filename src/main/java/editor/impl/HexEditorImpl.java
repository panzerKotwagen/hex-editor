package editor.impl;

import editor.HexEditor;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;


/**
 * The class that provides work with a file in binary format.
 */
public class HexEditorImpl implements HexEditor {

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
            if (!sourceFilePath.isAbsolute())
                sourceFilePath = sourceFilePath.toAbsolutePath();
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
     * Reads the specified number of bytes from the specified position
     *
     * @param offset the file position at which the reading is to
     *                 begin
     * @param count    read byte count
     * @return the read bytes or null if the read occurred with an
     * error
     */
    public byte[] read(long offset, int count) {
        if (offset >= getFileSize() || count < 0)
            return null;

        if (offset + count > getFileSize())
            count = (int)(getFileSize() - offset);

        byte[] readBytes = new byte[count];

        try (FileChannel tempFileChannel = (FileChannel) Files.newByteChannel(
                tempFilePath, READ)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 4);

            while (tempFileChannel.read(buffer, offset) != -1 && count > 0) {
                if (count < buffer.limit())
                    buffer.limit(count);

                buffer.flip();
                buffer.get(readBytes, readBytes.length - count, buffer.limit());

                count -= buffer.limit();
                offset += buffer.limit();

                buffer.clear();
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
        return readBytes;
    }

    /**
     * Returns the file size of the open file.
     *
     * @return the size of the open file or -1 if there is no such
     */
    public long getFileSize() {
        if (sourceFilePath == null) {
            return -1;
        }

        try (FileChannel tempFileChannel = (FileChannel) Files.newByteChannel(
                tempFilePath, READ)) {
            return tempFileChannel.size();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Insert the new byte values on a given position with replacement.
     *
     * @param position the file position at which the replacement is
     *                 to begin
     * @param newBytes new byte values
     * @return true if the operation was successful and false otherwise
     */
    public boolean insert(long position, byte... newBytes) {
        try (FileChannel tempFileChannel = (FileChannel) Files.newByteChannel(
                tempFilePath, WRITE)) {
            ByteBuffer mBuf = ByteBuffer.wrap(newBytes);
            mBuf.rewind();
            tempFileChannel.write(mBuf, position);
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Replaces a block of bytes with zeros.
     *
     * @param byteCount number of bytes to replace
     * @param position  the file position at which the replacement is
     *                  to begin
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
     * @param offset the file position at which the searching is to
     *                 begin
     * @return match position or -1 if it was not found
     */
    public long find(long offset, byte... mask) {
        byte[] readBytes;
        final int BUFFER_SIZE = Math.max(mask.length * 2, 1024 * 1024);
        int res = -1;

        while ((readBytes = read(offset, BUFFER_SIZE)) != null) {
            try {
                res = ByteSequenceImpl.find(mask, readBytes);
            } catch (NullPointerException e) {
                e.printStackTrace();
                return -1;
            }
            if (res != -1)
                return offset + res;
            // The mask length is subtracted to consider the case
            // when the required sequence is divided between two
            // buffers
            offset += BUFFER_SIZE - mask.length;
        }

        return res;
    }

    /**
     * Inserts bytes to the offset position without replacement. The
     * data after the inserted block is shifted towards large
     * addresses. If the <code>offset</code> is bigger than file size
     * fills <code>(offset - fileSize)</code> positions with zeros
     * starting from the <code>(offset + 1)</code>.
     *
     * @param offset     the file position at which the adding is to
     *                   begin
     * @param addedBytes added bytes
     * @return true if the operation was successful and false otherwise
     */
    public boolean add(long offset, byte... addedBytes) {
        Path path = Paths.get("~temp");

        try (RandomAccessFile r = new RandomAccessFile(
                tempFilePath.toFile(), "rw");
             RandomAccessFile rTemp = new RandomAccessFile(
                     path.toFile(), "rw")) {

            try (FileChannel sourceChannel = r.getChannel();
                 FileChannel targetChannel = rTemp.getChannel()) {

                long fileSize = r.length();
                long newOffset;

                try {
                    sourceChannel.transferTo(offset, (fileSize - offset),
                            targetChannel);
                }
                catch (IllegalArgumentException e) {
                    if (offset < 0) {
                        return false;
                    }

                    // If the insert offset is bigger than file size
                    // fill (offset - fileSize) positions with zeros.
                    int zeroCount = (int) (offset - fileSize);
                    addedBytes = ArrayUtils.addAll(new byte[zeroCount], addedBytes);
                    offset = fileSize;
                }

                sourceChannel.truncate(offset);

                r.seek(offset);
                r.write(addedBytes);

                newOffset = r.getFilePointer();
                targetChannel.position(0L);
                sourceChannel.transferFrom(targetChannel, newOffset,
                        (fileSize - offset));
            } catch (IllegalArgumentException | NullPointerException e) {
                e.printStackTrace();
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            path.toFile().delete();
        }
        return true;
    }

    /**
     * Deletes a block of bytes with a data shift after the cut block
     * towards smaller addresses. If <code>(offset + count)</code> more
     * than file size then deletes all bytes from the <code>offset</code>
     * to the end of the file.
     *
     * @param offset the file position at which the deleting is to begin
     * @param count  deleted byte count
     * @return true if the operation was successful and false otherwise
     */
    public boolean delete(long offset, long count) {
        Path path = Paths.get("~temp");

        try (RandomAccessFile r = new RandomAccessFile(
                tempFilePath.toFile(), "rw");
             RandomAccessFile rTemp = new RandomAccessFile(
                     path.toFile(), "rw")) {

            try (FileChannel sourceChannel = r.getChannel();
                 FileChannel targetChannel = rTemp.getChannel()) {

                long fileSize = r.length();
                long newOffset;

                if (offset + count > fileSize)
                    count = fileSize - offset;

                sourceChannel.transferTo(offset + count,
                        (fileSize - offset - count), targetChannel);
                sourceChannel.truncate(offset);

                r.seek(offset);

                newOffset = r.getFilePointer();
                targetChannel.position(0L);
                sourceChannel.transferFrom(targetChannel, newOffset,
                        (fileSize - offset - count));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            path.toFile().delete();
        }
        return true;
    }
}
