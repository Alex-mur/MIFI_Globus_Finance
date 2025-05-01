// Слушатель события загрузки DOM, инициализирует загрузку данных и привязывает обработчики событий
document.addEventListener('DOMContentLoaded', async () => {
    console.log('DOM загружен, инициализация дашбордов'); // Отладка: проверяем запуск
    // Загружаем аналитические данные без фильтров при открытии страницы
    await loadAnalyticsData();

    // Обработчик отправки формы фильтрации
    const filterForm = document.getElementById('filterForm');
    if (filterForm) {
        filterForm.addEventListener('submit', async (e) => {
            e.preventDefault(); // Предотвращаем стандартную отправку формы
            console.log('Форма фильтрации отправлена'); // Отладка
            await loadAnalyticsData(getFormData()); // Загружаем данные с фильтрами
        });
    } else {
        console.error('Элемент filterForm не найден'); // Отладка
    }

    // Обработчик кнопки сброса фильтров
    const resetButton = document.getElementById('resetFilters');
    if (resetButton) {
        resetButton.addEventListener('click', async () => {
            if (filterForm) filterForm.reset(); // Сбрасываем форму
            console.log('Фильтры сброшены'); // Отладка
            await loadAnalyticsData(); // Загружаем данные без фильтров
        });
    } else {
        console.error('Элемент resetFilters не найден'); // Отладка
    }

    // Обработчик изменения периода для графика поступлений
    const incomePeriodSelect = document.getElementById('incomePeriodSelect');
    if (incomePeriodSelect) {
        incomePeriodSelect.addEventListener('change', async (e) => {
            const incomePeriod = e.target.value; // Получаем период для поступлений
            const expensePeriod = document.getElementById('expensePeriodSelect')?.value || 'month'; // Текущий период для списаний
            const comparisonPeriod = document.getElementById('comparisonPeriodSelect')?.value || 'month'; // Текущий период для сравнения
            const statusPeriod = document.getElementById('statusPeriodSelect')?.value || 'month'; // Текущий период для статуса
            console.log(`Изменен период для поступлений: ${incomePeriod}`); // Отладка
            await loadAnalyticsData(getFormData(), incomePeriod, expensePeriod, comparisonPeriod, statusPeriod); // Загружаем данные
        });
    } else {
        console.error('Элемент incomePeriodSelect не найден'); // Отладка
    }

    // Обработчик изменения периода для графика списаний
    const expensePeriodSelect = document.getElementById('expensePeriodSelect');
    if (expensePeriodSelect) {
        expensePeriodSelect.addEventListener('change', async (e) => {
            const expensePeriod = e.target.value; // Получаем период для списаний
            const incomePeriod = document.getElementById('incomePeriodSelect')?.value || 'month'; // Текущий период для поступлений
            const comparisonPeriod = document.getElementById('comparisonPeriodSelect')?.value || 'month'; // Текущий период для сравнения
            const statusPeriod = document.getElementById('statusPeriodSelect')?.value || 'month'; // Текущий период для статуса
            console.log(`Изменен период для списаний: ${expensePeriod}`); // Отладка
            await loadAnalyticsData(getFormData(), incomePeriod, expensePeriod, comparisonPeriod, statusPeriod); // Загружаем данные
        });
    } else {
        console.error('Элемент expensePeriodSelect не найден'); // Отладка
    }

    // Обработчик изменения периода для графика сравнения
    const comparisonPeriodSelect = document.getElementById('comparisonPeriodSelect');
    if (comparisonPeriodSelect) {
        comparisonPeriodSelect.addEventListener('change', async (e) => {
            const comparisonPeriod = e.target.value; // Получаем период для сравнения
            const incomePeriod = document.getElementById('incomePeriodSelect')?.value || 'month'; // Текущий период для поступлений
            const expensePeriod = document.getElementById('expensePeriodSelect')?.value || 'month'; // Текущий период для списаний
            const statusPeriod = document.getElementById('statusPeriodSelect')?.value || 'month'; // Текущий период для статуса
            console.log(`Изменен период для сравнения: ${comparisonPeriod}`); // Отладка
            await loadAnalyticsData(getFormData(), incomePeriod, expensePeriod, comparisonPeriod, statusPeriod); // Загружаем данные
        });
    } else {
        console.error('Элемент comparisonPeriodSelect не найден'); // Отладка
    }

    // Обработчик изменения периода для графика статуса транзакций
    const statusPeriodSelect = document.getElementById('statusPeriodSelect');
    if (statusPeriodSelect) {
        statusPeriodSelect.addEventListener('change', async (e) => {
            const statusPeriod = e.target.value; // Получаем период для статуса
            const incomePeriod = document.getElementById('incomePeriodSelect')?.value || 'month'; // Текущий период для поступлений
            const expensePeriod = document.getElementById('expensePeriodSelect')?.value || 'month'; // Текущий период для списаний
            const comparisonPeriod = document.getElementById('comparisonPeriodSelect')?.value || 'month'; // Текущий период для сравнения
            console.log(`Изменен период для статуса: ${statusPeriod}`); // Отладка
            await loadAnalyticsData(getFormData(), incomePeriod, expensePeriod, comparisonPeriod, statusPeriod); // Загружаем данные
        });
    } else {
        console.error('Элемент statusPeriodSelect не найден'); // Отладка
    }
});

// Функция загрузки аналитических данных с сервера
async function loadAnalyticsData(filters = {}, incomePeriod = 'month', expensePeriod = 'month', comparisonPeriod = 'month', statusPeriod = 'month') {
    try {
        console.log('Загрузка данных с фильтрами:', filters); // Отладка
        const url = '/api/transactions/filter'; // URL для получения транзакций
        const options = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(filters) // Отправляем фильтры в теле запроса
        };

        const response = await fetch(url, options); // Выполняем запрос
        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`); // Проверяем статус ответа
        }
        const data = await response.json(); // Парсим JSON-ответ
        console.log('Получены данные:', data); // Отладка: проверяем данные

        renderCharts(data, incomePeriod, expensePeriod, comparisonPeriod, statusPeriod); // Рендерим все графики
    } catch (error) {
        console.error('Ошибка загрузки аналитики:', error); // Логируем ошибки
    }
}

// Функция извлечения данных из формы фильтрации
function getFormData() {
    const form = document.getElementById('filterForm'); // Получаем форму
    if (!form) {
        console.error('Форма filterForm не найдена'); // Отладка
        return {};
    }
    const formData = new FormData(form); // Собираем данные формы
    const filters = {};
    for (let [key, value] of formData.entries()) {
        if (value) filters[key] = value; // Добавляем только непустые значения
    }
    console.log('Извлечены фильтры:', filters); // Отладка
    return filters; // Возвращаем объект с фильтрами
}

// Функция обработки данных для графиков поступлений и списаний (по количеству транзакций)
function processTransactionData(transactions, period) {
    console.log(`Обработка данных для периода: ${period}`); // Отладка
    const countsByType = {
        'Поступление': {},
        'Списание': {}
    }; // Объект для хранения счетчиков
    const formatter = new Intl.DateTimeFormat('ru-RU', { month: 'long', year: 'numeric' }); // Форматтер для дат

    // Фильтруем и группируем транзакции только для "Поступление" и "Списание"
    transactions.forEach(transaction => {
        const type = transaction.transactionType;
        if (type !== 'Поступление' && type !== 'Списание') return; // Пропускаем неподходящие типы

        const dateStr = transaction.transactionDate;
        if (!dateStr) {
            console.warn('Транзакция без даты:', transaction); // Отладка
            return;
        }

        let date;
        try {
            date = new Date(dateStr); // Преобразуем дату транзакции
            if (isNaN(date.getTime())) {
                throw new Error('Некорректная дата');
            }
        } catch (error) {
            console.warn(`Ошибка парсинга даты в транзакции: ${dateStr}`, transaction); // Отладка
            return;
        }

        let key; // Ключ для периода
        // Определяем ключ в зависимости от выбранного периода
        switch (period) {
            case 'week':
                // Начало недели (понедельник)
                const weekStart = new Date(date);
                weekStart.setDate(date.getDate() - (date.getDay() || 7) + 1);
                key = weekStart.toLocaleDateString('ru-RU', { month: 'short', day: 'numeric', year: 'numeric' });
                break;
            case 'month':
                key = formatter.format(date); // Форматируем как "месяц год"
                break;
            case 'quarter':
                const quarter = Math.ceil((date.getMonth() + 1) / 3); // Определяем квартал
                key = `${quarter} квартал ${date.getFullYear()}`; // Формируем ключ
                break;
            case 'year':
                key = date.getFullYear().toString(); // Год как строка
                break;
            default:
                console.warn(`Неподдерживаемый период: ${period}`); // Отладка
                return;
        }

        countsByType[type][key] = (countsByType[type][key] || 0) + 1; // Увеличиваем счетчик
    });

    // Собираем все уникальные периоды
    const allPeriods = new Set();
    Object.values(countsByType).forEach(counts => {
        Object.keys(counts).forEach(period => allPeriods.add(period)); // Добавляем все периоды
    });

    // Сортируем периоды для корректного отображения
    const sortedPeriods = Array.from(allPeriods).sort((a, b) => {
        if (period === 'week') {
            return new Date(a) - new Date(b); // Сортировка по дате для недель
        }
        return a.localeCompare(b); // Лексикографическая сортировка
    });

    // Формируем данные для графика поступлений
    const incomeData = {
        labels: sortedPeriods, // Метки для графика
        datasets: [{
            label: 'Поступление',
            data: sortedPeriods.map(period => countsByType['Поступление'][period] || 0), // Данные
            borderColor: 'rgba(75, 192, 192, 1)', // Цвет линии
            backgroundColor: 'rgba(75, 192, 192, 0.2)', // Цвет заливки
            fill: true, // Включаем заливку
            tension: 0.4 // Плавность линии
        }]
    };

    // Формируем данные для графика списаний
    const expenseData = {
        labels: sortedPeriods, // Метки для графика
        datasets: [{
            label: 'Списание',
            data: sortedPeriods.map(period => countsByType['Списание'][period] || 0), // Данные
            borderColor: 'rgba(255, 99, 132, 1)', // Цвет линии
            backgroundColor: 'rgba(255, 99, 132, 0.2)', // Цвет заливки
            fill: true, // Включаем заливку
            tension: 0.4 // Плавность линии
        }]
    };

    console.log('Обработанные данные для количества:', { incomeData, expenseData }); // Отладка
    return { incomeData, expenseData }; // Возвращаем данные для графиков
}

// Функция обработки данных для графика сравнения сумм
function processComparisonData(transactions, period) {
    console.log(`Обработка данных для сравнения сумм, период: ${period}`); // Отладка
    const amountsByType = {
        'Поступление': {},
        'Списание': {}
    }; // Объект для хранения сумм
    const formatter = new Intl.DateTimeFormat('ru-RU', { month: 'long', year: 'numeric' }); // Форматтер для дат

    // Фильтруем и группируем транзакции по суммам для "Поступление" и "Списание"
    transactions.forEach(transaction => {
        const type = transaction.transactionType;
        if (type !== 'Поступление' && type !== 'Списание') return; // Пропускаем неподходящие типы

        const dateStr = transaction.transactionDate;
        const amount = transaction.amount;
        if (!dateStr || amount == null) {
            console.warn('Транзакция без даты или суммы:', transaction); // Отладка
            return;
        }

        let date;
        try {
            date = new Date(dateStr); // Преобразуем дату транзакции
            if (isNaN(date.getTime())) {
                throw new Error('Некорректная дата');
            }
        } catch (error) {
            console.warn(`Ошибка парсинга даты в транзакции: ${dateStr}`, transaction); // Отладка
            return;
        }

        let key; // Ключ для периода
        // Определяем ключ в зависимости от выбранного периода
        switch (period) {
            case 'week':
                // Начало недели (понедельник)
                const weekStart = new Date(date);
                weekStart.setDate(date.getDate() - (date.getDay() || 7) + 1);
                key = weekStart.toLocaleDateString('ru-RU', { month: 'short', day: 'numeric', year: 'numeric' });
                break;
            case 'month':
                key = formatter.format(date); // Форматируем как "месяц год"
                break;
            case 'quarter':
                const quarter = Math.ceil((date.getMonth() + 1) / 3); // Определяем квартал
                key = `${quarter} квартал ${date.getFullYear()}`; // Формируем ключ
                break;
            case 'year':
                key = date.getFullYear().toString(); // Год как строка
                break;
            default:
                console.warn(`Неподдерживаемый период: ${period}`); // Отладка
                return;
        }

        amountsByType[type][key] = (amountsByType[type][key] || 0) + parseFloat(amount); // Суммируем суммы
    });

    // Собираем все уникальные периоды
    const allPeriods = new Set();
    Object.values(amountsByType).forEach(amounts => {
        Object.keys(amounts).forEach(period => allPeriods.add(period)); // Добавляем все периоды
    });

    // Сортируем периоды для корректного отображения
    const sortedPeriods = Array.from(allPeriods).sort((a, b) => {
        if (period === 'week') {
            return new Date(a) - new Date(b); // Сортировка по дате для недель
        }
        return a.localeCompare(b); // Лексикографическая сортировка
    });

    // Формируем данные для графика сравнения
    const comparisonData = {
        labels: sortedPeriods, // Метки для графика
        datasets: [
            {
                label: 'Поступление',
                data: sortedPeriods.map(period => amountsByType['Поступление'][period] || 0), // Суммы поступлений
                backgroundColor: 'rgba(75, 192, 192, 0.8)', // Цвет столбцов
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            },
            {
                label: 'Списание',
                data: sortedPeriods.map(period => amountsByType['Списание'][period] || 0), // Суммы списаний
                backgroundColor: 'rgba(255, 99, 132, 0.8)', // Цвет столбцов
                borderColor: 'rgba(255, 99, 132, 1)',
                borderWidth: 1
            }
        ]
    };

    console.log('Обработанные данные для сравнения:', comparisonData); // Отладка
    return comparisonData; // Возвращаем данные для графика сравнения
}

// Функция обработки данных для графика статуса транзакций
function processStatusData(transactions, period) {
    console.log(`Обработка данных для статуса транзакций, период: ${period}`); // Отладка
    const countsByStatus = {
        'Платеж выполнен': {},
        'Отменена': {}
    }; // Объект для хранения счетчиков по статусу
    const formatter = new Intl.DateTimeFormat('ru-RU', { month: 'long', year: 'numeric' }); // Форматтер для дат

    // Фильтруем и группируем транзакции по статусу "Платеж выполнен" и "Отменена"
    transactions.forEach(transaction => {
        const status = transaction.status; // Исправлено: используем transaction.status
        if (status !== 'Платеж выполнен' && status !== 'Отменена') return; // Пропускаем неподходящие статусы

        const dateStr = transaction.transactionDate;
        if (!dateStr) {
            console.warn('Транзакция без даты:', transaction); // Отладка
            return;
        }

        let date;
        try {
            date = new Date(dateStr); // Преобразуем дату транзакции
            if (isNaN(date.getTime())) {
                throw new Error('Некорректная дата');
            }
        } catch (error) {
            console.warn(`Ошибка парсинга даты в транзакции: ${dateStr}`, transaction); // Отладка
            return;
        }

        let key; // Ключ для периода
        // Определяем ключ в зависимости от выбранного периода
        switch (period) {
            case 'week':
                // Начало недели (понедельник)
                const weekStart = new Date(date);
                weekStart.setDate(date.getDate() - (date.getDay() || 7) + 1);
                key = weekStart.toLocaleDateString('ru-RU', { month: 'short', day: 'numeric', year: 'numeric' });
                break;
            case 'month':
                key = formatter.format(date); // Форматируем как "месяц год"
                break;
            case 'quarter':
                const quarter = Math.ceil((date.getMonth() + 1) / 3); // Определяем квартал
                key = `${quarter} квартал ${date.getFullYear()}`; // Формируем ключ
                break;
            case 'year':
                key = date.getFullYear().toString(); // Год как строка
                break;
            default:
                console.warn(`Неподдерживаемый период: ${period}`); // Отладка
                return;
        }

        countsByStatus[status][key] = (countsByStatus[status][key] || 0) + 1; // Увеличиваем счетчик
    });

    // Собираем все уникальные периоды
    const allPeriods = new Set();
    Object.values(countsByStatus).forEach(counts => {
        Object.keys(counts).forEach(period => allPeriods.add(period)); // Добавляем все периоды
    });

    // Сортируем периоды для корректного отображения
    const sortedPeriods = Array.from(allPeriods).sort((a, b) => {
        if (period === 'week') {
            return new Date(a) - new Date(b); // Сортировка по дате для недель
        }
        return a.localeCompare(b); // Лексикографическая сортировка
    });

    // Формируем данные для графика статуса
    const statusData = {
        labels: sortedPeriods, // Метки для графика
        datasets: [
            {
                label: 'Платеж выполнен',
                data: sortedPeriods.map(period => countsByStatus['Платеж выполнен'][period] || 0), // Количество проведенных
                backgroundColor: 'rgba(54, 162, 235, 0.8)', // Цвет столбцов
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            },
            {
                label: 'Отменена',
                data: sortedPeriods.map(period => countsByStatus['Отменена'][period] || 0), // Количество отмененных
                backgroundColor: 'rgba(255, 159, 64, 0.8)', // Цвет столбцов
                borderColor: 'rgba(255, 159, 64, 1)',
                borderWidth: 1
            }
        ]
    };

    console.log('Обработанные данные для статуса:', statusData); // Отладка
    return statusData; // Возвращаем данные для графика статуса
}

let incomeChart = null; // Переменная для графика поступлений
let expenseChart = null; // Переменная для графика списаний
let comparisonChart = null; // Переменная для графика сравнения
let statusChart = null; // Переменная для графика статуса

// Функция рендеринга всех графиков
function renderCharts(data, incomePeriod = 'month', expensePeriod = 'month', comparisonPeriod = 'month', statusPeriod = 'month') {
    // Проверяем наличие всех canvas элементов
    const incomeCanvas = document.getElementById('incomeChart');
    const expenseCanvas = document.getElementById('expenseChart');
    const comparisonCanvas = document.getElementById('comparisonChart');
    const statusCanvas = document.getElementById('statusChart');
    if (!incomeCanvas || !expenseCanvas || !comparisonCanvas || !statusCanvas) {
        console.error('Один или несколько canvas не найдены:', { incomeCanvas, expenseCanvas, comparisonCanvas, statusCanvas }); // Отладка
        return;
    }

    const incomeCtx = incomeCanvas.getContext('2d'); // Контекст для поступлений
    const expenseCtx = expenseCanvas.getContext('2d'); // Контекст для списаний
    const comparisonCtx = comparisonCanvas.getContext('2d'); // Контекст для сравнения
    const statusCtx = statusCanvas.getContext('2d'); // Контекст для статуса

    // Обрабатываем данные для каждого графика
    const incomeProcessedData = processTransactionData(data, incomePeriod);
    const expenseProcessedData = processTransactionData(data, expensePeriod);
    const comparisonProcessedData = processComparisonData(data, comparisonPeriod);
    const statusProcessedData = processStatusData(data, statusPeriod);

    // Уничтожаем предыдущие графики
    if (incomeChart) {
        incomeChart.destroy();
    }
    if (expenseChart) {
        expenseChart.destroy();
    }
    if (comparisonChart) {
        comparisonChart.destroy();
    }
    if (statusChart) {
        statusChart.destroy();
    }

    console.log('Рендеринг графиков:', { incomePeriod, expensePeriod, comparisonPeriod, statusPeriod }); // Отладка

    // Создаем график для поступлений (по количеству транзакций)
    incomeChart = new Chart(incomeCtx, {
        type: 'line', // Тип графика - линия
        data: incomeProcessedData.incomeData, // Данные для поступлений
        options: {
            responsive: true, // График адаптируется к размеру контейнера
            scales: {
                y: {
                    beginAtZero: true, // Ось Y начинается с нуля
                    title: {
                        display: true,
                        text: 'Количество транзакций' // Подпись оси Y
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Период' // Подпись оси X
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top' // Легенда сверху
                },
                tooltip: {
                    mode: 'index', // Всплывающие подсказки
                    intersect: false
                },
                title: {
                    display: true,
                    text: 'Динамика поступлений' // Заголовок графика
                }
            },
            interaction: {
                mode: 'nearest', // Взаимодействие с ближайшей точкой
                axis: 'x',
                intersect: false
            }
        }
    });

    // Создаем график для списаний (по количеству транзакций)
    expenseChart = new Chart(expenseCtx, {
        type: 'line', // Тип графика - линия
        data: expenseProcessedData.expenseData, // Данные для списаний
        options: {
            responsive: true, // График адаптируется к размеру контейнера
            scales: {
                y: {
                    beginAtZero: true, // Ось Y начинается с нуля
                    title: {
                        display: true,
                        text: 'Количество транзакций' // Подпись оси Y
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Период' // Подпись оси X
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top' // Легенда сверху
                },
                tooltip: {
                    mode: 'index', // Всплывающие подсказки
                    intersect: false
                },
                title: {
                    display: true,
                    text: 'Динамика списаний' // Заголовок графика
                }
            },
            interaction: {
                mode: 'nearest', // Взаимодействие с ближайшей точкой
                axis: 'x',
                intersect: false
            }
        }
    });

    // Создаем график для сравнения сумм поступлений и списаний
    comparisonChart = new Chart(comparisonCtx, {
        type: 'bar', // Тип графика - столбчатый
        data: comparisonProcessedData, // Данные для сравнения
        options: {
            responsive: true, // График адаптируется к размеру контейнера
            scales: {
                y: {
                    beginAtZero: true, // Ось Y начинается с нуля
                    title: {
                        display: true,
                        text: 'Сумма (руб.)' // Подпись оси Y
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Период' // Подпись оси X
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top' // Легенда сверху
                },
                tooltip: {
                    mode: 'index', // Всплывающие подсказки для всех столбцов в периоде
                    intersect: false
                },
                title: {
                    display: true,
                    text: 'Сравнение количества поступивших средств и потраченных' // Заголовок графика
                }
            },
            interaction: {
                mode: 'nearest', // Взаимодействие с ближайшей точкой
                axis: 'x',
                intersect: false
            }
        }
    });

    // Создаем график для статуса транзакций (проведенные и отмененные)
    statusChart = new Chart(statusCtx, {
        type: 'bar', // Тип графика - столбчатый
        data: statusProcessedData, // Данные для статуса
        options: {
            responsive: true, // График адаптируется к размеру контейнера
            scales: {
                y: {
                    beginAtZero: true, // Ось Y начинается с нуля
                    title: {
                        display: true,
                        text: 'Количество транзакций' // Подпись оси Y
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Период' // Подпись оси X
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top' // Легенда сверху
                },
                tooltip: {
                    mode: 'index', // Всплывающие подсказки для всех столбцов в периоде
                    intersect: false
                },
                title: {
                    display: true,
                    text: 'Количество проведенных транзакций и отмененных транзакций' // Заголовок графика
                }
            },
            interaction: {
                mode: 'nearest', // Взаимодействие с ближайшей точкой
                axis: 'x',
                intersect: false
            }
        }
    });
}