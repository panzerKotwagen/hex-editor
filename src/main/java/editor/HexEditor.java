package editor;


public interface HexEditor {
    /**
     * Opens the file at the specified path.
     *
     * @param path string file path
     * @return true if the file was opened and false otherwise
     */
    boolean openFile(String path);

    /**
     * Sets paths to null and deletes the temporary file.
     *
     * @return true if the operation was successful and false
     * otherwise
     */
    boolean closeFile();

    /**
     * Copies the temporary file data to the current opened file.
     *
     * @return true if changes were successfully saved to the source
     * file
     */
    boolean saveFile();

    /**
     * Creates a new file with the temporary file data.
     *
     * @return true if a new file was successfully created
     */
    boolean saveAsNewFile(String filename);

    /**
     * Reads the specified number of bytes from the specified position
     *
     * @param offset the file position at which the reading is to
     *                 begin
     * @param count    read byte count
     * @return the read bytes or null if the read occurred with an
     * error
     */
    byte[] read(long offset, int count);

    /**
     * Returns the file size of the open file.
     *
     * @return the size of the open file or -1 if there is no such
     */
    long getFileSize();

    /**
     * Insert the new byte values on a given position with replacement.
     *
     * @param position the file position at which the replacement is
     *                 to begin
     * @param newBytes new byte values
     * @return true if the operation was successful and false otherwise
     */
    boolean insert(long position, byte... newBytes);

    /**
     * Replaces a block of bytes with zeros.
     *
     * @param byteCount number of bytes to replace
     * @param position  the file position at which the replacement is
     *                  to begin
     * @return true if the operation was successful and false otherwise
     */
    boolean insertZeros(int byteCount, long position);

    /**
     * Finds some sequence of bytes specified by the exact value or by
     * some mask.
     *
     * @param offset the file position at which the searching is to
     *                 begin
     * @return match position or -1 if it was not found
     */
    long find(long offset, byte... mask);

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
    boolean add(long offset, byte... addedBytes);

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
    boolean delete(long offset, long count);
}
