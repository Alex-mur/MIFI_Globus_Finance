package it.globus.finance.component;


import it.globus.finance.model.CategoryType;
import it.globus.finance.model.Role;
import it.globus.finance.model.entity.Category;
import it.globus.finance.model.entity.Report;
import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.entity.User;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.ReportRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.model.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SampleDataInitializer {

    private final UserRepo userRepository;
    private final CategoryRepo categoryRepository;
    private final TransactionRepo transactionRepository;
    private final ReportRepo reportRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SampleDataInitializer(UserRepo userRepository, CategoryRepo categoryRepository, TransactionRepo transactionRepository, ReportRepo reportRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initDemoData() {
        if (userRepository.count() > 0) {
            return; // Данные уже есть
        }

        // Создание пользователей
        User u1 = createUser("u1", "user1@example.com", Role.USER);
        User u2 = createUser("u2", "user2@example.com", Role.USER);
        User u3 = createUser("u3", "user3@example.com", Role.USER);

        var users = StreamSupport
                .stream(userRepository.saveAll(List.of(u1, u2, u3)).spliterator(), false)
                .toList();

        // Создание категорий
        Category food = createCategory("Еда", CategoryType.EXPENSE, "Покупка продуктов");
        Category transport = createCategory("Транспорт", CategoryType.EXPENSE, "Транспортные расходы");
        Category salary = createCategory("Зарплата", CategoryType.INCOME, "Заработная плата");

        var categories = StreamSupport
                .stream(categoryRepository.saveAll(List.of(food, transport, salary)).spliterator(), false)
                .toList();

        // Создание транзакций
        createTransactions(users, categories);

        // Создание отчетов
        createReports(users);
    }

    private User createUser(String username, String email, Role role) {
        return new User(username, passwordEncoder.encode("123"), role, email, true, LocalDateTime.now());
    }

    private Category createCategory(String name, CategoryType type, String description) {
        return new Category(name, type, description);
    }

    private void createTransactions(List<User> users, List<Category> categories) {
        // Transaction 1
        Transaction t1 = new Transaction();
        t1.setUser(users.get(0));
        t1.setTransactionDate(LocalDateTime.now().minusDays(5));
        t1.setTransactionType("test");
        t1.setAmount(new BigDecimal("2500.50"));
        t1.setStatus("Завершено");
        t1.setComment("Оплата ресторана");
        t1.setCategory(categories.get(0));
        t1.setSenderBank("Сбербанк");
        t1.setSenderAccount("408178101111111");
        t1.setReceiverInn("7736207543");
        t1.setReceiverAccount("407028105012345");

        // Transaction 2
        Transaction t2 = new Transaction();
        t2.setUser(users.get(1));
        t2.setTransactionDate(LocalDateTime.now().minusDays(5));
        t2.setTransactionType("test");
        t2.setAmount(new BigDecimal("2500.50"));
        t2.setStatus("Завершено");
        t2.setComment("Оплата`");
        t2.setCategory(categories.get(1));
        t2.setSenderBank("Сбербанк");
        t2.setSenderAccount("408178101111111");
        t2.setReceiverInn("7736207543");
        t2.setReceiverAccount("40702810500000345");

        // Transaction 3 (между пользователями)
        Transaction t3 = new Transaction();
        t3.setUser(users.get(2));
        t3.setTransactionDate(LocalDateTime.now().minusDays(2));
        t3.setTransactionType("test");
        t3.setAmount(new BigDecimal("7500.00"));
        t3.setStatus("В обработке");
        t3.setCategory(categories.get(1));
        t3.setSenderAccount("40817810222222222");
        t3.setReceiverAccount("40817810333333333");

        // Transaction 5 (долгая транзакция)
        Transaction t5 = new Transaction();
        t5.setUser(users.get(1));
        t5.setTransactionDate(LocalDateTime.of(2023, 10, 1, 9, 30));
        t5.setTransactionType("test");
        t5.setAmount(new BigDecimal("5432.10"));
        t5.setStatus("Отменено");
        t5.setComment("Ошибочный платеж");
        t5.setSenderBank("Тинькофф");
        t5.setSenderAccount("4081781044444444");

        transactionRepository.saveAll(List.of(t1, t2, t3, t5));
    }

    private void createReports(List<User> users) {
        Report r1 = new Report(
                users.get(0),
                "{\"type\":\"monthly\", \"period\":\"2023-10\", \"categories\":[\"food\"]}",
                LocalDateTime.now(),
                "\\sdffe\\sdfweffew\\file1"
        );

        Report r2 = new Report(
                users.get(1),
                "{\"type\":\"custom\", \"dateRange\":{\"from\":\"2023-10-01\", \"to\":\"2023-10-31\"}}",
                LocalDateTime.now(),
                "\\sd32443tffe\\sdfweffew\\file2"
        );

        Report r3 = new Report(
                users.get(2),
                "{\"type\":\"annual\", \"year\":2023, \"format\":\"excel\"}",
                LocalDateTime.now(),
                "\\sdfuyi67u56fe\\sdfwe456y6yyffew\\file3"
        );

        reportRepository.saveAll(List.of(r1, r2, r3));
    }
}
