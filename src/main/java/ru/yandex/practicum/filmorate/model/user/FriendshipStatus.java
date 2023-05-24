package ru.yandex.practicum.filmorate.model.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FriendshipStatus {

    private int id;

    private Boolean status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipStatus that = (FriendshipStatus) o;
        return id == that.id;
    }

}
