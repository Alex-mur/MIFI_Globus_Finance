package it.globus.finance.service;

import com.lowagie.text.DocumentException;
import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.ReportCreateRequest;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ReportService {

    private final TransactionRepo transactionRepo;
    private final UserService userService;
    private final ReportStorageService reportStorageService;
    private final ReportHTMLGeneratorService reportHTMLGeneratorService;

    public ReportService(TransactionRepo transactionRepo, UserService userService, ReportStorageService reportStorageService, ReportHTMLGeneratorService reportHTMLGeneratorService) {
        this.transactionRepo = transactionRepo;
        this.userService = userService;
        this.reportStorageService = reportStorageService;
        this.reportHTMLGeneratorService = reportHTMLGeneratorService;
    }

    private <T> void applyIfPresent(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    public String generateReport(ReportCreateRequest request) throws DocumentException, IOException {
        byte[] pdfBytes = getReportInBytes(request);
        return reportStorageService.saveReport(pdfBytes);
    }

    public byte[] getReport(String fileName) throws IOException, IllegalAccessError {
        return reportStorageService.loadReport(fileName);
    }

    private byte[] getReportInBytes(ReportCreateRequest request) throws DocumentException, IOException {
        List<Transaction> transactions = transactionRepo.findByUser_Id(userService.getCurrentUser().getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        List<Transaction> filteredTransactions = new ArrayList<>(transactions);
        LocalDateTime dateTimeFrom;
        if (request.getReportStartDate() != null) {
            dateTimeFrom = LocalDateTime.parse(request.getReportStartDate(), formatter);
            filteredTransactions = filteredTransactions.stream().filter(transaction -> {
                LocalDateTime date = transaction.getTransactionDate();
                return date.equals(dateTimeFrom) || date.isAfter(dateTimeFrom);
            }).toList();
        } else {
            dateTimeFrom = null;
        }

        LocalDateTime dateTimeTo;
        if (request.getReportEndDate() != null) {
            dateTimeTo = LocalDateTime.parse(request.getReportEndDate(), formatter);
            filteredTransactions = filteredTransactions.stream().filter(transaction -> {
                LocalDateTime date = transaction.getTransactionDate();
                return date.equals(dateTimeTo) || date.isBefore(dateTimeTo);
            }).toList();
        } else {
            dateTimeTo = null;
        }

        String htmlContent = reportHTMLGeneratorService.generateHtmlReport(filteredTransactions, "Отчет по операциям", dateTimeFrom, dateTimeTo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        String fontPath = getClass().getClassLoader().getResource("fonts/roboto.ttf").toString();
        renderer.getFontResolver().addFont(fontPath, "Identity-H", true);

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(out);
        renderer.finishPDF();

        return out.toByteArray();
    }
}
