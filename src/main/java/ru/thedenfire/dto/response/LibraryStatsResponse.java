package ru.thedenfire.dto.response;

import java.util.List;

public record LibraryStatsResponse(
        int totalBooks,
        BookResponse oldestBook,
        BookResponse newestBook,
        List<AuthorStatResponse> topAuthors
) {
}
