package it.globus.finance.model.repo;

import it.globus.finance.model.entity.Report;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface ReportRepo extends CrudRepository<Report, BigInteger> {
}
