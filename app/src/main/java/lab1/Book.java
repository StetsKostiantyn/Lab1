package lab1;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Book {
    private String title;

    private String author;

    private int yearOfPublication;

    private String isbn;

    private boolean isAvailable;

    public Book() {

    }

    public Book(String title, String author, int yearOfPublication, String isbn) {
        if (title == null || author == null) {
            throw new IllegalArgumentException("Cannot create a Book with nullable title and author");
        }
        if (isbn.length() != 13) {
            throw new IllegalArgumentException("Cannot create a Book. ISBN must contain 13 digits");
        }
        this.title = title;
        this.author = author;
        this.yearOfPublication = yearOfPublication;
        this.isbn = isbn;
        this.isAvailable = true;
    }
    @XmlElement
    public String getTitle() { return title; }
    public void setTitle(String title) {this.title = title; } 
    @XmlElement
    public String getAuthor() { return author; }
    public void setAuthor(String author) {this.author = author; } 
    @XmlElement
    public int getYearOfPublication() { return yearOfPublication; }
    public void setYearOfPublication(int yearOfPublication) {this.yearOfPublication = yearOfPublication; } 
    @XmlElement
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) {this.isbn = isbn; } 
    @XmlElement
    public boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\u001B[33m");
        sb.append("Title: " + title + "\n");
        sb.append("Author: " + author + "\n");
        sb.append("Year of publication: " + yearOfPublication + "\n");
        sb.append("ISBN: " + isbn + "\n");
        sb.append("Available: " + isAvailable + "\n");
        sb.append("\u001B[0m");
        return sb.toString();
    }
}