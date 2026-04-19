package ru.thedenfire.repository;

import org.junit.jupiter.api.Test;
import ru.thedenfire.domain.Book;
import ru.thedenfire.exception.BookNotFoundException;
import ru.thedenfire.exception.DuplicateBookException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryBookRepositoryTest {

    @Test
    void shouldSaveAndReturnBooksInInsertionOrder() {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        Book firstBook = new Book("gta", "rc", 2008);
        Book secondBook = new Book("cs", "vav", 1999);

        repository.save(firstBook);
        repository.save(secondBook);

        assertEquals(List.of(firstBook, secondBook), repository.findAll());
    }

    @Test
    void shouldReturnCopyOfBooksList() {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        Book book = new Book("gta", "rc", 2008);
        repository.save(book);

        repository.findAll().clear();

        assertEquals(List.of(book), repository.findAll());
    }

    @Test
    void shouldRejectDuplicateBookByTitleAndAuthorIgnoringCase() {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        repository.save(new Book("GTA", "VAV", 2008));

        assertThrows(DuplicateBookException.class,
                () -> repository.save(new Book(" gta ", " vav ", 2020)));
    }

    @Test
    void shouldRemoveBookById() {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        Book book = new Book("gta", "vav", 2008);
        repository.save(book);

        Book removedBook = repository.removeById(book.getId());

        assertEquals(book, removedBook);
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldThrowWhenRemovingUnknownBook() {
        InMemoryBookRepository repository = new InMemoryBookRepository();

        assertThrows(BookNotFoundException.class, () -> repository.removeById(UUID.randomUUID()));
    }

    @Test
    void shouldFindOldestAndNewestBooks() {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        Book oldestBook = new Book("gta", "vav", 1999);
        Book newestBook = new Book("cs", "rc", 2008);

        repository.save(newestBook);
        repository.save(oldestBook);

        assertEquals(oldestBook, repository.findOldest().orElseThrow());
        assertEquals(newestBook, repository.findNewest().orElseThrow());
    }
}
