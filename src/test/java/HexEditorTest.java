import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HexEditorTest {
    HexEditor hexEditor = new HexEditor();

    @Test
    void findInSmallFile() {
        hexEditor.openFile("src/test/resources/test1.txt");
        long res = hexEditor.find(0, new byte[]{54, 32, 55});
        hexEditor.closeFile();
        assertEquals(6, res);
    }

    @Test
    void findInBigFile() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(0, new byte[]{42, (byte) 246, (byte) 215, 108});
        hexEditor.closeFile();
        assertEquals(10_193_044, res);
    }

    @Test
    void findAtTheBeginning() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(0, new byte[]{73, 68});
        hexEditor.closeFile();
        assertEquals(0, res);
    }

    @Test
    void findNonExistingBytes() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(0, new byte[]{-42, (byte) 246, (byte) 215, -108});
        hexEditor.closeFile();
        assertEquals(-1, res);
    }

    @Test
    void findNonExistingBytes2() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(0, new byte[]{-114, 114, 49, 86});
        hexEditor.closeFile();
        assertEquals(-1, res);
    }

    @Test
    void findLongMask() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] mask = new byte[]{103, 46, (byte) 164, 105, (byte) 150, 14, (byte) 161, 94, (byte) 197, 28, (byte) 181, 93, 13, 74, 35, 16};
        long res = hexEditor.find(0, mask);
        hexEditor.closeFile();
        assertEquals(9_852_816, res);
    }

    @Test
    void findAtTheEnd() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] mask = new byte[]{0, 0, (byte) 255};
        long res = hexEditor.find(10_000_000, mask);
        hexEditor.closeFile();
        assertEquals(10_213_769, res);
    }

    @Test
    void fileShouldNotOpenWhenAnotherFileOpened() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertFalse(hexEditor.openFile("src/test/resources/test2.mp3"));
        hexEditor.closeFile();
    }

    @Test
    void openNonExistingFile() {
        assertFalse(hexEditor.openFile("src/test/resources/teASFast1.txt"));
        hexEditor.closeFile();
    }

    @Test
    void readExistingBytesAtTheFileBeginning() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] readBytes = hexEditor.read(0, 8);
        assertArrayEquals(new byte[]{73, 68, 51, 3, 0, 0, 0, 19}, readBytes);
        hexEditor.closeFile();
    }

    @Test
    void readMoreThenExist() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertNull(hexEditor.read(0, 15));
        hexEditor.closeFile();
    }

    @Test
    void readMoreThenExist2() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertNull(hexEditor.read(50, 1));
        hexEditor.closeFile();
    }

    @Test
    void readFromNegativePosition() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        assertNull(hexEditor.read(-500, 1));
        hexEditor.closeFile();
    }

    @Test
    void readNegativeByteCount() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        assertNull(hexEditor.read(0, -100));
        hexEditor.closeFile();
    }

    @Test
    void findFromNegativePosition() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(-500, new byte[]{73, 68});
        hexEditor.closeFile();
        assertEquals(-1, res);
    }

    @Test
    void readZeroBytes() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertArrayEquals(new byte[0], hexEditor.read(0, 0));
        hexEditor.closeFile();
    }

    @Test
    void readTenMillionBytes() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        assertEquals(10_000_000, hexEditor.read(213771, 10_000_000).length);
        hexEditor.closeFile();
    }

    @Test
    void readExistingBytesAtTheFileEnd() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] readBytes = hexEditor.read(10_213_768, 4);
        assertArrayEquals(new byte[]{0, 0, 0, (byte) 255}, readBytes);
        hexEditor.closeFile();
    }

    @Test
    void insertBytesIntoFile() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.insert(1, new byte[]{10, 15, -99});
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();
        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(1, hexEditor.find(0, new byte[]{10, 15, -99}));
        hexEditor.closeFile();
    }

    @Test
    void addBytesIntoFileBeginning() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.add(0, new byte[]{10, 15, -99});
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();

        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(0, hexEditor.find(0, new byte[]{10, 15, -99, 49, 50}));
        hexEditor.closeFile();
    }

    @Test
    void addBytesIntoFileMiddle() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.add(10, new byte[]{10, 15, -99});
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();
        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(9, hexEditor.find(0, new byte[]{56, 10, 15, -99, 57}));
        hexEditor.closeFile();
    }

    @Test
    void addBytesIntoFileEnd() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.add(11, new byte[]{10, 15, -99});
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();

        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(9, hexEditor.find(9, new byte[]{56, 57, 10, 15, -99}));
        hexEditor.closeFile();
    }

    @Test
    void addBytesIntoNegativePosition() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertFalse(hexEditor.add(-10, new byte[]{10, 15, -99}));
        hexEditor.closeFile();
    }

    @Test
    void addBytesIntoPositionThatIsBiggerThanFileSize() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertFalse(hexEditor.add(50, new byte[]{10, 15, -99}));
        hexEditor.closeFile();
    }

    @Test
    void addZeroBytes() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertTrue(hexEditor.add(0));
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();

        hexEditor.openFile("src/test/resources/test3.txt");
        byte[] readBytes = hexEditor.read(0, 10);
        assertEquals(0, hexEditor.find(0, readBytes));
        hexEditor.closeFile();
    }

    @Test
    void addMoreBytesThanFileSize() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertTrue(hexEditor.add(0, new byte[100]));
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();

        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(0, hexEditor.find(0, new byte[100]));
        hexEditor.closeFile();
    }
}
