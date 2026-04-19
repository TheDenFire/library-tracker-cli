package ru.thedenfire.presentation;

import org.junit.jupiter.api.Test;
import ru.thedenfire.exception.CommandFormatException;
import ru.thedenfire.mapper.AuthorStatMapper;
import ru.thedenfire.mapper.BookMapper;
import ru.thedenfire.mapper.LibraryStatsMapper;
import ru.thedenfire.repository.InMemoryBookRepository;
import ru.thedenfire.service.LibraryService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandProcessorTest {

    @Test
    void shouldRecognizeExitCommandIgnoringCaseAndSpaces() {
        CommandProcessor commandProcessor = createCommandProcessor();

        assertTrue(commandProcessor.isExitCommand("  exit  "));
        assertFalse(commandProcessor.isExitCommand("list"));
    }

    @Test
    void shouldAddAndListBooks() {
        CommandProcessor commandProcessor = createCommandProcessor();

        String addResult = commandProcessor.process("ADD gta;rc;2008");
        String listResult = commandProcessor.process("LIST");

        assertTrue(addResult.contains("Книга добавлена:"));
        assertTrue(addResult.contains("gta / rc / 2008"));
        assertTrue(listResult.contains("Список книг:"));
        assertTrue(listResult.contains("gta / rc / 2008"));
    }

    @Test
    void shouldFindBookByTitleAndAuthorQuery() {
        CommandProcessor commandProcessor = createCommandProcessor();
        commandProcessor.process("ADD gta;rc;2008");
        commandProcessor.process("ADD ca;vav;1999");

        String result = commandProcessor.process("FIND gta rc");

        assertTrue(result.contains("Результаты поиска:"));
        assertTrue(result.contains("gta / rc / 2008"));
        assertFalse(result.contains("ca / vav / 1999"));
    }

    @Test
    void shouldPrintStatsForBooks() {
        CommandProcessor commandProcessor = createCommandProcessor();
        commandProcessor.process("ADD gta;vav;2008");

        String result = commandProcessor.process("STATS");

        assertTrue(result.contains("Статистика:"));
        assertTrue(result.contains("Всего книг: 1"));
        assertTrue(result.contains("Топ-3 авторов:"));
        assertTrue(result.contains("1. vav — 1"));
    }

    @Test
    void shouldRejectInvalidCommands() {
        CommandProcessor commandProcessor = createCommandProcessor();

        assertThrows(CommandFormatException.class, () -> commandProcessor.process(""));
        assertThrows(CommandFormatException.class, () -> commandProcessor.process("ADD gta;vav;zero"));
        assertThrows(CommandFormatException.class, () -> commandProcessor.process("LIST pages"));
        assertThrows(CommandFormatException.class, () -> commandProcessor.process("FIND"));
        assertThrows(CommandFormatException.class, () -> commandProcessor.process("UNKNOWN"));
    }

    private CommandProcessor createCommandProcessor() {
        BookMapper bookMapper = new BookMapper();
        LibraryService libraryService = new LibraryService(
                new InMemoryBookRepository(),
                bookMapper,
                new LibraryStatsMapper(bookMapper, new AuthorStatMapper())
        );
        return new CommandProcessor(libraryService);
    }
}
