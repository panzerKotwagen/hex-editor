import java.math.BigInteger;
import java.util.Arrays;

/**
 * A class for working with a sequence of bytes of given length.
 */
public class ByteSequence {
    public static void main(String[] args) {
    }

    /**
     * Sequence of bytes written in little-endian order.
     */
    private byte[] byteSequence;

    /**
     * Initializes byteSequence with byte array values.
     *
     * @param bytes
     */
    ByteSequence(byte... bytes) {
        byteSequence = new byte[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            byteSequence[i] = bytes[i];
        }
    }

    ByteSequence(int length) {
        byteSequence = new byte[length];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteSequence that = (ByteSequence) o;
        return Arrays.equals(byteSequence, that.byteSequence);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(byteSequence);
    }

    /**
     * Returns the byte at the specified index.
     *
     * @param index the index of the byte
     * @return the byte at the specified index
     */
    public byte getByte(int index) {
        return byteSequence[index];
    }

    public int length() {
        return byteSequence.length;
    }

    /**
     * Converts a sequence of bytes to one long number.
     *
     * @param byteCount
     * @param start     the byte position starting from which the number is calculated
     * @return
     */
    private long representAsLongNumber(int start, int byteCount) {
        long res = 0;

        if (start + byteCount > length()) {
            byteCount = length();
        }

        for (int i = start; i < byteCount; i++) {
            res += (long) (byteSequence[i] & 0xFF) << (8 * i);
        }
        return res;
    }

    /**
     * Represents the byte of the sequence as unsigned num.
     *
     * @return
     */
    public long representAsUnsigned8Bit(int index) {

        return (byteSequence[index] & 0xFF);
    }

    /**
     * Represents the first byte of the sequence as signed num.
     *
     * @return
     */
    public byte representAsSigned8Bit(int index) {
        return getByte(index);
    }

    /**
     * Represents the 2 bytes of the sequence as unsigned num.
     *
     * @return
     */
    public long representAsUnsigned16Bit(int start) {

        return representAsLongNumber(start, 2);
    }

    /**
     * Represents the first 2 bytes of the sequence as signed num.
     *
     * @return
     */
    public short representAsSigned16Bit(int start) {

        return (short) representAsLongNumber(start, 2);
    }

    /**
     * Represents the first 4 bytes of the sequence as unsigned num.
     *
     * @return
     */
    public long representAsUnsigned32Bit(int start) {

        return representAsLongNumber(start, 4);
    }

    /**
     * Represents the first 4 bytes of the sequence as signed num.
     *
     * @return
     */
    public int representAsSigned32Bit(int start) {

        return (int) representAsLongNumber(start, 4);
    }

    /**
     * Represents the byte sequence as unsigned num.
     *
     * @return
     */
    public BigInteger representAsUnsigned64Bit(int start) {
        BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);
        long unsignedLong = representAsLongNumber(start, 8);
        return BigInteger.valueOf(unsignedLong).and(UNSIGNED_LONG_MASK);
    }

    /**
     * Represents the byte sequence as signed num.
     *
     * @return
     */
    public long representAsSigned64Bit(int start) {

        return representAsLongNumber(start, 8);
    }

    /**
     * Represents the byte sequence as float num.
     *
     * @return
     */
    public float representAsFloat(int start) {

        return Float.intBitsToFloat((int) representAsLongNumber(start, 4));
    }

    /**
     * Represents the byte sequence as double num.
     *
     * @return
     */
    public double representAsDouble(int start) {

        return Double.longBitsToDouble(representAsLongNumber(start, 8));
    }

    /**
     * Represents a byte array as a string in hex format with leading zeros.
     *
     * @param bytes byte array
     * @return string representation in hex format
     */
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /**
     * Finds the position from which the mask equals the compared byte array.
     *
     * @param mask     the byte mask
     * @param compared the byte sequence with which the mask is compared
     * @return the index within this mask of the first occurrence of the
     * specified byte array.
     */
    public static int find(byte[] mask, byte[] compared) {
        String maskString = byteArrayToHexString(mask);
        String comparedString = byteArrayToHexString(compared);

        return comparedString.indexOf(maskString) / 2;
    }
}
