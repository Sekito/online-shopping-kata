package com.bem.onlineshopping;

import com.bem.onlineshopping.dto.SignupDTO;
import com.bem.onlineshopping.model.Cart;
import com.bem.onlineshopping.model.Customer;
import com.bem.onlineshopping.model.Product;
import com.bem.onlineshopping.repository.CartRepository;
import com.bem.onlineshopping.repository.CustomerRepository;
import com.bem.onlineshopping.repository.ProductRepository;
import com.bem.onlineshopping.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class OnlineShoppingKataApplication implements CommandLineRunner {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private  AuthenticationService authenticationService;

	public static void main(String[] args) {
		SpringApplication.run(OnlineShoppingKataApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (productRepository.count() == 0) {
			initializeProducts();
		}
	}

	private void initializeProducts() {
		Product product1 = new Product();
		product1.setName("Product 1");
		product1.setPrice(10.99);
		product1.setInventory(5);
		productRepository.save(product1);



		Product product2 = new Product();
		product2.setName("Product 2");
		product2.setPrice(15.49);
		product2.setInventory(2);
		productRepository.save(product2);


		Product product3 = new Product();
		product3.setName("Product 3");
		product3.setPrice(8.99);
		product3.setInventory(3);
		productRepository.save(product3);



		if (customerRepository.count() == 0) {
			/*Customer customer = new Customer();
			customer.setCustomerName("Bentouri El Mehdi");
			customer.setEmail("bentouri.elmehdi@example.com");
			customer.setPassword("123456");*/
			SignupDTO signupDTO = new SignupDTO();
			signupDTO.setEmail("bentouri.elmehdi@example.com");
			signupDTO.setPassword("123456");
			signupDTO.setFullName("bentouri.elmehdi");
			Customer customer  = authenticationService.signup(signupDTO);

			Cart cart = new Cart();
			cart.setCustomer(customer);
			customer.setCart(cart);
			customer = customerRepository.save(customer);
			//cartRepository.save(cart);
			System.out.println("Customer created: " + customer.getCustomerName() +" id : "+customer.getCustomerId());
			System.out.println("Cart created for customer, cartId =  " + cart.getCartId());
		} else {
			System.out.println("Customer already exists.");
		}
	}
}
