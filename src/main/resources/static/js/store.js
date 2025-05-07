const assignModal = document.getElementById('assignCashierModal');

  assignModal.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const registerId = button.getAttribute('data-register-id');
    const cashierSelect = document.getElementById('cashierId');
    const form = document.getElementById('assignCashierForm');

    document.getElementById('cashRegisterId').value = registerId;
    form.action = '/cashRegister/' + registerId + '/assign';
  });