package lab1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ReaderTest {

    private Library library;
    private Book book1;
    private Book book2;
    private Reader reader;

    @BeforeEach
    void setUp() {
        library = new Library();
        book1 = library.addBook("Title 1", "Author 1", 2001, "1111111111111");
        book2 = library.addBook("Title 2", "Author 2", 2002, "2222222222222");
        reader = library.addReader("John", "Doe");
    }

    @Test
    void requestBook_Successful_WhenBookAvailableAndInLibrary() {
        assertThat(book1.getIsAvailable()).isTrue();
        assertThat(reader.getBorrowedBooksSize()).isZero();

        reader.requestBook(book1, library);

        assertThat(reader.getBorrowedBooksSize()).isEqualTo(1);
        assertThat(reader.getBorrowedBooks()).containsExactly(book1);
        assertThat(book1.getIsAvailable()).isFalse();
    }

    @Test
    void requestBook_BookNotAvailable() {
        book1.setIsAvailable(false);

        reader.requestBook(book1, library);
        assertThat(reader.getBorrowedBooksSize()).isZero();
        assertThat(book1.getIsAvailable()).isFalse();
    }

    @Test
    void requestBook_BookNotInLibrary() {
        Book externalBook = new Book("External", "Writer", 2020, "9998887776665");
        assertThat(library.getBooks()).doesNotContain(externalBook);

        reader.requestBook(externalBook, library);
        assertThat(reader.getBorrowedBooksSize()).isZero();
    }

     @Test
    void requestBook_NullBook() {
        assertThatCode(() -> reader.requestBook(null, library))
            .doesNotThrowAnyException();
        assertThat(reader.getBorrowedBooksSize()).isZero();
    }


    @Test
    void returnBook_Successful() {
        reader.requestBook(book1, library);
        assertThat(reader.getBorrowedBooksSize()).isEqualTo(1);
        assertThat(book1.getIsAvailable()).isFalse();

        reader.returnBook(book1, library);

        assertThat(reader.getBorrowedBooksSize()).isZero();
        assertThat(reader.getBorrowedBooks()).isEmpty();
        assertThat(book1.getIsAvailable()).isTrue();
    }

    @Test
    void returnBook_ReaderDoesNotHaveBook() {
        assertThat(reader.getBorrowedBooks()).doesNotContain(book2);

        reader.returnBook(book2, library);
        assertThat(reader.getBorrowedBooksSize()).isZero();
        assertThat(book2.getIsAvailable()).isTrue();
    }

    @Test
    void returnBook_NullBook() {
        reader.requestBook(book1, library);
        assertThat(reader.getBorrowedBooksSize()).isEqualTo(1);

        assertThatCode(() -> reader.returnBook(null, library))
            .doesNotThrowAnyException();
        assertThat(reader.getBorrowedBooksSize()).isEqualTo(1);
    }
}