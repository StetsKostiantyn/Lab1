package lab1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lab1.Library.SortOption;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir; // Для створення тимчасових файлів/директорій

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.*;

public class XMLManagerTest {

    private Library library;
    private Book book1, book2;
    private Reader reader1, reader2;

    // @TempDir автоматично створює та видаляє тимчасову директорію для тестів
    @TempDir
    Path tempDir;

    private Path xmlFilePath;

    // Для перехоплення System.err
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        library = new Library();
        book1 = library.addBook("Title A", "Author Z", 2000, "1111111111111");
        book2 = library.addBook("Title B", "Author Y", 1990, "2222222222222");
        reader1 = library.addReader("Zoe", "Last"); // ID 1
        reader2 = library.addReader("Adam", "First"); // ID 2

        // Читач бере книгу для перевірки стану при імпорті/експорті
        reader1.requestBook(book1, library);

        // Встановлюємо шлях до файлу в тимчасовій директорії
        xmlFilePath = tempDir.resolve("test_library.xml");

        // Перенаправляємо System.err
        System.setErr(new PrintStream(errContent));
    }

     @org.junit.jupiter.api.AfterEach
     public void restoreStreams() {
         // Відновлюємо System.err після кожного тесту
         System.setErr(originalErr);
     }

     @Test
    void TestXMLExport() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        XMLManager.ExportToXml(library, outputStream, null);

        String xmlOutput = outputStream.toString();

        assertTrue(xmlOutput.contains("<library>"));
        assertTrue(xmlOutput.contains("readerIdCount"));

        outputStream.close();
    }

    @Test
    void TestXMLExport_Mock() throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Library library = new Library();
        Library mockLibrary = mock(Library.class);
        when(mockLibrary.getReaders()).thenReturn(library.getReaders());
        when(mockLibrary.getBooks()).thenReturn(library.getBooks());
        when(mockLibrary.getExportableCopy(Library.SortOption.BOOK_AVAILABILITY)).thenReturn(library);
        XMLManager.ExportToXml(mockLibrary, outputStream, Library.SortOption.BOOK_AVAILABILITY);
        String xmlOutput = outputStream.toString();

        assertEquals(true, xmlOutput.contains("<library>"));
        assertEquals(true, xmlOutput.contains("readerIdCounter"));
        assertEquals(false, xmlOutput.contains("book"));
        outputStream.close();
    }

    @Test
    void ImportFromXMLMocked() {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();

        Library mockLibrary = mock(Library.class);
        when(mockLibrary.getBooks()).thenReturn(library.getBooks());
        when(mockLibrary.getReaders()).thenReturn(library.getReaders());
        when(mockLibrary.getExportableCopy(Library.SortOption.READER_ID)).thenReturn(library);
        XMLManager.ExportToXml(mockLibrary, fos, Library.SortOption.READER_ID);

        String xmlOutput = fos.toString();
        assertEquals(false, xmlOutput.contains("3333333333333"));
        assertEquals(true, xmlOutput.contains("Zoe"));
    }
}