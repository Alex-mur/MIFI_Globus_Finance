package it.globus.finance.service;

import it.globus.finance.model.entity.Transaction;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportHTMLGeneratorService {

    private final TemplateEngine templateEngine;

    ReportHTMLGeneratorService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generateHtmlReport(List<Transaction> transactions,
                                     String title,
                                     LocalDate fromDate,
                                     LocalDate toDate) {

        Context context = new Context();

        context.setVariable("title", title);
        context.setVariable("transactions", transactions);

        String fromDateString = "...";
        if (fromDate != null) {
            fromDateString = fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        context.setVariable("fromDate", fromDateString);

        String toDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        if (toDate != null) {
            toDateString = toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        context.setVariable("toDate", toDateString);
        context.setVariable("now", LocalDateTime.now());

        BigDecimal totalIncome = calculateTotal(transactions, "INCOME");
        BigDecimal totalExpense = calculateTotal(transactions, "EXPENSE");
        BigDecimal total = totalIncome.subtract(totalExpense);
        context.setVariable("totalIncome", totalIncome);
        context.setVariable("totalExpense", totalExpense);
        context.setVariable("total", total);

        return templateEngine.process("transactions-report", context);
    }

    private BigDecimal calculateTotal(List<Transaction> transactions, String type) {
        return transactions.stream()
                .filter(t -> t.getTransactionType().equalsIgnoreCase(type))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}