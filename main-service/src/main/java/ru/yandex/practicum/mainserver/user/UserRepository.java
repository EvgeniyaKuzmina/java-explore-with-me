package ru.yandex.practicum.mainserver.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.user.model.User;

import java.util.List;

/**
 * класс репозиторий для работы с БД пользователей
 */

public interface UserRepository extends JpaRepository<User, Long> {

    // получение списка пользователей с пагинацией
    List<User> findAllUsers(Pageable pageable);

    // получение списка пользователей по Id с пагинацией
    List<User> findAllByIdNotOrderByIdAsc(Long userId, Pageable pageable);
}
