package ru.thedenfire.presentation;

import lombok.AllArgsConstructor;
import ru.thedenfire.dto.request.CharacterBook;
import ru.thedenfire.dto.request.CreateBookRequest;
import ru.thedenfire.dto.request.FindBooksRequest;
import ru.thedenfire.dto.request.ListBooksRequest;
import ru.thedenfire.dto.request.RemoveBookRequest;
import ru.thedenfire.dto.response.AuthorStatResponse;
import ru.thedenfire.dto.response.BookResponse;
import ru.thedenfire.dto.response.LibraryStatsResponse;
import ru.thedenfire.exception.CommandFormatException;
import ru.thedenfire.service.LibraryService;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CommandProcessor {
    private final LibraryService libraryService;

    public boolean isExitCommand(String input) {
        return input != null && "EXIT".equalsIgnoreCase(input.trim());
    }

    public String process(String input) {
        if (input == null || input.isBlank()) {
            throw new CommandFormatException("Команда не должна быть пустой.");
        }

        String trimmedInput = input.trim();
        int firstSpace = trimmedInput.indexOf(' ');
        String command = (firstSpace >= 0 ? trimmedInput.substring(0, firstSpace) : trimmedInput)
                .toUpperCase(Locale.ROOT);
        String arguments = firstSpace >= 0 ? trimmedInput.substring(firstSpace + 1).trim() : "";

        return switch (command) {
            case "ADD" -> handleAdd(arguments);
            case "REMOVE" -> handleRemove(arguments);
            case "LIST" -> handleList(arguments);
            case "FIND" -> handleFind(arguments);
            case "STATS" -> handleStats(arguments);
            case "EXIT" -> "Работа завершена.";
            default -> throw new CommandFormatException("Неизвестная команда: " + command + ".");
        };
    }

    private String handleAdd(String arguments) {
        String[] parts = arguments.split(";", -1);
        if (parts.length != 3) {
            throw new CommandFormatException("Формат команды: ADD <название>;<автор>;<год>.");
        }

        String title = parts[0].trim();
        String author = parts[1].trim();
        if (title.isBlank()) {
            throw new CommandFormatException("Название книги обязательно.");
        }
        if (author.isBlank()) {
            throw new CommandFormatException("Автор книги обязателен.");
        }

        int year;
        try {
            year = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException exception) {
            throw new CommandFormatException("Год должен быть целым числом.");
        }
        if (year <= 0) {
            throw new CommandFormatException("Год издания должен быть положительным числом.");
        }

        BookResponse book = libraryService.addBook(new CreateBookRequest(title, author, year));
        return "Книга добавлена: " + formatBook(book);
    }

    private String handleRemove(String arguments) {
        if (arguments.isBlank()) {
            throw new CommandFormatException("Формат команды: REMOVE <id>.");
        }

        UUID id;
        try {
            id = UUID.fromString(arguments);
        } catch (IllegalArgumentException exception) {
            throw new CommandFormatException("ID должен быть корректным UUID.");
        }

        BookResponse removedBook = libraryService.removeBook(new RemoveBookRequest(id));
        return "Книга удалена: " + formatBook(removedBook);
    }

    private String handleList(String arguments) {
        List<BookResponse> books = libraryService.listBooks(new ListBooksRequest(parseSort(arguments)));
        if (books.isEmpty()) {
            return "Библиотека пуста.";
        }

        return "Список книг:" + System.lineSeparator() + formatBooks(books);
    }

    private String handleFind(String arguments) {
        if (arguments.isBlank()) {
            throw new CommandFormatException("Формат команды: FIND <запрос>.");
        }

        List<BookResponse> books = libraryService.findBooks(new FindBooksRequest(arguments));
        if (books.isEmpty()) {
            return "По запросу ничего не найдено.";
        }

        return "Результаты поиска:" + System.lineSeparator() + formatBooks(books);
    }

    private String handleStats(String arguments) {
        if (!arguments.isBlank()) {
            throw new CommandFormatException("Формат команды: STATS.");
        }

        LibraryStatsResponse stats = libraryService.getStats();
        StringBuilder builder = new StringBuilder();
        builder.append("Статистика:").append(System.lineSeparator());
        builder.append("Всего книг: ").append(stats.totalBooks()).append(System.lineSeparator());
        builder.append("Самая старая книга: ")
                .append(formatNullableBook(stats.oldestBook()))
                .append(System.lineSeparator());
        builder.append("Самая новая книга: ")
                .append(formatNullableBook(stats.newestBook()))
                .append(System.lineSeparator());
        builder.append("Топ-3 авторов: ");

        if (stats.topAuthors().isEmpty()) {
            builder.append("нет данных");
            return builder.toString();
        }

        builder.append(System.lineSeparator());
        for (int index = 0; index < stats.topAuthors().size(); index++) {
            AuthorStatResponse authorStat = stats.topAuthors().get(index);
            builder.append(index + 1)
                    .append(". ")
                    .append(authorStat.author())
                    .append(" — ")
                    .append(authorStat.count());
            if (index < stats.topAuthors().size() - 1) {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    private String formatBooks(List<BookResponse> books) {
        return books.stream()
                .map(this::formatBook)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private CharacterBook parseSort(String arguments) {
        if (arguments.isBlank()) {
            return null;
        }

        return switch (arguments.toLowerCase(Locale.ROOT)) {
            case "title" -> CharacterBook.TITLE;
            case "author" -> CharacterBook.AUTHOR;
            case "year" -> CharacterBook.YEAR;
            default -> throw new CommandFormatException("Неизвестная сортировка: " + arguments + ". Доступно: title, author, year.");
        };
    }

    private String formatNullableBook(BookResponse book) {
        return book == null ? "нет данных" : formatBook(book);
    }

    private String formatBook(BookResponse book) {
        return "[" + book.id() + "] " + book.title() + " / " + book.author() + " / " + book.year();
    }
}
