import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ByteSequenceTest {

    @Test
    void test1() {
        byte[] testBytes = new byte[] {57};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(57, byteSequence.representAsSigned16Bit());
        assertEquals(57, byteSequence.representAsUnsigned16Bit());
    }

    @Test
    void test2() {
        byte[] testBytes = new byte[] {56, 57};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(14648, byteSequence.representAsSigned16Bit());
        assertEquals(14648, byteSequence.representAsUnsigned16Bit());
    }

    @Test
    void test3() {
        byte[] testBytes = new byte[] {32, 55, 56, 57};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(14112, byteSequence.representAsSigned16Bit());
        assertEquals(14112, byteSequence.representAsUnsigned16Bit());

        assertEquals(959985440, byteSequence.representAsSigned32Bit());
        assertEquals(959985440, byteSequence.representAsUnsigned32Bit());
    }

    @Test
    void test4() {
        byte[] testBytes = new byte[] {32, 52, 53, 54, 32, 55, 56, 57};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(13344, byteSequence.representAsSigned16Bit());
        assertEquals(13344, byteSequence.representAsUnsigned16Bit());

        assertEquals(909456416, byteSequence.representAsSigned32Bit());
        assertEquals(909456416, byteSequence.representAsUnsigned32Bit());

        assertEquals(4123106070345626656L, byteSequence.representAsSigned64Bit());
        assertEquals(BigInteger.valueOf(4123106070345626656L), byteSequence.representAsUnsigned64Bit());
    }

    @Test
    void test5() {
        byte[] testBytes = new byte[] {-62, -61, -60, -59, -58, -57, -56, -55};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(194, byteSequence.representAsUnsigned8Bit());
        assertEquals(-62, byteSequence.representAsSigned8Bit());

        assertEquals(50114, byteSequence.representAsUnsigned16Bit());
        assertEquals(-15422, byteSequence.representAsSigned16Bit());

        assertEquals(3318006722L, byteSequence.representAsUnsigned32Bit());
        assertEquals(-976960574, byteSequence.representAsSigned32Bit());

        assertEquals(new BigInteger("14540091053501105090"), byteSequence.representAsUnsigned64Bit());
        assertEquals(-3906653020208446526L, byteSequence.representAsSigned64Bit());
    }
}
