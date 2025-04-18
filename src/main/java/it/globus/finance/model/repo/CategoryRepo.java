package it.globus.finance.model.repo;

import it.globus.finance.model.entity.Category;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface CategoryRepo extends CrudRepository<Category, BigInteger> {
}
