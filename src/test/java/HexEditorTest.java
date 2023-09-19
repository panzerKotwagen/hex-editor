import editor.HexEditor;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HexEditorTest {
    HexEditor hexEditor = new HexEditor();

    @AfterEach
    void closeFile() {
        hexEditor.closeFile();
    }

    @Test
    void findInSmallFile() {
        hexEditor.openFile("src/test/resources/test1.txt");
        long res = hexEditor.find(0, new byte[]{54, 32, 55});
        assertEquals(6, res);
    }

    @Test
    void findInBigFile() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(0, new byte[]{42, (byte) 246, (byte) 215, 108});
        assertEquals(10_193_044, res);
    }

    @Test
    void findAtTheBeginning() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(0, new byte[]{73, 68});
        assertEquals(0, res);
    }

    @Test
    void findNonExistingBytes() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(0, new byte[]{-42, (byte) 246, (byte) 215, -108});
        assertEquals(-1, res);
    }

    @Test
    void findNonExistingBytes2() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(0, new byte[]{-114, 114, 49, 86});
        assertEquals(-1, res);
    }

    @Test
    void findLongMask() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] mask = new byte[]{103, 46, (byte) 164, 105, (byte) 150, 14, (byte) 161, 94, (byte) 197, 28, (byte) 181, 93, 13, 74, 35, 16};
        long res = hexEditor.find(0, mask);
        assertEquals(9_852_816, res);
    }

    @Test
    void findAtTheEnd() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] mask = new byte[]{0, 0, (byte) 255};
        long res = hexEditor.find(10_000_000, mask);
        assertEquals(10_213_769, res);
    }

    @Test
    void findFromNegativePosition() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        long res = hexEditor.find(-500, new byte[]{73, 68});
        assertEquals(-1, res);
    }

    @Test
    void findVeryLongMask() {
        hexEditor.openFile("src/test/resources/test1.txt");
        byte[] mask = ArrayUtils.addAll(new byte[91_147_647], new byte[]{99});
        hexEditor.add(10000, mask);
        assertEquals(10000, hexEditor.find(0, mask));
    }

    @Test
    void findStartingFromPositionBiggerThanFileSize() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] mask = ArrayUtils.addAll(new byte[]{99});
        assertEquals(-1, hexEditor.find(hexEditor.getFileSize() + 1, mask));
    }

    @Test
    void fileShouldNotOpenWhenAnotherFileOpened() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertFalse(hexEditor.openFile("src/test/resources/test2.mp3"));
    }

    @Test
    void openNonExistingFile() {
        assertFalse(hexEditor.openFile("src/test/resources/teASFast1.txt"));
    }

    @Test
    void readExistingBytesAtTheFileBeginning() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        byte[] readBytes = hexEditor.read(0, 8);
        assertArrayEquals(new byte[]{73, 68, 51, 3, 0, 0, 0, 19}, readBytes);
    }

    @Test
    void readMoreThenExist() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertArrayEquals(hexEditor.read(0, 11), hexEditor.read(0, 15));
    }

    @Test
    void readMoreThenExist2() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertNull(hexEditor.read(50, 1));
    }

    @Test
    void readFromNegativePosition() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        assertNull(hexEditor.read(-500, 1));
    }

    @Test
    void readNegativeByteCount() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        assertNull(hexEditor.read(0, -100));
    }

    @Test
    void readZeroBytes() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertArrayEquals(new byte[0], hexEditor.read(0, 0));
    }

    @Test
    void readTenMillionBytes() {
        hexEditor.openFile("src/test/resources/test2.mp3");
        assertEquals(10_000_000, hexEditor.read(213771, 10_000_000).length);
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
    }

    @Test
    void insertBytesIntoNegativePosition() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertFalse(hexEditor.insert(-1, new byte[]{10, 15, -99}));
    }

    @Test
    void insertBytesIntoPositionBiggerThanFileSize() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.insert(9000, new byte[]{10, 15, -99});
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();
        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(9000, hexEditor.find(9000, new byte[]{10, 15, -99}));
        assertEquals(9003, hexEditor.getFileSize());
    }

    @Test
    void insertMoreBytesThanFileSize() {
        hexEditor.openFile("src/test/resources/test1.txt");
        byte[] mask = ArrayUtils.addAll(new byte[91_147_647], new byte[]{99});
        hexEditor.insert(9000, mask);
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();
        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(9000, hexEditor.find(9000, mask));
    }

    @Test
    void addBytesIntoFileBeginning() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.add(0, new byte[]{10, 15, -99});
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();

        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(0, hexEditor.find(0, new byte[]{10, 15, -99, 49, 50}));
    }

    @Test
    void addBytesIntoFileMiddle() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.add(10, new byte[]{10, 15, -99});
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();
        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(9, hexEditor.find(0, new byte[]{56, 10, 15, -99, 57}));
    }

    @Test
    void addBytesIntoFileEnd() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.add(11, new byte[]{10, 15, -99});
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();

        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(9, hexEditor.find(9, new byte[]{56, 57, 10, 15, -99}));
    }

    @Test
    void addBytesIntoNegativePosition() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertFalse(hexEditor.add(-10, new byte[]{10, 15, -99}));
    }

    @Test
    void addBytesIntoPositionThatIsBiggerThanFileSize() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.add(50, new byte[]{10, 15, -99});
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0, 0, 10, 15, -99}, hexEditor.read(43, 50));
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
    }

    @Test
    void addMoreBytesThanFileSize() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertTrue(hexEditor.add(0, new byte[100]));
        hexEditor.saveAsNewFile("src/test/resources/test3.txt");
        hexEditor.closeFile();

        hexEditor.openFile("src/test/resources/test3.txt");
        assertEquals(0, hexEditor.find(0, new byte[100]));
    }

    @Test
    void deleteAllFile() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.delete(0, hexEditor.getFileSize());
        assertEquals(0, hexEditor.getFileSize());
    }

    @Test
    void deleteAtNegativePosition() {
        hexEditor.openFile("src/test/resources/test1.txt");
        assertFalse(hexEditor.delete(-1, hexEditor.getFileSize()));
    }

    @Test
    void deleteMoreThanExists() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.delete(2, hexEditor.getFileSize() * 999);
        assertEquals(2, hexEditor.getFileSize());
    }

    @Test
    void deleteWhenFileIsNotOpened() {
        assertThrows(NullPointerException.class,
                () -> hexEditor.delete(0, 0));
    }

    @Test
    void deleteZeroBytes() {
        hexEditor.openFile("src/test/resources/test1.txt");
        hexEditor.delete(0, 0);
        assertEquals(11, hexEditor.getFileSize());
    }
}
