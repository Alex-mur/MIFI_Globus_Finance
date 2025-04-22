package it.globus.finance.model.repo;

import it.globus.finance.model.entity.Report;
import org.springframework.data.repository.CrudRepository;

public interface ReportRepo extends CrudRepository<Report, Long> {
}
