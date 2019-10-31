package com.example.client;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@SpringBootApplication
public class RsocketClientJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsocketClientJavaApplication.class, args);
    }

	@Bean
	RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
		return RSocketRequester.builder()
				.rsocketStrategies(rSocketStrategies)
				.connectTcp("localhost", 7000)
				.block();
	}

}

//@Configuration
// class ClientConfiguration {
//
//	@Bean
//	public RSocket rSocket() {
//		return RSocketFactory
//				.connect()
//				.mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE, MimeTypeUtils.APPLICATION_JSON_VALUE)
//				.frameDecoder(PayloadDecoder.ZERO_COPY)
//				.transport(TcpClientTransport.create(7000))
//				.start()
//				.block();
//	}

//	@Bean
//	RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
//		return RSocketRequester.wrap(rSocket(), MimeTypeUtils.APPLICATION_JSON,MimeTypeUtils.APPLICATION_JSON, rSocketStrategies);
//	}
//}
@RestController
class GreetingController {
	private final RSocketRequester requester;

	GreetingController(RSocketRequester requester) {
		this.requester = requester;
	}

	@GetMapping(value = "/hello")
	Publisher<Void> hello() {
		return requester.
				route("hello")
				.data(new Person("Sunil"))
				.send();
	}
	@GetMapping("/greet/{name}")
	public Publisher<GreetingsResponse> greet(@PathVariable String name) {
		return requester
				.route("greet")
				.data(new GreetingsRequest(name))
				.retrieveMono(GreetingsResponse.class);
	}
}

class Person{
	private String name;

	public String getName() {
		return name;
	}







	public void setName(String name) {
		this.name = name;
	}

	public Person(String name) {
		this.name = name;
	}
	public Person(){

	}
}

class GreetingsRequest {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GreetingsRequest(String name) {
		this.name = name;
	}
	public GreetingsRequest() {

	}
}



class GreetingsResponse {

	private String greeting;

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	public GreetingsResponse(String greeting) {
		this.greeting = greeting;
	}
	public GreetingsResponse(){

	}
}