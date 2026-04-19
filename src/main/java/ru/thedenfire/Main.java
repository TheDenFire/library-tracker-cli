package ru.thedenfire;

import ru.thedenfire.presentation.CommandProcessor;
import ru.thedenfire.exception.LibraryException;
import ru.thedenfire.mapper.AuthorStatMapper;
import ru.thedenfire.mapper.BookMapper;
import ru.thedenfire.mapper.LibraryStatsMapper;
import ru.thedenfire.repository.BookRepository;
import ru.thedenfire.repository.InMemoryBookRepository;
import ru.thedenfire.service.LibraryService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BookRepository bookRepository = new InMemoryBookRepository();
        BookMapper bookMapper = new BookMapper();
        LibraryStatsMapper libraryStatsMapper = new LibraryStatsMapper(bookMapper, new AuthorStatMapper());
        LibraryService libraryService = new LibraryService(bookRepository, bookMapper, libraryStatsMapper);
        CommandProcessor commandProcessor = new CommandProcessor(libraryService);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Библиотека готова к работе. Введите команду или EXIT для выхода.");

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }

            if (commandProcessor.isExitCommand(line)) {
                System.out.println("Работа завершена.");
                break;
            }

            try {
                System.out.println(commandProcessor.process(line));
            } catch (LibraryException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            }
        }
    }
}
