package it.globus.finance.component;


import it.globus.finance.model.CategoryType;
import it.globus.finance.model.Role;
import it.globus.finance.model.entity.Category;
import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.entity.User;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.model.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SampleDataInitializer {

    private final UserRepo userRepository;
    private final CategoryRepo categoryRepository;
    private final TransactionRepo transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SampleDataInitializer(UserRepo userRepository, CategoryRepo categoryRepository, TransactionRepo transactionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
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
    }

    private User createUser(String username, String email, Role role) {
        return new User(username, passwordEncoder.encode("123"), role, email, true, LocalDateTime.now());
    }

    private Category createCategory(String name, CategoryType type, String description) {
        return new Category(name, type, description);
    }

    private void createTransactions(List<User> users, List<Category> categories) {
        List<Transaction> transactions = new ArrayList<>();

        String[] senderBanks = {"Сбербанк", "Тинькофф", "ВТБ", "Альфа-Банк"};
        String[] receiverBanks = {"Банк Открытие", "Газпромбанк", "Райффайзен"};
        String[] statuses = {"Завершено", "В обработке", "Отменено"};
        String[] transactionTypes = {"INCOME", "EXPENSE"};

        for (int i = 0; i < 30; i++) {
            Transaction t = new Transaction();
            t.setUser(users.get(i % users.size()));
            t.setTransactionDate(LocalDateTime.now().minusDays(i));

            t.setTransactionType(transactionTypes[i % transactionTypes.length]);
            t.setAmount(BigDecimal.valueOf(500 + i * 100));
            t.setStatus(statuses[i % statuses.length]);
            t.setComment("Тестовая транзакция №" + (i + 1));

            t.setSenderBank(senderBanks[i % senderBanks.length]);
            t.setSenderAccount("40817810" + String.format("%07d", i));

            t.setReceiverBank(receiverBanks[i % receiverBanks.length]);
            t.setReceiverInn(String.format("%011d", 77000000000L + i));
            t.setReceiverAccount("40702810" + String.format("%07d", i));

            t.setReceiverPhone("+7" + String.format("%010d", 9000000000L + i));
            t.setReceiverType(i % 2 == 0 ? "Физ.лицо" : "Юр.лицо");

            t.setCategory(categories.get(i % categories.size()));

            transactions.add(t);
        }

        transactionRepository.saveAll(transactions);
    }
}
