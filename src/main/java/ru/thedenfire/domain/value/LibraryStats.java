package ru.thedenfire.domain.value;

import lombok.Value;
import ru.thedenfire.domain.Book;

import java.util.List;
import java.util.Optional;

@Value
public class LibraryStats {
    int totalBooks;
    Optional<Book> oldestBook;
    Optional<Book> newestBook;
    List<AuthorStat> topAuthors;
}
