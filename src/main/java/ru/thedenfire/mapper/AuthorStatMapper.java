package ru.thedenfire.mapper;

import ru.thedenfire.domain.value.AuthorStat;
import ru.thedenfire.dto.response.AuthorStatResponse;

public class AuthorStatMapper {
    public AuthorStatResponse toResponse(AuthorStat authorStat) {
        return new AuthorStatResponse(authorStat.getAuthor(), authorStat.getCount());
    }
}
