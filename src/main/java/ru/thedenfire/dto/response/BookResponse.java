package ru.thedenfire.dto.response;

import java.util.UUID;

public record BookResponse(UUID id, String title, String author, int year) {
}
