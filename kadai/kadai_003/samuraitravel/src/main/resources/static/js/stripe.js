const stripe = Stripe('pk_test_51PrKvaJtCNxzDT8faic2w2wYfpGZMU9yDNrUCPGkgmpjXSgFR3Xw5xkbMes4jfI8bdO7hXmRfjmMYOZaWPhQNxPE00JesaIkB8');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
  stripe.redirectToCheckout({
    sessionId: sessionId
  })
});
