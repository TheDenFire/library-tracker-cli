package ru.thedenfire.repository;

import ru.thedenfire.domain.Book;
import ru.thedenfire.exception.BookNotFoundException;
import ru.thedenfire.exception.DuplicateBookException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryBookRepository implements BookRepository {
    private final List<Book> books = new ArrayList<>();

    @Override
    public Book save(Book book) {
        if (isDuplicate(book)) {
            throw new DuplicateBookException("Книга с таким названием и автором уже существует.");
        }

        books.add(book);
        return book;
    }

    @Override
    public Book removeById(UUID id) {
        Book book = books.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Книга с id=" + id + " не найдена."));
        books.remove(book);
        return book;
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    @Override
    public Optional<Book> findOldest() {
        return books.stream()
                .min(Comparator.comparingInt(Book::getYear).thenComparing(Book::getId));
    }

    @Override
    public Optional<Book> findNewest() {
        return books.stream()
                .max(Comparator.comparingInt(Book::getYear).thenComparing(Book::getId));
    }

    private boolean isDuplicate(Book book) {
        return books.stream()
                .anyMatch(existingBook -> existingBook.hasSameIdentityAs(book));
    }
}
