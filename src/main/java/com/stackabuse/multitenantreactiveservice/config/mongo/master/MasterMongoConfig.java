package com.stackabuse.multitenantreactiveservice.config.mongo.master;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;
import java.util.Objects;

@Configuration
@EnableMongoRepositories(basePackages = {"com.stackabuse.multitenantnosqlservice.repository"})
public class MasterMongoConfig {

    @Bean
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(new StringToClassConverter()));
    }

    @Bean
    @ConditionalOnMissingBean(MongoConverter.class)
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context,
                                                       MongoCustomConversions conversions) {
        MappingMongoConverter mappingConverter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }

    @Bean
    @ConditionalOnMissingBean(DataBufferFactory.class)
    public DefaultDataBufferFactory dataBufferFactory() {
        return new DefaultDataBufferFactory();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({SocketChannel.class, NioEventLoopGroup.class})
    static class NettyDriverConfiguration {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        MasterMongoConfig.NettyDriverMongoClientSettingsBuilderCustomizer nettyDriverCustomizer(
                ObjectProvider<MongoClientSettings> settings) {
            return new MasterMongoConfig.NettyDriverMongoClientSettingsBuilderCustomizer(settings);
        }
    }

    private static final class NettyDriverMongoClientSettingsBuilderCustomizer
            implements MongoClientSettingsBuilderCustomizer, DisposableBean {

        private final ObjectProvider<MongoClientSettings> settings;

        private volatile EventLoopGroup eventLoopGroup;

        private NettyDriverMongoClientSettingsBuilderCustomizer(ObjectProvider<MongoClientSettings> settings) {
            this.settings = settings;
        }

        @Override
        public void customize(MongoClientSettings.Builder builder) {
            if (!isStreamFactoryFactoryDefined(this.settings.getIfAvailable())) {
                NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
                this.eventLoopGroup = eventLoopGroup;
                builder.streamFactoryFactory(
                        NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build());
            }
        }

        @Override
        public void destroy() {
            EventLoopGroup eventLoopGroup = this.eventLoopGroup;
            if (Objects.nonNull(eventLoopGroup)) {
                eventLoopGroup.shutdownGracefully().awaitUninterruptibly();
                this.eventLoopGroup = null;
            }
        }

        private boolean isStreamFactoryFactoryDefined(MongoClientSettings settings) {
            return Objects.nonNull(settings) && Objects.nonNull(settings.getStreamFactoryFactory());
        }
    }

    private static class StringToClassConverter implements Converter<String, Class> {

        @Override
        public Class convert(String source) {
            try {
                return Class.forName(source);
            } catch (ClassNotFoundException e) {
                throw new MongoException("Couldn't read string as class type", e);
            }

        }
    }
}

