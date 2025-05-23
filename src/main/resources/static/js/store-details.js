function showSection(section, button) {
    // Скрий всички секции
    document.getElementById('products-section').style.display = 'none';
    document.getElementById('cashiers-section').style.display = 'none';
    document.getElementById('receipts-section').style.display = 'none';
    document.getElementById('financialReport-section').style.display = 'none';

    // Покажи избраната секция
    document.getElementById(section + '-section').style.display = 'block';

    // Обнови активен бутон
    document.querySelectorAll('#storeFilterTabs .nav-link').forEach(el => {
        el.classList.remove('active');
    });
    button.classList.add('active');
}