package lab1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Library {
    @XmlElement
    private int readerIdCounter;

    @XmlElement
    private List<Book> books;
    @XmlElement
    private List<Reader> readers;

    public Library() {
        readerIdCounter = 1;
        books = new ArrayList<>();
        readers = new ArrayList<>();
    }

    public List<Book> getBooks() { return books; }
    public List<Reader> getReaders() { return readers; }

    public Book addBook(String title, String author, int yearOfPublication, String isbn) {
        try {
            for (Book book : books)
            {
                if (book.getIsbn().equals(isbn)) {
                    System.err.println("Such ISBN already exists");
                    return null;
                }
            }
            Book tempBook = new Book(title, author, yearOfPublication, isbn);
            books.add(tempBook);
            return tempBook;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Book getBookByIsbn(String isbn) {
        try {
            for (Book book : books) {
                if (book.getIsbn().equals(isbn)) {
                    return book;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void removeBook(Book book) {
        try {
            if (books.contains(book)) {
                books.remove(book);
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    public void UpdateBookTitle(String isbn, String newTitle)
    {
        try {
            Book book = getBookByIsbn(isbn);
            if (book != null) {
                book.setTitle(newTitle);
            }
            else {
                System.err.println("There is not book with such title");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    public Reader addReader(String firstName, String lastName) {
        try {
            Reader tempReader = new Reader(readerIdCounter, firstName, lastName);
            readers.add(tempReader);
            readerIdCounter++;
            return tempReader;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Reader getReaderById(int id) {
        try {
            for (Reader reader : readers) {
                if (reader.getId() == id) {
                    return reader;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void removeReader(Reader reader) {
        try {
            if (readers.contains(reader)) {
                for (Book book : reader.getBorrowedBooks()) {
                    book.setIsAvailable(true);
                }
                readers.remove(reader);
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    public void updateReaderName(int id, String newFirstName, String newLastName) {
        try {
            Reader reader = getReaderById(id);
            if (reader != null) {
                reader.setFirstName(newFirstName);
                reader.setLastName(newLastName);
            }
            else {
                System.err.println("There is not reader with such name");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Library information:\n");
        if (readers.size() > 0) {
            sb.append("Readers:\n");
            for(Reader reader : readers) {
                sb.append(reader + "\n");
            }
        }
        else {
            sb.append("There is no readers\n");
        }
        if (books.size() > 0) {
            sb.append("\n\nBooks:\n");
            for(Book book : books) {
                sb.append(book + "\n");
            }
        }
        else {
            sb.append("There is no books");
        }
        return sb.toString();
    }

    public enum SortOption {
        READER_ID,
        READER_LAST_NAME,
        READER_BORROWED_BOOKS_COUNT,
        BOOK_TITLE,
        BOOK_YEAR_OF_PUBLICATION,
        BOOK_AVAILABILITY        
    }

    public Library getExportableCopy(SortOption sortOption) {
        Library copy = new Library();
        copy.readerIdCounter = this.readerIdCounter;

        if (sortOption != null) {
            if (sortOption == sortOption.READER_ID || sortOption == sortOption.READER_LAST_NAME || sortOption == sortOption.READER_BORROWED_BOOKS_COUNT) {
                copy.readers = getSortedReaders(sortOption);
            }
            else {
                copy.books = new ArrayList<>(this.books); 
            }

            if (sortOption == sortOption.BOOK_TITLE || sortOption == sortOption.BOOK_YEAR_OF_PUBLICATION || sortOption == sortOption.BOOK_AVAILABILITY) {
                copy.books = getSortedBooks(sortOption);
            }
            else {
                copy.readers = new ArrayList<>(this.readers);
            }
        } else {
            copy.readers = new ArrayList<>(this.readers);
            copy.books = new ArrayList<>(this.books);
        }
        return copy;
    }

    public List<Reader> getSortedReaders(SortOption sortOption) {
        List<Reader> sortedReaders = new ArrayList<>(readers);
        switch (sortOption) {
            case READER_ID:
                sortedReaders.sort(Comparator.comparingInt(Reader::getId));
                break;
            case READER_LAST_NAME:
                sortedReaders.sort(Comparator.comparing(Reader::getLastName));
                break;
            case READER_BORROWED_BOOKS_COUNT:
                sortedReaders.sort(Comparator.comparingInt(Reader::getBorrowedBooksSize));
                break;
            default:
                break;
        }
        return sortedReaders;
    }

    public List<Book> getSortedBooks(SortOption sortOption) {
        List<Book> sortedBooks = new ArrayList<>(books);
        switch (sortOption) {
            case BOOK_TITLE:
                sortedBooks.sort(Comparator.comparing(Book::getTitle));
                break;
            case BOOK_YEAR_OF_PUBLICATION:
                sortedBooks.sort(Comparator.comparingInt(Book::getYearOfPublication));
                break;
            case BOOK_AVAILABILITY:
                sortedBooks.sort(Comparator.comparing(Book::getIsAvailable));
                break;
            default:
                break;
        }
        return sortedBooks;
    }
}
