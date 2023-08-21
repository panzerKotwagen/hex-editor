import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HexEditorTest {
    HexEditor hexEditor = new HexEditor();

    @Test
    void testFindBytesByMask() {
        hexEditor.openFile("src/test/resources/test1.txt");
        long res = hexEditor.findBytesByMask(0, new byte[]{54, 32, 55});
        hexEditor.closeFile();
        assertEquals(6, res);
    }

    @Test
    void testFindBytesByMask1() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.findBytesByMask(0, new byte[]{42, (byte) 246, (byte) 215, 108});
        hexEditor.closeFile();
        assertEquals(10_193_044, res);
    }

    @Test
    void testFindBytesByMask2() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.findBytesByMask(0, new byte[]{73, 68});
        hexEditor.closeFile();
        assertEquals(0, res);
    }

    @Test
    void testFindBytesByMask3() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.findBytesByMask(0, new byte[]{-42, (byte) 246, (byte) 215, -108});
        hexEditor.closeFile();
        assertEquals(-1, res);
    }

    @Test
    void testFindBytesByMask4() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.findBytesByMask(0, new byte[]{-114, 114, 49, 86});
        hexEditor.closeFile();
        assertEquals(-1, res);
    }

    @Test
    void testFindBytesByMask5() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] mask = new byte[]{103, 46, (byte) 164, 105, (byte) 150, 14, (byte) 161, 94, (byte) 197, 28, (byte) 181, 93, 13, 74, 35, 16};
        long res = hexEditor.findBytesByMask(0, mask);
        hexEditor.closeFile();
        assertEquals(9_852_816, res);
    }
}
