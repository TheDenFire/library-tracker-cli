package ru.thedenfire.mapper;

import org.junit.jupiter.api.Test;
import ru.thedenfire.domain.value.AuthorStat;
import ru.thedenfire.domain.Book;
import ru.thedenfire.domain.value.LibraryStats;
import ru.thedenfire.dto.response.AuthorStatResponse;
import ru.thedenfire.dto.response.BookResponse;
import ru.thedenfire.dto.response.LibraryStatsResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapperTest {

    @Test
    void shouldMapBookToResponse() {
        Book book = new Book("gta", "rc", 2008);
        BookResponse response = new BookMapper().toResponse(book);

        assertEquals(book.getId(), response.id());
        assertEquals("gta", response.title());
        assertEquals("rc", response.author());
        assertEquals(2008, response.year());
    }

    @Test
    void shouldMapAuthorStatToResponse() {
        AuthorStatResponse response = new AuthorStatMapper().toResponse(new AuthorStat("rc", 2));

        assertEquals("rc", response.author());
        assertEquals(2, response.count());
    }

    @Test
    void shouldMapLibraryStatsToResponse() {
        Book oldestBook = new Book("rdr", "rc", 1999);
        Book newestBook = new Book("cs", "vav", 2008);
        LibraryStats stats = new LibraryStats(
                2,
                oldestBook,
                newestBook,
                List.of(new AuthorStat("vav", 1))
        );
        BookMapper bookMapper = new BookMapper();
        LibraryStatsMapper mapper = new LibraryStatsMapper(bookMapper, new AuthorStatMapper());

        LibraryStatsResponse response = mapper.toResponse(stats);

        assertEquals(2, response.totalBooks());
        assertEquals(oldestBook.getId(), response.oldestBook().id());
        assertEquals(newestBook.getId(), response.newestBook().id());
        assertEquals("vav", response.topAuthors().getFirst().author());
        assertEquals(1, response.topAuthors().getFirst().count());
    }

    @Test
    void shouldMapMissingBooksToNullInStatsResponse() {
        LibraryStats stats = new LibraryStats(0, null, null, List.of());
        LibraryStatsMapper mapper = new LibraryStatsMapper(new BookMapper(), new AuthorStatMapper());

        LibraryStatsResponse response = mapper.toResponse(stats);

        assertNull(response.oldestBook());
        assertNull(response.newestBook());
        assertEquals(List.of(), response.topAuthors());
    }
}
