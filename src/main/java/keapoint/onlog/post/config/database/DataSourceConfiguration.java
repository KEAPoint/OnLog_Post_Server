package keapoint.onlog.post.config.database;

import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfiguration {

    private static final String WRITER_DATASOURCE = "writerDataSource";
    private static final String READER_DATASOURCE = "readerDataSource";


    @Bean(WRITER_DATASOURCE) // writerDataSource 이름의 Bean을 생성한다.
    @ConfigurationProperties(prefix = "spring.datasource.writer") // 접두사로 시작하는 속성을 사용해서 Bean을 구성한다.
    public DataSource writerDataSource() {
        return DataSourceBuilder.create()
                // HikariDataSource 타입의 DataSource 객체를 생성한다.
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(READER_DATASOURCE) // readerDataSource 이름의 Bean을 생성한다.
    @ConfigurationProperties(prefix = "spring.datasource.reader")
    public DataSource readerDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public DataSource routingDataSource(
            @Qualifier(WRITER_DATASOURCE) DataSource writerDataSource,
            @Qualifier(READER_DATASOURCE) DataSource readerDataSource) { // writerDataSource와 readerDataSource라는 이름을 가진 Bean을 주입받는다.

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> datasourceMap = ImmutableMap.<Object, Object>builder()
                .put("writer", writerDataSource)
                .put("reader", readerDataSource)
                .build();

        // RoutingDataSource의 대상 데이터 소스를 위에서 생성한 맵으로 지정한다.
        routingDataSource.setTargetDataSources(datasourceMap);

        // 기본 대상 데이터 소스를 writerDataSource로 설정한다.
        routingDataSource.setDefaultTargetDataSource(writerDataSource);

        return routingDataSource;
    }

    @Primary // 동일한 타입의 여러 Bean 중에서 우선적으로 사용되는 기본 Bean을 설정한다.
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        // 지연 연결 기능을 제공하기 위해서 사용한다 -> 데이터베이스 연결의 지연 실행을 지원하고, 필요한 시점에서만 연결을 수행하도록 구성한다.
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

}