package ru.liger.telegram.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.liger.telegram.bot.entity.TaskEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByUserIdAndDataAfterOrderByDataAsc(Long userId, Date data);
    List<TaskEntity> findByUserIdAndDataOrderByDataAsc(Long userId, Date data);
}
