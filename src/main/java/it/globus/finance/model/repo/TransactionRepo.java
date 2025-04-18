package it.globus.finance.model.repo;

import it.globus.finance.model.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface TransactionRepo extends CrudRepository<Transaction, BigInteger> {
}
