import java.util.*;

abstract class Book {
    protected String isbn;
    protected String title;
    protected int publishYear;
    protected double price;
    protected String author;

    public Book(String isbn, String title, int publishYear, double price, String author) {
        this.isbn = isbn;
        this.title = title;
        this.publishYear = publishYear;
        this.price = price;
        this.author = author;
    }

    public String getIsbn() { return isbn; }
    public int getPublishYear() { return publishYear; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getAuthor() { return author; }

    public abstract boolean isPurchasable();
    public abstract void deliver(String email, String address);
}

interface Shippable {
    void ship(String address);
}

interface Emailable {
    void email(String email);
}

class PaperBook extends Book implements Shippable {
    private int stock;

    public PaperBook(String isbn, String title, int year, double price, String author, int stock) {
        super(isbn, title, year, price, author);
        this.stock = stock;
    }

    public int getStock() { return stock; }

    public void reduceStock(int qty) {
        if (stock < qty) throw new IllegalArgumentException("Not enough stock");
        stock -= qty;
    }

    @Override
    public boolean isPurchasable() {
        return true;
    }

    @Override
    public void ship(String address) {
        System.out.println("Quantum book store: Shipping PaperBook to " + address);
    }

    @Override
    public void deliver(String email, String address) {
        ship(address);
    }
}

class EBook extends Book implements Emailable {
    private String fileType;

    public EBook(String isbn, String title, int year, double price, String author, String fileType) {
        super(isbn, title, year, price, author);
        this.fileType = fileType;
    }

    @Override
    public boolean isPurchasable() {
        return true;
    }

    @Override
    public void email(String email) {
        System.out.println("Quantum book store: Emailing EBook to " + email);
    }

    @Override
    public void deliver(String email, String address) {
        email(email);
    }
}

class ShowcaseBook extends Book {
    public ShowcaseBook(String isbn, String title, int year, double price, String author) {
        super(isbn, title, year, price, author);
    }

    @Override
    public boolean isPurchasable() {
        return false;
    }

    @Override
    public void deliver(String email, String address) {
        throw new UnsupportedOperationException("Quantum book store: Cannot deliver Showcase Book");
    }
}

class Inventory {
    private Map<String, Book> books = new HashMap<>();

    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    public void removeOutdatedBooks(int yearsLimit) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        books.values().removeIf(book -> currentYear - book.getPublishYear() > yearsLimit);
    }

    public double buyBook(String isbn, int qty, String email, String address) {
        if (!books.containsKey(isbn)) throw new IllegalArgumentException("Book not found");
        Book book = books.get(isbn);

        if (!book.isPurchasable())
            throw new IllegalArgumentException("Quantum book store: Book is not for sale");

        if (book instanceof PaperBook) {
            ((PaperBook) book).reduceStock(qty);
        }

        double total = book.getPrice() * qty;

        for (int i = 0; i < qty; i++) {
            book.deliver(email, address);
        }

        return total;
    }

    public void listBooks() {
        for (Book book : books.values()) {
            System.out.println("Quantum book store: " + book.getTitle() + " by " + book.getAuthor());
        }
    }
}

public class QuantumBookstoreFullTest {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();

        Book paperBook = new PaperBook("123", "Java Basics", 2020, 150.0, "Reham Ayad", 5);
        Book eBook = new EBook("456", "Learn Angular", 2022, 100.0, "Ahmed Ali", "PDF");
        Book showcase = new ShowcaseBook("789", "Fawry Systems", 2019, 200.0, "Fawry Team");

        inventory.addBook(paperBook);
        inventory.addBook(eBook);
        inventory.addBook(showcase);

        inventory.listBooks();

        System.out.println("Paid: " + inventory.buyBook("123", 2, "reham@example.com", "Smart Village"));
        System.out.println("Paid: " + inventory.buyBook("456", 1, "reham@example.com", "Smart Village"));

        inventory.removeOutdatedBooks(4); // remove books older than 4 years
        inventory.listBooks();
    }
}  