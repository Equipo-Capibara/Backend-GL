package escuelaing.edu.co.bakend_gl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * Configuración de WebSocket para la aplicación
 * Establece los endpoints, brokers y configuraciones necesarias para la comunicación en tiempo real
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    @Value("${websocket.endpoint:/ws}")
    private String endpoint;

    @Value("${websocket.broker-destinations:/topic}")
    private String[] brokerDestinations;

    @Value("${websocket.application-prefixes:/app}")
    private String applicationPrefix;

    /**
     * Configura el broker de mensajes para la comunicación WebSocket
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Broker de mensajes para envío de mensajes desde el servidor al cliente
        registry.enableSimpleBroker(brokerDestinations);

        // Prefijos para mensajes enviados desde el cliente al servidor
        registry.setApplicationDestinationPrefixes(applicationPrefix);
    }

    /**
     * Registra los endpoints para la conexión WebSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(endpoint)
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS()
                .setHeartbeatTime(25000)
                .setDisconnectDelay(30000);

        // Endpoint adicional sin SockJS para clientes que no lo necesiten (POSTMAN)
        registry.addEndpoint(endpoint)
                .setAllowedOriginPatterns(allowedOrigins);
    }

    /**
     * Configuración del transporte WebSocket
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(128 * 1024)    // Límite del tamaño del mensaje (128KB)
                .setSendBufferSizeLimit(512 * 1024) // Límite del buffer de envío (512KB)
                .setSendTimeLimit(20000);           // Límite de tiempo para enviar mensaje (20s)
    }
}