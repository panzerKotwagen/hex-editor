import java.math.BigInteger;

/**
 * A class for working with a sequence of bytes of a length 8.
 */
public class ByteSequence {
    public static void main(String[] args) {
        byte[] testBytes = new byte[] {32, 52, 53, 54, 32, 55, 56, 57};
        ByteSequence byteSequence = new ByteSequence(testBytes);
        BigInteger bi = byteSequence.representAsUnsigned64Bit();
        System.out.println(bi);
    }

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
            res += (long)(byteSequence[i] & 0xFF) << (8 * i);
        }
        return res;
    }

    /**
     * Represents the first byte of the sequence as unsigned num.
     * @return
     */
    public long representAsUnsigned8Bit() {
        return (long)byteSequence[0];
    }

    /**
     * Represents the first byte of the sequence as signed num.
     * @return
     */
    public byte representAsSigned8Bit() {
        return byteSequence[0];
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
        BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);
        long unsignedLong = respresentAsLongNumber(8);
        return  BigInteger.valueOf(unsignedLong).and(UNSIGNED_LONG_MASK);
    }

    /**
     * Represents the byte sequence as signed num.
     * @return
     */
    public long representAsSigned64Bit() {
        return respresentAsLongNumber(8);
    }
}
