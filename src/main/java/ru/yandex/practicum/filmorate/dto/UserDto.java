package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserDto(long id,
                      @NotBlank(message = "Электронная почта не может быть пустой")
                      @Email(message = "Электронная почта должна содержать @") String email,
                      @NotBlank(message = "Логин не может быть пустым")
                      @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы") String login,
                      String name,
                      @NotNull(message = "Дата рождения не может быть пустой")
                      @PastOrPresent(message = "Дата рождения не может быть в будущем") LocalDate birthday) {
}
