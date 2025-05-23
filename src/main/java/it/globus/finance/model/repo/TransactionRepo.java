package it.globus.finance.model.repo;

import it.globus.finance.model.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepo extends CrudRepository<Transaction, Long> {
    List<Transaction> findByUser_Id(Long userId);
}
