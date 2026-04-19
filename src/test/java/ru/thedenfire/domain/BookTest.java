package ru.thedenfire.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookTest {

    @Test
    void shouldTrimTitleAndAuthorOnCreate() {
        Book book = new Book("gta", "rc", 2008);

        assertEquals("gta", book.getTitle());
        assertEquals("rc", book.getAuthor());
        assertEquals(2008, book.getYear());
    }

    @Test
    void shouldCompareBusinessIdentityByTitleAndAuthorIgnoringCaseAndSpaces() {
        Book book = new Book("GTA", "RC", 2008);
        Book sameBook = new Book(" gta", " rc ", 2020);

        assertTrue(book.hasSameIdentityAs(sameBook));
    }

    @Test
    void shouldUseIdForEntityEquality() {
        Book book = new Book("GTA", "RC", 2008);
        Book sameBook = new Book(" gta", " rc ", 2008);

        assertNotEquals(book, sameBook);
    }
}
