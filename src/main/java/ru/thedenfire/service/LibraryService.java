package ru.thedenfire.service;

import lombok.AllArgsConstructor;
import ru.thedenfire.domain.value.AuthorStat;
import ru.thedenfire.domain.Book;
import ru.thedenfire.domain.value.LibraryStats;
import ru.thedenfire.dto.request.CreateBookRequest;
import ru.thedenfire.dto.request.FindBooksRequest;
import ru.thedenfire.dto.request.ListBooksRequest;
import ru.thedenfire.dto.request.RemoveBookRequest;
import ru.thedenfire.dto.response.BookResponse;
import ru.thedenfire.dto.response.LibraryStatsResponse;
import ru.thedenfire.mapper.BookMapper;
import ru.thedenfire.mapper.LibraryStatsMapper;
import ru.thedenfire.repository.BookRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@AllArgsConstructor
public class LibraryService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final LibraryStatsMapper libraryStatsMapper;

    public BookResponse addBook(CreateBookRequest request) {
        Book book = new Book(request.title(), request.author(), request.year());
        return bookMapper.toResponse(bookRepository.save(book));
    }

    public BookResponse removeBook(RemoveBookRequest request) {
        return bookMapper.toResponse(bookRepository.removeById(request.id()));
    }

    public List<BookResponse> listBooks(ListBooksRequest request) {
        List<Book> result = bookRepository.findAll();
        if (request.sortBy() == null) {
            return result.stream().map(bookMapper::toResponse).toList();
        }

        Comparator<Book> comparator = switch (request.sortBy()) {
            case TITLE -> Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Book::getId);
            case AUTHOR -> Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Book::getId);
            case YEAR -> Comparator.comparingInt(Book::getYear)
                    .thenComparing(Book::getId);
        };

        result.sort(comparator);
        return result.stream().map(bookMapper::toResponse).toList();
    }

    public List<BookResponse> findBooks(FindBooksRequest request) {
        List<String> queryTokens = List.of(normalize(request.query()).split("\\s+"));

        return bookRepository.findAll().stream()
                .filter(book -> {
                    String searchableText = normalize(book.getTitle() + " " + book.getAuthor());
                    return queryTokens.stream().allMatch(searchableText::contains);
                })
                .map(bookMapper::toResponse)
                .toList();
    }

    public LibraryStatsResponse getStats() {
        List<Book> books = bookRepository.findAll();

        List<AuthorStat> topAuthors = books.stream()
                .collect(Collectors.groupingBy(book -> normalize(book.getAuthor())))
                .values()
                .stream()
                .map(authorBooks -> new AuthorStat(authorBooks.getFirst().getAuthor(), authorBooks.size()))
                .sorted(Comparator.comparingLong(AuthorStat::getCount).reversed()
                        .thenComparing(AuthorStat::getAuthor, String.CASE_INSENSITIVE_ORDER))
                .limit(3)
                .toList();

        LibraryStats stats = new LibraryStats(
                books.size(),
                bookRepository.findOldest().orElse(null),
                bookRepository.findNewest().orElse(null),
                topAuthors
        );
        return libraryStatsMapper.toResponse(stats);
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
