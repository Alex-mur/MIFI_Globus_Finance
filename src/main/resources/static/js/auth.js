document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080';

    async function makeRequest(url, method, body = null, authToken = null) {
        const headers = {
            'Content-Type': 'application/json',
        };

        if (authToken) {
            headers['Authorization'] = `Bearer ${authToken}`;
        }

        const response = await fetch(`${API_BASE_URL}${url}`, {
            method,
            headers,
            body: body ? JSON.stringify(body) : null,
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Something went wrong');
        }

        return response.json();
    }

    let api = {
        login: (credentials) => makeRequest('/api/auth/login', 'POST', credentials),
        register: (userData) => makeRequest('/api/auth/register', 'POST', userData),
        getProtectedData: (token) => makeRequest('/api/protected', 'GET', null, token),
    };

    // Проверяем, есть ли токен при загрузке страницы
    checkAuth();

    // Обработчики форм
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const logoutBtn = document.getElementById('logoutBtn');

    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }

    if (logoutBtn) {
        logoutBtn.addEventListener('click', handleLogout);
    }

    async function handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const { token } = await api.login({ username, password });
            localStorage.setItem('jwtToken', token);
            window.location.href = '/lk.html';
        } catch (error) {
            alert(error.message);
        }
    }

    async function handleRegister(e) {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const email = document.getElementById('email').value;

        try {
            const { token } = await api.register({ username, password, email });
            localStorage.setItem('jwtToken', token);
            window.location.href = '/lk.html';
        } catch (error) {
            alert(error.message);
        }
    }

    function handleLogout() {
        localStorage.removeItem('jwtToken');
        window.location.href = '/auth/login.html';
    }

    function checkAuth() {
        const token = localStorage.getItem('jwtToken');
        const currentPage = window.location.pathname;

        if (token && (currentPage.includes('login.html') || currentPage.includes('register.html'))) {
            window.location.href = '/lk.html';
        } else if (!token && currentPage.includes('lk.html')) {
            window.location.href = '/auth/login.html';
        }

        // Если на dashboard, показываем имя пользователя
        if (currentPage.includes('lk.html') && token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                document.getElementById('usernameDisplay').textContent = payload.username;
            } catch (e) {
                console.error('Error parsing token', e);
            }
        }
    }
});