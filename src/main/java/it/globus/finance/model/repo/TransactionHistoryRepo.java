package it.globus.finance.model.repo;

import it.globus.finance.model.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryRepo extends JpaRepository<TransactionHistory, Long> {

    List<TransactionHistory> findAllByUserIdAndChangeDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<TransactionHistory> findAllByUserId(Long userId);
}
