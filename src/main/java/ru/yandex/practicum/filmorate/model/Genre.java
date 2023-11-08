package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Genre {

    @PositiveOrZero
    private Long id;

    @NotBlank(message = "name symbol > 0")
    @Length(max = 50, message = "size < 50")
    private String name;

}
//    Test list film response | AssertionError: List length must be 2: expected 3 to deeply equal 2
//        FAIL
//        Test film[0] 'id' field | AssertionError: "id" must be 1: expected 3 to deeply equal 1
//        FAIL
//        Test film[0] 'name' field | AssertionError: "name" must be "Film Updated": expected 'New film with director' to deeply equal 'Film Updated'
//        FAIL
//        Test film[0] 'description' field | AssertionError: "description" must be "New film update decription": expected 'Film with director' to deeply equal 'New film update decription'
//        FAIL
//        Test film[0] 'releaseDate' field | AssertionError: "releaseDate" field must be "1989-04-17": expected '1999-04-30' to deeply equal '1989-04-17'
//        FAIL
//        Test film[0] 'duration' field | AssertionError: "duration" field must be 190: expected 120 to deeply equal 190
//        FAIL
//        Test film[0] 'mpa' field | AssertionError: "mpa.id" field must be 5: expected 3 to deeply equal 5
//        FAIL
//        Test film[0] 'genres' field | AssertionError: "genres[0].id" field must be 2: expected 1 to deeply equal 2
//        FAIL
//        Test film[1] 'genres' field | AssertionError: "genres" field must be have 1 genre: expected 3 to deeply equal 1
