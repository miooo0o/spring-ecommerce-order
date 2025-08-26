# spring-ecommerce-order
## eCommerce: Orders - ORM, integration with external APIs<br>
Simple e-commerce checkout flow implementation using external API integration with Stripe for payment processing.<br>
Basic deployment setup on Amazon AWS with Spring Boot application.

### Step 2

#### start.sh
```shell
#!/bin/bash

JAR_NAME="spring-ecommerce-0.0.1-SNAPSHOT.jar"
DEPLOY_PATH="/home/ubuntu/app/"

echo "> Checking for a currently running application..."
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]
then
  echo "> No running application found."
else
  echo "> Stopping running application. PID: $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> Starting application..."
nohup java -jar $DEPLOY_PATH$JAR_NAME > ./application.log 2>&1 &
```

---

#### Step 2, Second: Code Review applies
#### Configuration & Setup
- [ ] Move `@EnableConfigurationProperties(StripeProperties::class)` annotation...
- [x] Add shell script for deployment

#### StripeClient Improvements
- [x] Configure timeouts for RestClient (connect timeout: 500ms, read timeout: 10s)
- [x] Implement detailed exception handling following Stripe documentation
  - [x] Parse Stripe error response body into `StripeErrorInfo`
  - [x] Create specific exception types for different Stripe error scenarios

#### Transaction Management
- [x] Review external API call within transaction in `CheckoutService`
- [x] Analyze potential problems with having Stripe API call inside `@Transactional`

#### Documentation
- [x] Update README.md with proper project description
- [x] Remove or complete the "todo" section in README.md


#### Step 2, First: Order - Payment
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
- [x] Change CartItem's property: make reference Option instead of Product
  - [x] Refactor Option to decouple it from Product
  - [x] Clean up code to make it testable
  - [x] Make to pass all tests