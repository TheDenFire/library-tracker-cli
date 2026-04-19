package ru.thedenfire.domain.value;

import lombok.Value;
import ru.thedenfire.domain.Book;

import java.util.List;

@Value
public class LibraryStats {
    int totalBooks;
    Book oldestBook;
    Book newestBook;
    List<AuthorStat> topAuthors;
}
