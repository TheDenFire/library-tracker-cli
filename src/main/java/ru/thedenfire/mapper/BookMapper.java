package ru.thedenfire.mapper;

import ru.thedenfire.domain.Book;
import ru.thedenfire.dto.response.BookResponse;

public class BookMapper {
    public BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getYear());
    }
}
