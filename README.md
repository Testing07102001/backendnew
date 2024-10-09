# Stripe Payment Integration with Java and Spring Boot

This repository provides a simple implementation of Stripe payments in a Java and Spring Boot application. It includes a single microservice dedicated to handling Stripe payments. The project is built using Maven and demonstrates the integration of the Stripe Java library.

## Prerequisites

Before starting, make sure you have a Stripe Secret Key. Additionally, Stripe offers a Test mode for developers to simulate transactions without real money.

## Getting Started

Clone the repository:

```git clone https://github.com/orlandolorenzo724/stripe.git```

## Maven Dependency
```
<!-- https://mvnrepository.com/artifact/com.stripe/stripe-java -->
<dependency>
    <groupId>com.stripe</groupId>
    <artifactId>stripe-java</artifactId>
    <version>24.3.0-beta.1</version>
</dependency>
```

## Configuration
Configure your application.yml:

```
server:
  port: ${PORT}
  # other configurations...

spring:
  # other configurations...
  stripe:
    secretKey: ${STRIPE_SECRET_KEY}
```

Set ```PORT``` and ```STRIPE_SECRET_KEY``` either in your IntelliJ Run Configuration Env or directly in the application.yml.

## API Endpoints
### Create Payment
#### Endpoint: /api/v1/stripe/create-payment
##### Method: POST
#### Request Body:

```
{
  "amount": 1000,
  "quantity": 1,
  "currency": "USD",
  "name": "Product Name",
  "successUrl": "http://localhost:3000/success",
  "cancelUrl": "http://localhost:3000/cancel"
}
```
#### Response:
```
{
  "status": "SUCCESS",
  "message": "Payment session created successfully",
  "httpStatus": 200,
  "data": {
    "sessionId": "stripe-session-id",
    "sessionUrl": "checkout-url"
  }
}
```

### Capture Payment
#### Endpoint: /api/v1/stripe/capture-payment
##### Method: GET
#### Request Parameter:
```
sessionId: The ID of the payment session
```
#### Response:
```
{
  "status": "SUCCESS",
  "message": "Payment successfully captured.",
  "httpStatus": 200,
  "data": {
    "sessionId": "stripe-session-id",
    "sessionStatus": "complete",
    "paymentStatus": "paid"
  }
}

```

## Controller Class
The main controller class is ```StripeController``` which handles the endpoints. Check the source code for more details.

## Conclusion

Stripe offers a robust payment processing solution, and this project provides a straightforward integration into Java and Spring Boot applications. Feel free to explore the full code on GitHub.
