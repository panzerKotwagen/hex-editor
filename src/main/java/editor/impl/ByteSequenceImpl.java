package editor.impl;

import editor.ByteSequence;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * The class for working with an immutable sequence of bytes of a
 * given length.
 */
public class ByteSequenceImpl implements ByteSequence {

    /**
     * Sequence of bytes written in little-endian order.
     */
    private final byte[] byteSequence;

    /**
     * Constructs a new byte sequence with given byte array values.
     *
     * @param bytes the byte array the values of which will be copied
     *              to byte sequence
     */
    public ByteSequenceImpl(byte[] bytes) {
        byteSequence = new byte[bytes.length];

        System.arraycopy(bytes, 0, byteSequence, 0, bytes.length);
    }

    /**
     * Compares this byte sequence to the specified object.
     *
     * @param o the object to compare this ByteSequence against
     * @return true if the given object represents a ByteSequence
     * equivalent to this string, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteSequenceImpl that = (ByteSequenceImpl) o;
        return Arrays.equals(byteSequence, that.byteSequence);
    }

    /**
     * Returns a hash code for this byte sequence.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(byteSequence);
    }

    /**
     * Returns the byte at the specified index.
     *
     * @param index the index of the byte value
     * @return the byte value at the specified index of this byte
     * sequence. The first byte value is at index 0.
     */
    public byte getByte(int index) {
        return byteSequence[index];
    }

    /**
     * Returns the length of this byte sequence.
     *
     * @return the length of the sequence
     */
    public int length() {
        return byteSequence.length;
    }

    /**
     * Represents this byte sequence as one long number.
     *
     * @param start     the byte position starting from which the
     *                  number is calculated
     * @param byteCount the number of bytes to calculate
     * @return a calculated long number
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
     * Represents the byte at the specified position as unsigned num.
     *
     * @param index the index of the byte value
     * @return the byte value at the specified index represented as
     * long number
     */
    public long representAsUnsigned8Bit(int index) {

        return (byteSequence[index] & 0xFF);
    }

    /**
     * Represents the byte at the specified position as signed num.
     *
     * @param index the index of the byte value
     * @return the byte value at the specified index
     */
    public byte representAsSigned8Bit(int index) {
        return getByte(index);
    }

    /**
     * Represents the 2 bytes starting from the specified position as
     * unsigned num.
     *
     * @param start the index to start the calculating from
     * @return the long number representing the specified bytes
     */
    public long representAsUnsigned16Bit(int start) {

        return representAsLongNumber(start, 2);
    }

    /**
     * Represents the 2 bytes starting from the specified position as
     * signed num.
     *
     * @param start the index to start the calculating from
     * @return the short number representing the specified bytes
     */
    public short representAsSigned16Bit(int start) {

        return (short) representAsLongNumber(start, 2);
    }

    /**
     * Represents the 4 bytes starting from the specified position as
     * unsigned num.
     *
     * @param start the index to start the calculating from
     * @return the long number representing the specified bytes
     */
    public long representAsUnsigned32Bit(int start) {

        return representAsLongNumber(start, 4);
    }

    /**
     * Represents the 4 bytes starting from the specified position as
     * signed num.
     *
     * @param start the index to start the calculating from
     * @return the int number representing the specified bytes
     */
    public int representAsSigned32Bit(int start) {

        return (int) representAsLongNumber(start, 4);
    }

    /**
     * Represents the 8 bytes starting from the specified position as
     * unsigned num.
     *
     * @param start the index to start the calculating from
     * @return the BigInteger number representing the specified bytes
     */
    public BigInteger representAsUnsigned64Bit(int start) {
        BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);
        long unsignedLong = representAsLongNumber(start, 8);
        return BigInteger.valueOf(unsignedLong).and(UNSIGNED_LONG_MASK);
    }

    /**
     * Represents the 8 bytes starting from the specified position as
     * signed num.
     *
     * @param start the index to start the calculating from
     * @return the long number representing the specified bytes
     */
    public long representAsSigned64Bit(int start) {

        return representAsLongNumber(start, 8);
    }

    /**
     * Represents the 4 bytes starting from the specified position as
     * float number.
     *
     * @param start the index to start the calculating from
     * @return the float number representing the specified bytes
     */
    public float representAsFloat(int start) {

        return Float.intBitsToFloat((int) representAsLongNumber(start, 4));
    }

    /**
     * Represents the 8 bytes starting from the specified position as
     * double number.
     *
     * @param start the index to start the calculating from
     * @return the double number representing the specified bytes
     */
    public double representAsDouble(int start) {

        return Double.longBitsToDouble(representAsLongNumber(start, 8));
    }

    /**
     * Represents the byte sequence as positive BigInteger
     *
     * @param byteCount the number of bytes to calculate
     * @return a calculated BigInteger
     */
    public BigInteger representAsBigInteger(int byteCount) {
        byte[] reverse = getInBigEndianOrder(byteCount);

        return new BigInteger(1, reverse);
    }

    /**
     * Returns a subsequence of bytes in big-endian order.
     *
     * @param byteCount the number of used bytes
     * @return a byte array in big-endian order
     */
    private byte[] getInBigEndianOrder(int byteCount) {
        byte[] reverse = new byte[byteCount];
        System.arraycopy(byteSequence, 0, reverse, 0, byteCount);
        ArrayUtils.reverse(reverse);
        return reverse;
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

        ByteSequenceImpl maskByteSequence = new ByteSequenceImpl(mask);
        ByteSequenceImpl comparedByteSequence = new ByteSequenceImpl(compared);

        BigInteger maskBits = maskByteSequence
                .representAsBigInteger(mask.length);
        BigInteger comparedPartBits = comparedByteSequence
                .representAsBigInteger(mask.length);

        if (maskBits.compareTo(comparedPartBits) == 0)
            return 0;

        for (int i = mask.length; i < compared.length; ++i) {
            // shift one byte to the right removing the lower digit
            comparedPartBits = comparedPartBits.shiftRight(8);

            // insert a new byte on the left
            BigInteger addedByte = BigInteger.valueOf(
                    comparedByteSequence.representAsUnsigned8Bit(i));
            addedByte = addedByte.shiftLeft((mask.length - 1) * 8);
            comparedPartBits = comparedPartBits.add(addedByte);

            boolean res = maskBits.compareTo(comparedPartBits) == 0;

            if (res)
                return i - mask.length + 1;
        }

        return -1;
    }
}
