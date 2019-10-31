package com.example.client;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@SpringBootApplication
public class RsocketClientJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsocketClientJavaApplication.class, args);
    }



}

@Configuration
class RSocketConfig {


	@Bean
	RSocketRequester requester(RSocketStrategies rSocketStrategies) {
		return RSocketRequester.builder(this.rSocket(), MimeTypeUtils.APPLICATION_JSON, rSocketStrategies);
	}

	@Bean
	RSocket rSocket() {
		return RSocketFactory
				.connect()
				.frameDecoder(PayloadDecoder.ZERO_COPY)
				.dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
				.transport(TcpClientTransport.create(7000))
				.start()
				.block();
	}
}
@RestController
class GreetingController {
	private final RSocketRequester requester;

	GreetingController(RSocketRequester requester) {
		this.requester = requester;
	}

	@GetMapping(value = "/hi")
	Mono<Void> hello() {
		return this.requester.route("hello").data("hello").send();
	}
}