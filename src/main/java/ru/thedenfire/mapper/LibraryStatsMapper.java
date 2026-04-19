package ru.thedenfire.mapper;

import lombok.AllArgsConstructor;
import ru.thedenfire.domain.value.LibraryStats;
import ru.thedenfire.dto.response.LibraryStatsResponse;

@AllArgsConstructor
public class LibraryStatsMapper {
    private final BookMapper bookMapper;
    private final AuthorStatMapper authorStatMapper;

    public LibraryStatsResponse toResponse(LibraryStats stats) {
        return new LibraryStatsResponse(
                stats.getTotalBooks(),
                stats.getOldestBook() == null ? null : bookMapper.toResponse(stats.getOldestBook()),
                stats.getNewestBook() == null ? null : bookMapper.toResponse(stats.getNewestBook()),
                stats.getTopAuthors().stream().map(authorStatMapper::toResponse).toList()
        );
    }
}
