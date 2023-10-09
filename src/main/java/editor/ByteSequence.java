package editor;

import java.math.BigInteger;

/**
 * The interface for working with an immutable sequence of bytes of a
 * given length.
 */
public interface ByteSequence {

    /**
     * Returns the byte at the specified index.
     *
     * @param index the index of the byte value
     * @return the byte value at the specified index of this byte
     * sequence. The first byte value is at index 0.
     */
    byte getByte(int index);

    /**
     * Returns the length of this byte sequence.
     *
     * @return the length of the sequence
     */
    int length();

    /**
     * Represents the byte at the specified position as unsigned num.
     *
     * @param index the index of the byte value
     * @return the byte value at the specified index represented as
     * long number
     */
    long representAsUnsigned8Bit(int index);

    /**
     * Represents the byte at the specified position as signed num.
     *
     * @param index the index of the byte value
     * @return the byte value at the specified index
     */
    byte representAsSigned8Bit(int index);

    /**
     * Represents the 2 bytes starting from the specified position as
     * unsigned num.
     *
     * @param start the index to start the calculating from
     * @return the long number representing the specified bytes
     */
    long representAsUnsigned16Bit(int start);

    /**
     * Represents the 2 bytes starting from the specified position as
     * signed num.
     *
     * @param start the index to start the calculating from
     * @return the short number representing the specified bytes
     */
    short representAsSigned16Bit(int start);

    /**
     * Represents the 4 bytes starting from the specified position as
     * unsigned num.
     *
     * @param start the index to start the calculating from
     * @return the long number representing the specified bytes
     */
    long representAsUnsigned32Bit(int start);

    /**
     * Represents the 4 bytes starting from the specified position as
     * signed num.
     *
     * @param start the index to start the calculating from
     * @return the int number representing the specified bytes
     */
    int representAsSigned32Bit(int start);

    /**
     * Represents the 8 bytes starting from the specified position as
     * unsigned num.
     *
     * @param start the index to start the calculating from
     * @return the BigInteger number representing the specified bytes
     */
    BigInteger representAsUnsigned64Bit(int start);

    /**
     * Represents the 8 bytes starting from the specified position as
     * signed num.
     *
     * @param start the index to start the calculating from
     * @return the long number representing the specified bytes
     */
    long representAsSigned64Bit(int start);

    /**
     * Represents the 4 bytes starting from the specified position as
     * float number.
     *
     * @param start the index to start the calculating from
     * @return the float number representing the specified bytes
     */
    float representAsFloat(int start);

    /**
     * Represents the 8 bytes starting from the specified position as
     * double number.
     *
     * @param start the index to start the calculating from
     * @return the double number representing the specified bytes
     */
    double representAsDouble(int start);

    /**
     * Represents the byte sequence as positive BigInteger
     *
     * @param byteCount the number of bytes to calculate
     * @return a calculated BigInteger
     */
    BigInteger representAsBigInteger(int byteCount);


}
