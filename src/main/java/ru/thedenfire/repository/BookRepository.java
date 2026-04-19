package ru.thedenfire.repository;

import ru.thedenfire.domain.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
    Book save(Book book);

    Book removeById(UUID id);

    List<Book> findAll();

    Optional<Book> findOldest();

    Optional<Book> findNewest();
}
