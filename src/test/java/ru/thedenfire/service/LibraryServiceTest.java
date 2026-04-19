package ru.thedenfire.service;

import org.junit.jupiter.api.Test;
import ru.thedenfire.dto.request.CharacterBook;
import ru.thedenfire.dto.request.CreateBookRequest;
import ru.thedenfire.dto.request.FindBooksRequest;
import ru.thedenfire.dto.request.ListBooksRequest;
import ru.thedenfire.dto.response.BookResponse;
import ru.thedenfire.dto.response.LibraryStatsResponse;
import ru.thedenfire.exception.DuplicateBookException;
import ru.thedenfire.mapper.AuthorStatMapper;
import ru.thedenfire.mapper.BookMapper;
import ru.thedenfire.mapper.LibraryStatsMapper;
import ru.thedenfire.repository.InMemoryBookRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LibraryServiceTest {

    @Test
    void shouldRejectDuplicateBookByTitleAndAuthorIgnoringCase() {
        LibraryService libraryService = createService();
        libraryService.addBook(new CreateBookRequest("Metro", "Dmitriy", 1965));

        assertThrows(DuplicateBookException.class,
                () -> libraryService.addBook(new CreateBookRequest("  metro ", " dmitriy ", 1965)));
    }

    @Test
    void shouldSortBooksByYearAscending() {
        LibraryService libraryService = createService();
        libraryService.addBook(new CreateBookRequest("Metro 2033", "Dima", 2008));
        libraryService.addBook(new CreateBookRequest("Stalker", "Oleg", 1999));
        libraryService.addBook(new CreateBookRequest("OOP", "Ivan", 2003));

        List<BookResponse> books = libraryService.listBooks(new ListBooksRequest(CharacterBook.YEAR));

        assertEquals(List.of("Stalker", "OOP", "Metro 2033"),
                books.stream().map(BookResponse::title).toList());
    }

    @Test
    void shouldBuildStatsWithOldestNewestAndTopAuthors() {
        LibraryService libraryService = createService();
        libraryService.addBook(new CreateBookRequest("Book A", "Author 1", 2005));
        libraryService.addBook(new CreateBookRequest("Book B", "Author 2", 1999));
        libraryService.addBook(new CreateBookRequest("Book C", "Author 1", 2010));
        libraryService.addBook(new CreateBookRequest("Book D", "Author 3", 2001));

        LibraryStatsResponse stats = libraryService.getStats();

        assertEquals(4, stats.totalBooks());
        assertEquals("Book B", stats.oldestBook().title());
        assertEquals("Book C", stats.newestBook().title());
        assertEquals("Author 1", stats.topAuthors().getFirst().author());
        assertEquals(2L, stats.topAuthors().getFirst().count());
    }

    @Test
    void shouldGroupAuthorStatsIgnoringCase() {
        LibraryService libraryService = createService();
        libraryService.addBook(new CreateBookRequest("Book A", "Author 1", 2005));
        libraryService.addBook(new CreateBookRequest("Book B", "author 1", 2010));

        LibraryStatsResponse stats = libraryService.getStats();

        assertEquals("Author 1", stats.topAuthors().getFirst().author());
        assertEquals(2L, stats.topAuthors().getFirst().count());
    }

    @Test
    void shouldFindBooksByTitleAndAuthorTokensIgnoringCase() {
        LibraryService libraryService = createService();
        libraryService.addBook(new CreateBookRequest("Metro 2033", "Dmitry", 2008));
        libraryService.addBook(new CreateBookRequest("Metro 2033", "Ivan", 2009));
        libraryService.addBook(new CreateBookRequest("Metro", "Oleg", 1999));

        List<BookResponse> books = libraryService.findBooks(new FindBooksRequest("metro dmitry"));

        assertEquals(List.of("Metro 2033"), books.stream().map(BookResponse::title).toList());
        assertEquals(List.of("Dmitry"), books.stream().map(BookResponse::author).toList());
    }

    private LibraryService createService() {
        BookMapper bookMapper = new BookMapper();
        return new LibraryService(
                new InMemoryBookRepository(),
                bookMapper,
                new LibraryStatsMapper(bookMapper, new AuthorStatMapper())
        );
    }
}
