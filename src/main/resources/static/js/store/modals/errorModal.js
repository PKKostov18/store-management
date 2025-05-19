export function showErrorModal(message) {
  const errorModalBody = document.getElementById('errorModalBody');
  errorModalBody.textContent = message;

  const errorModalElement = document.getElementById('errorModal');
  new bootstrap.Modal(errorModalElement).show();
}
