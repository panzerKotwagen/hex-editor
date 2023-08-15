import java.math.BigInteger;

/**
 * A class for working with a sequence of bytes of a length 8.
 */
public class ByteSequence {

    /**
     * Sequence of bytes written in order from left to right.
     */
    private final byte[] byteSequence = new byte[8];

    /**
     * Initializes byteSequence with bytes array values. If its length
     * is greater than 8, only the first 8 values will be recorded.
     * @param bytes
     */
    ByteSequence(byte... bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (i > 7)
                return;
            byteSequence[i] = bytes[i];
        }
    }

    private long respresentAsLongNumber(int byteCount) {
        long res = 0;
        for (int i = 0; i < byteCount; i++) {
            res += (byteSequence[i] & 0xFF) << i;
        }
        return res;
    }

    /**
     * Represents the first 2 bytes of the sequence as unsigned num.
     * @return
     */
    public long representAsUnsigned16Bit() {
        return respresentAsLongNumber(2);
    }

    /**
     * Represents the first 2 bytes of the sequence as signed num.
     * @return
     */
    public short representAsSigned16Bit() {
        return (short)respresentAsLongNumber(2);
    }

    /**
     * Represents the first 4 bytes of the sequence as unsigned num.
     * @return
     */
    public long representAsUnsigned32Bit() {
        return respresentAsLongNumber(4);
    }

    /**
     * Represents the first 4 bytes of the sequence as signed num.
     * @return
     */
    public int representAsSigned32Bit() {
        return (int)respresentAsLongNumber(4);
    }

    /**
     * Represents the byte sequence as unsigned num.
     * @return
     */
    public BigInteger representAsUnsigned64Bit() {
//        BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);
//        long unsignedLong = respresentAsLongNumber(8);
//        BigInteger bi =  BigInteger.valueOf(unsignedLong).and(UNSIGNED_LONG_MASK);
        return new BigInteger(1, byteSequence);
    }

    /**
     * Represents the byte sequence as signed num.
     * @return
     */
    public long representAsSigned64Bit() {
        return respresentAsLongNumber(8);
    }
}
