package lab1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class LibraryTest {

    private Library library;
    private Book book1, book2;
    private Reader reader1, reader2;

    @TempDir
    Path tempDir;

    private Path xmlFilePath;

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        library = new Library();
        // Додаємо книги для тестів
        book1 = library.addBook("The Hobbit", "J.R.R. Tolkien", 1937, "1111111111111");
        book2 = library.addBook("1984", "George Orwell", 1949, "2222222222222");
        // Додаємо читачів для тестів
        reader1 = library.addReader("Alice", "Smith"); // ID 1
        reader2 = library.addReader("Bob", "Johnson"); // ID 2

        xmlFilePath = tempDir.resolve("test_library.xml");
        System.setErr(new PrintStream(errContent));
    }

    @org.junit.jupiter.api.AfterEach
     public void restoreStreams() {
         // Відновлюємо System.err після кожного тесту
         System.setErr(originalErr);
     }

    @Test
    void addBook_Successful() {
        int initialSize = library.getBooks().size();
        Book newBook = library.addBook("New Title", "New Author", 2025, "1000000000001");
        assertThat(newBook).isNotNull();
        assertThat(library.getBooks()).hasSize(initialSize + 1);
        assertThat(library.getBooks()).contains(newBook);
        assertThat(newBook.getTitle()).isEqualTo("New Title");
    }

    @Test
    void addBook_DuplicateIsbn_ShouldNotAddAndReturnNull() {
        int initialSize = library.getBooks().size();
        // Намагаємось додати книгу з існуючим ISBN book1
        Book duplicateBook = library.addBook("Another Title", "Another Author", 2000, "1111111111111");
        assertThat(duplicateBook).isNull();
        assertThat(library.getBooks()).hasSize(initialSize); // Розмір не змінився
        // Перевіряємо, що оригінальна книга все ще там
        assertThat(library.getBookByIsbn("1111111111111")).isSameAs(book1);
    }

     @Test
    void getBookByIsbn_Found() {
        Book foundBook = library.getBookByIsbn("2222222222222"); // ISBN of book2
        assertThat(foundBook).isNotNull();
        assertThat(foundBook).isSameAs(book2);
    }

    @Test
    void getBookByIsbn_NotFound() {
        Book notFoundBook = library.getBookByIsbn("0000000000");
        assertThat(notFoundBook).isNull();
    }

    @Test
    void removeBook_Existing() {
        int initialSize = library.getBooks().size();
        assertThat(library.getBooks()).contains(book2);
        library.removeBook(book2);
        assertThat(library.getBooks()).hasSize(initialSize - 1);
        assertThat(library.getBooks()).doesNotContain(book2);
    }

    @Test
    void removeBook_NonExisting() {
         int initialSize = library.getBooks().size();
         Book nonExistingBook = new Book("NonExist", "Ghost", 2000, "5555555555555");
         library.removeBook(nonExistingBook);
         assertThat(library.getBooks()).hasSize(initialSize); // Розмір не змінився
    }

    @Test
    void updateBookTitle_Existing() {
        String newTitle = "Nineteen Eighty-Four";
        library.UpdateBookTitle("2222222222222", newTitle); // ISBN of book2
        Book updatedBook = library.getBookByIsbn("2222222222222");
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getTitle()).isEqualTo(newTitle);
        assertThat(updatedBook).isSameAs(book2); // Перевіряємо, що це той самий об'єкт
    }

    @Test
    void updateBookTitle_NonExisting() {
        // Спробуємо оновити неіснуючу книгу
        library.UpdateBookTitle("0000000000000", "No Such Title");
        // Переконуємось, що нічого не змінилося з існуючими
        assertThat(library.getBookByIsbn("2222222222222").getTitle()).isEqualTo("1984");
    }

     @Test
    void addReader_Successful() {
        int initialSize = library.getReaders().size();
        Reader newReader = library.addReader("Charlie", "Brown");
        assertThat(newReader).isNotNull();
        assertThat(library.getReaders()).hasSize(initialSize + 1);
        assertThat(library.getReaders()).contains(newReader);
        assertThat(newReader.getFirstName()).isEqualTo("Charlie");
        assertThat(newReader.getId()).isEqualTo(initialSize + 1); // ID має інкрементуватись
    }

    @Test
    void addReader_InvalidData_ShouldNotAddAndReturnNull() {
        int initialSize = library.getReaders().size();
        Reader invalidReader = library.addReader(null, "LastName");
        assertThat(invalidReader).isNull();
        assertThat(library.getReaders()).hasSize(initialSize);
    }

    @Test
    void getReaderById_Found() {
        Reader foundReader = library.getReaderById(2); // ID of reader2
        assertThat(foundReader).isNotNull();
        assertThat(foundReader).isSameAs(reader2);
    }

    @Test
    void getReaderById_NotFound() {
        Reader notFoundReader = library.getReaderById(999);
        assertThat(notFoundReader).isNull();
    }

    @Test
    void removeReader_Existing_WithBorrowedBooks() {
        // Спочатку читач бере книгу
        reader1.requestBook(book1, library);
        assertThat(book1.getIsAvailable()).isFalse();
        assertThat(library.getReaders()).contains(reader1);
        int initialReaderSize = library.getReaders().size();

        // Видаляємо читача
        library.removeReader(reader1);

        assertThat(library.getReaders()).hasSize(initialReaderSize - 1);
        assertThat(library.getReaders()).doesNotContain(reader1);
        // Перевіряємо, що книга, яку він мав, стала доступною
        assertThat(book1.getIsAvailable()).isTrue();
    }

     @Test
    void removeReader_Existing_WithoutBorrowedBooks() {
        int initialReaderSize = library.getReaders().size();
        assertThat(reader2.getBorrowedBooks()).isEmpty(); // Переконуємось, що книг немає
        assertThat(library.getReaders()).contains(reader2);

        library.removeReader(reader2);

        assertThat(library.getReaders()).hasSize(initialReaderSize - 1);
        assertThat(library.getReaders()).doesNotContain(reader2);
    }


    @Test
    void removeReader_NonExisting() {
        int initialReaderSize = library.getReaders().size();
        Reader nonExistingReader = new Reader(999, "Ghost", "Reader");
        library.removeReader(nonExistingReader);
        assertThat(library.getReaders()).hasSize(initialReaderSize); // Розмір не змінився
    }

    @Test
    void updateReaderName_Existing() {
        String newFirstName = "Robert";
        String newLastName = "Jones";
        library.updateReaderName(2, newFirstName, newLastName); // ID of reader2
        Reader updatedReader = library.getReaderById(2);
        assertThat(updatedReader).isNotNull();
        assertThat(updatedReader.getFirstName()).isEqualTo(newFirstName);
        assertThat(updatedReader.getLastName()).isEqualTo(newLastName);
        assertThat(updatedReader).isSameAs(reader2); // Той самий об'єкт
    }

    @Test
    void updateReaderName_NonExisting() {
        library.updateReaderName(999, "No", "Body");
        // Переконуємось, що імена існуючих не змінились
        assertThat(library.getReaderById(1).getFirstName()).isEqualTo("Alice");
        assertThat(library.getReaderById(2).getFirstName()).isEqualTo("Bob");
    }
}