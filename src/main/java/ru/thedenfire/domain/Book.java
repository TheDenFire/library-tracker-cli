package ru.thedenfire.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Locale;
import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {
    @EqualsAndHashCode.Include
    private final UUID id;
    private final String title;
    private final String author;
    private final int year;

    public Book(String title, String author, int year) {
        this.id = UUID.randomUUID();
        this.title = title.trim();
        this.author = author.trim();
        this.year = year;
    }

    public boolean hasSameIdentityAs(Book other) {
        return normalize(title).equals(normalize(other.title))
                && normalize(author).equals(normalize(other.author));
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
