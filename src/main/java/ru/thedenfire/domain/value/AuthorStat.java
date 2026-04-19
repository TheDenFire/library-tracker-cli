package ru.thedenfire.domain.value;

import lombok.Value;

@Value
public class AuthorStat {
    String author;
    long count;
}
