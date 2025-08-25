# spring-ecommerce-order
## Feature List
### Step 2
#### todo
- [x] Change CartItem's property: make reference Option instead of Product
  - [x] Refactor Option to decouple it from Product
  - [x] Clean up code to make it testable
  - [x] Make to pass all tests
  - [ ] 
#### Order - Payment
- [x] Configure Stripe **sandbox secret key** in environment variables (`STRIPE_SECRET_KEY`).
- [x] Implement `POST /orders` endpoint.
  - [x] Implement new entities `Option`, `OptionItem`, `Payment`, `Order`
  - [x] Accept `optionId`, `quantity`, `paymentMethodId` as request parameters.
  - [x] Call Stripe Payment Create API with the provided details.
  - [x] On success:
    - [x] Decrease stock for the selected product option.
    - [x] Remove the product from the user's cart if it exists.
    - [x] Save the order record.
  - [x] On failure:
    - "Payment failed"
