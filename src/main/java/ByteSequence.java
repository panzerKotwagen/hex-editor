import java.math.BigInteger;
import java.util.Arrays;

/**
 * A class for working with a sequence of bytes of a length 8.
 */
public class ByteSequence {
    public static void main(String[] args) {
    }

    /**
     * Sequence of bytes written in order from left to right.
     */
    private final byte[] byteSequence = new byte[8];

    /**
     * Default constructor.
     */
    ByteSequence() {
    }

    /**
     * Initializes byteSequence with bytes array values. If its length
     * is greater than 8, only the first 8 values will be recorded.
     *
     * @param bytes
     */
    ByteSequence(byte... bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (i > 7)
                return;
            byteSequence[i] = bytes[i];
        }
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
     * @param pos the index of the byte
     * @return the byte at the specified index
     */
    public byte getByte(int pos) {
        return byteSequence[pos];
    }

    /**
     * Converts a sequence of bytes to one long number.
     *
     * @param byteCount
     * @return
     */
    private long respresentAsLongNumber(int byteCount) {
        long res = 0;
        for (int i = 0; i < byteCount; i++) {
            res += (long) (byteSequence[i] & 0xFF) << (8 * i);
        }
        return res;
    }

    /**
     * Represents the first byte of the sequence as unsigned num.
     *
     * @return
     */
    public long representAsUnsigned8Bit() {
        return (byteSequence[0] & 0xFF);
    }

    /**
     * Represents the first byte of the sequence as signed num.
     *
     * @return
     */
    public byte representAsSigned8Bit() {
        return byteSequence[0];
    }

    /**
     * Represents the first 2 bytes of the sequence as unsigned num.
     *
     * @return
     */
    public long representAsUnsigned16Bit() {
        return respresentAsLongNumber(2);
    }

    /**
     * Represents the first 2 bytes of the sequence as signed num.
     *
     * @return
     */
    public short representAsSigned16Bit() {
        return (short) respresentAsLongNumber(2);
    }

    /**
     * Represents the first 4 bytes of the sequence as unsigned num.
     *
     * @return
     */
    public long representAsUnsigned32Bit() {
        return respresentAsLongNumber(4);
    }

    /**
     * Represents the first 4 bytes of the sequence as signed num.
     *
     * @return
     */
    public int representAsSigned32Bit() {
        return (int) respresentAsLongNumber(4);
    }

    /**
     * Represents the byte sequence as unsigned num.
     *
     * @return
     */
    public BigInteger representAsUnsigned64Bit() {
        BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);
        long unsignedLong = respresentAsLongNumber(8);
        return BigInteger.valueOf(unsignedLong).and(UNSIGNED_LONG_MASK);
    }

    /**
     * Represents the byte sequence as signed num.
     *
     * @return
     */
    public long representAsSigned64Bit() {
        return respresentAsLongNumber(8);
    }

    /**
     * @return
     */
    public float representAsFloat() {
        return Float.intBitsToFloat((int) respresentAsLongNumber(4));
    }

    /**
     * @return
     */
    public double representAsDouble() {
        return Double.longBitsToDouble(respresentAsLongNumber(8));
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
        if (mask.length > compared.length)
            return -1;

        if (compared.length > 8) return -1;

        ByteSequence maskByteSequence = new ByteSequence(mask);
        ByteSequence comparedByteSequence = new ByteSequence(compared);

        long maskBits = maskByteSequence.representAsSigned64Bit();
        long comparedPartBits = comparedByteSequence.representAsSigned64Bit();

        long a = (long) Math.pow(2, mask.length * 8) - 1;
        comparedPartBits = comparedPartBits & a; //cut to the size of the mask

        for (int i = 0; i <= compared.length - mask.length; ++i) {
            boolean res = (maskBits & comparedPartBits) == maskBits;

            if (res == true)
                return i;

            // shift one byte to the right removing the lower digit
            comparedPartBits = (comparedPartBits >> 8);

            // insert a new byte on the left
            byte addedByte = comparedByteSequence.getByte(mask.length + i);
            comparedPartBits += (long) (addedByte & 0xFF) << 24;
        }

        return -1;
    }
}
