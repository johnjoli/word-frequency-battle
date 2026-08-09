import com.bootcamp.service.WordCounter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WordCounterTest {
    @Test
    void testBasicCount() throws Exception {
        Path tempFile = Files.createTempFile("test", "txt");
        Files.writeString(tempFile, "Java, java! Java?");
        WordCounter wc = new WordCounter();
        Map<String, Long> result = wc.countWords(tempFile);
        assertEquals(3L, result.get("java"));
    }

    @Test
    void emptyFile_shouldReturnEmptyMap() throws Exception {
        Path file = createTempFile("");
        Map<String, Long> result = new WordCounter().countWords(file);
        assertTrue(result.isEmpty());
    }

    @Test
    void onlyPunctuationAndWhitespace_shouldReturnEmptyMap() throws Exception {
        Path file = createTempFile(" ! @ # $ % ^ & * ( ) \t \n ");
        Map<String, Long> result = new WordCounter().countWords(file);
        assertTrue(result.isEmpty());
    }

    @Test
    void numbersShouldBeAsWords() throws Exception {
        Path file = createTempFile("123 456 123");
        Map<String, Long> result = new WordCounter().countWords(file);
        assertEquals(1L, result.get("456"));
        assertEquals(2L, result.get("123"));
    }

    @Test
    void nonExistentFileShouldThrowsIOException() throws Exception {
        Path file = Path.of("there_is_no_such_file.txt");
        assertThrows(IOException.class, () -> new WordCounter().countWords(file));
    }

    private Path createTempFile(String content) throws Exception {
        Path file = Files.createTempFile("test", "txt");
        Files.writeString(file, content);
        return file;
    }
}