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
        reader = library.addReader("John", "Doe"); // ID буде 1
    }

    @Test
    void requestBook_Successful_WhenBookAvailableAndInLibrary() {
        assertThat(book1.getIsAvailable()).isTrue();
        assertThat(reader.getBorrowedBooksSize()).isZero();

        reader.requestBook(book1, library);

        assertThat(reader.getBorrowedBooksSize()).isEqualTo(1);
        assertThat(reader.getBorrowedBooks()).containsExactly(book1);
        assertThat(book1.getIsAvailable()).isFalse(); // Перевірка, що книга стала недоступною в бібліотеці
    }

    @Test
    void requestBook_BookNotAvailable() {
        book1.setIsAvailable(false); // Робимо книгу недоступною

        reader.requestBook(book1, library);
        // Очікуємо повідомлення про помилку (можна перехопити System.err, але це складніше)
        // Перевіряємо, що стан не змінився
        assertThat(reader.getBorrowedBooksSize()).isZero();
        assertThat(book1.getIsAvailable()).isFalse(); // Залишилась недоступною
    }

    @Test
    void requestBook_BookNotInLibrary() {
        Book externalBook = new Book("External", "Writer", 2020, "9998887776665");
        assertThat(library.getBooks()).doesNotContain(externalBook);

        reader.requestBook(externalBook, library);
        // Очікуємо повідомлення про помилку
        // Перевіряємо, що стан читача не змінився
        assertThat(reader.getBorrowedBooksSize()).isZero();
    }

     @Test
    void requestBook_NullBook() {
        // Перевіряємо, що не виникає NullPointerException
        assertThatCode(() -> reader.requestBook(null, library))
            .doesNotThrowAnyException();
        // Можливо, варто додати перевірку на null в самому методі requestBook
        assertThat(reader.getBorrowedBooksSize()).isZero();
    }


    @Test
    void returnBook_Successful() {
        // Спочатку читач бере книгу
        reader.requestBook(book1, library);
        assertThat(reader.getBorrowedBooksSize()).isEqualTo(1);
        assertThat(book1.getIsAvailable()).isFalse();

        // Потім повертає
        reader.returnBook(book1, library);

        assertThat(reader.getBorrowedBooksSize()).isZero();
        assertThat(reader.getBorrowedBooks()).isEmpty();
        assertThat(book1.getIsAvailable()).isTrue(); // Книга знову доступна в бібліотеці
    }

    @Test
    void returnBook_ReaderDoesNotHaveBook() {
        assertThat(reader.getBorrowedBooks()).doesNotContain(book2);

        reader.returnBook(book2, library);
        // Очікуємо повідомлення про помилку
        // Перевіряємо, що стан не змінився
        assertThat(reader.getBorrowedBooksSize()).isZero();
        assertThat(book2.getIsAvailable()).isTrue(); // Доступність книги не мала змінитись
    }

    @Test
    void returnBook_NullBook() {
         // Спочатку читач бере книгу
        reader.requestBook(book1, library);
        assertThat(reader.getBorrowedBooksSize()).isEqualTo(1);

        // Перевіряємо, що не виникає NullPointerException
        assertThatCode(() -> reader.returnBook(null, library))
            .doesNotThrowAnyException();
         // Можливо, варто додати перевірку на null в самому методі returnBook
        assertThat(reader.getBorrowedBooksSize()).isEqualTo(1); // Стан не змінився
    }
}