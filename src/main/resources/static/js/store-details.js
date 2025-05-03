const assignModal = document.getElementById('assignCashierModal');

  assignModal.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const registerId = button.getAttribute('data-register-id');
    const cashierSelect = document.getElementById('cashierId');
    const form = document.getElementById('assignCashierForm');

    // Провери дали има опции в падащото меню
    if (cashierSelect.options.length === 0) {
      event.preventDefault(); // спира отварянето на модала
      alert('Няма свободни касиери.');
      return;
    }

    // Попълни данни
    document.getElementById('cashRegisterId').value = registerId;
    form.action = '/cashRegister/' + registerId + '/assign';
  });