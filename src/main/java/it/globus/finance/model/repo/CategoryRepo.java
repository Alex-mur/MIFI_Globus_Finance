package it.globus.finance.model.repo;

import it.globus.finance.model.entity.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepo extends CrudRepository<Category, Long> {
}
