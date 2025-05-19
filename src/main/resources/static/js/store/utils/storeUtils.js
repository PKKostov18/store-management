export function getStoreId() {
  return document.body.getAttribute("data-store-id");
}

export function getCashierId() {
  const activeRegister = document.querySelector('.register-card.register-active');
  const cashierName = activeRegister?.querySelector('span')?.textContent?.replace('Касиер: ', '').trim();
  if (!cashierName) return null;

  const select = document.getElementById('cashierId');
  return Array.from(select?.options || []).find(opt => opt.text === cashierName)?.value || null;
}
