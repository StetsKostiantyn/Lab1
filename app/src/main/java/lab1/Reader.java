package lab1;

import java.util.*;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Reader {
    private int id;
    private String firstName;
    private String lastName;
    private List<Book> borrowedBooks = new ArrayList<>();

    public Reader() {

    }

    public Reader(int id, String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("Cannot create a Book with first name and last name");
        }
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    @XmlElement
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    @XmlElement
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    @XmlElement
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    @XmlElement
    public List<Book> getBorrowedBooks() { return borrowedBooks; }
    public void setBorrowedBooks(List<Book> borrowedBooks) { this.borrowedBooks = borrowedBooks; }
    public int getBorrowedBooksSize() { return borrowedBooks.size(); }

    public void requestBook(Book book, Library library) {
        try {
            if (book != null ) {
                if (book.getIsAvailable()) {
                    if (library.getBooks().contains(book)) {
                        borrowedBooks.add(book);
                        library.getBookByIsbn(book.getIsbn()).setIsAvailable(false);
                    }
                    else {
                        System.err.println("There is not such book in the library");
                    }
                }
                else {
                    System.err.println("This book is not available");
                }
            }
            else {
                System.err.println("Book can not ne null");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void returnBook(Book book, Library library) {
        try {
            if (book != null) {
                if (borrowedBooks.contains(book)) {
                    borrowedBooks.remove(book);
                    library.getBookByIsbn(book.getIsbn()).setIsAvailable(true);
                } else {
                    System.err.println("This reader does not have such book");
                }
            }
            else {
                System.err.println("Book can not ne null");
            }
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return Objects.equals(id, reader.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\u001B[34m");
        sb.append("Reader ID: " + id + "\n");
        sb.append("First name: " + firstName + "\n");
        sb.append("Last name: " + lastName + "\n");
        if (borrowedBooks.size() > 0) {
            sb.append("\u001B[0mBorrowed books:\n\u001B[34m");
            for(Book book : borrowedBooks) {
                sb.append(book + "\n");
            }
        }
        else {
            sb.append("\u001B[0mThere is no borrowed books\n");
        }
        sb.append("\u001B[0m");
        return sb.toString();
    }
}
