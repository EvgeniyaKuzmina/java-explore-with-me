package ru.yandex.practicum.mainservice.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainservice.user.model.User;

import java.util.Collection;

/**
 * класс репозиторий для работы с БД пользователей
 */

public interface UserRepository extends JpaRepository<User, Long> {

    // получение списка пользователей с пагинацией
    Page<User> findByIdIn(Collection<Long> ids, Pageable pageable);


}
