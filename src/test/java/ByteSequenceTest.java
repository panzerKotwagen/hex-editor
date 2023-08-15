import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ByteSequence class.
 */
public class ByteSequenceTest {

    @Test
    void RepresentZeroReturnZero() {
        ByteSequence byteSequence = new ByteSequence();

        assertEquals(0, byteSequence.representAsUnsigned8Bit());
        assertEquals(0, byteSequence.representAsSigned8Bit());

        assertEquals(0, byteSequence.representAsUnsigned16Bit());
        assertEquals(0, byteSequence.representAsSigned16Bit());

        assertEquals(0, byteSequence.representAsUnsigned32Bit());
        assertEquals(0, byteSequence.representAsSigned32Bit());

        assertEquals(BigInteger.valueOf(0), byteSequence.representAsUnsigned64Bit());
        assertEquals(0, byteSequence.representAsSigned64Bit());

    }

    @Test
    void Represent8BitOnePositiveByte() {
        byte[] testBytes = new byte[]{57};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(57, byteSequence.representAsUnsigned8Bit());
        assertEquals(57, byteSequence.representAsSigned8Bit());
    }

    @Test
    void Represent8BitOneNegativeByte() {
        byte[] testBytes = new byte[]{-62};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(194, byteSequence.representAsUnsigned8Bit());
        assertEquals(-62, byteSequence.representAsSigned8Bit());
    }

    @Test
    void Represent16BitTwoPositiveBytes() {
        byte[] testBytes = new byte[]{56, 57};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(14648, byteSequence.representAsUnsigned16Bit());
        assertEquals(14648, byteSequence.representAsSigned16Bit());
    }

    @Test
    void Represent16BitTwoNegativeBytes() {
        byte[] testBytes = new byte[]{-9, -8};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(63735, byteSequence.representAsUnsigned16Bit());
        assertEquals(-1801, byteSequence.representAsSigned16Bit());
    }

    @Test
    void Represent16BitFourNegativeBytes() {
        byte[] testBytes = new byte[]{-62, -61, -60, -59};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(50114, byteSequence.representAsUnsigned16Bit());
        assertEquals(-15422, byteSequence.representAsSigned16Bit());
    }

    @Test
    void Represent16BitFourPositiveNegativeBytes() {
        byte[] testBytes = new byte[]{21, 82, -47, -16};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(21013, byteSequence.representAsUnsigned16Bit());
        assertEquals(21013, byteSequence.representAsSigned16Bit());
    }

    @Test
    void Represent32BitFourPositiveBytes() {
        byte[] testBytes = new byte[]{32, 55, 56, 57};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(959985440, byteSequence.representAsUnsigned32Bit());
        assertEquals(959985440, byteSequence.representAsSigned32Bit());
    }

    @Test
    void Represent32BitFourNegativeBytes() {
        byte[] testBytes = new byte[]{-62, -61, -60, -59};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(3318006722L, byteSequence.representAsUnsigned32Bit());
        assertEquals(-976960574, byteSequence.representAsSigned32Bit());
    }

    @Test
    void Represent32BitFourPositiveNegativeBytes() {
        byte[] testBytes = new byte[]{21, 82, -47, -16};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(4040249877L, byteSequence.representAsUnsigned32Bit());
        assertEquals(-254717419, byteSequence.representAsSigned32Bit());

        testBytes = new byte[]{-31, 37, -15, 23};
        byteSequence = new ByteSequence(testBytes);

        assertEquals(401679841, byteSequence.representAsUnsigned32Bit());
        assertEquals(401679841, byteSequence.representAsSigned32Bit());
    }

    @Test
    void Represent32BitEightNegativeBytes() {
        byte[] testBytes = new byte[]{-62, -61, -60, -59, -58, -57, -56, -55};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(3318006722L, byteSequence.representAsUnsigned32Bit());
        assertEquals(-976960574, byteSequence.representAsSigned32Bit());
    }

    @Test
    void Represent64BitEightPositiveBytes() {
        byte[] testBytes = new byte[]{32, 52, 53, 54, 32, 55, 56, 57};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(BigInteger.valueOf(4123106070345626656L), byteSequence.representAsUnsigned64Bit());
        assertEquals(4123106070345626656L, byteSequence.representAsSigned64Bit());
    }

    @Test
    void Represent64BitEightNegativeBytes() {
        byte[] testBytes = new byte[]{-62, -61, -60, -59, -58, -57, -56, -55};
        ByteSequence byteSequence = new ByteSequence(testBytes);

        assertEquals(new BigInteger("14540091053501105090"), byteSequence.representAsUnsigned64Bit());
        assertEquals(-3906653020208446526L, byteSequence.representAsSigned64Bit());
    }

    @Test
    void RepresentFloatPositiveBytes() {
        byte[] testBytes = new byte[]{49, 50, 51, 32, 52, 53, 54, 32};
        ByteSequence byteSequence = new ByteSequence(testBytes);
        float e = 1e-45f;

        float diff = byteSequence.representAsFloat() - 1.51784968e-19f;

        assertTrue(diff < e);
    }

    @Test
    void RepresentFloatNegativePositiveBytes() {
        byte[] testBytes = new byte[]{-95, -79, -63, 9, 35, 51, 82, -16};
        ByteSequence byteSequence = new ByteSequence(testBytes);
        float e = 1e-45f;

        float diff = byteSequence.representAsFloat() - 4.6630101e-33f;

        assertTrue(diff < e);
    }

    @Test
    void RepresentDoublePositiveBytes() {
        byte[] testBytes = new byte[]{49, 50, 51, 32, 52, 53, 54, 32};
        ByteSequence byteSequence = new ByteSequence(testBytes);
        double e = 1e-150;

        double diff = byteSequence.representAsDouble() - 1.6563353787836496e-153;

        assertTrue(diff < e);
    }

    @Test
    void RepresentDoubleNegativePositiveBytes() {
        byte[] testBytes = new byte[]{-95, -79, -63, 9, 35, 51, 82, -16};
        ByteSequence byteSequence = new ByteSequence(testBytes);
        double e = 1e-150;

        double diff = byteSequence.representAsDouble() - -1.1302178564830866e+233;

        assertTrue(diff < e);
    }
//
}
