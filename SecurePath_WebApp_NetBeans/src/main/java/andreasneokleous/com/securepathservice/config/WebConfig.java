package andreasneokleous.com.securepathservice.config;

import andreasneokleous.com.securepathservice.dao.CrimeDAO;
import andreasneokleous.com.securepathservice.dao.CrimeDAOImpl;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 *
 * @author Andreas Neokleous
 * In this class the web MVC configuration is set up with annotations.
 * It extends the base class WebMvcConfigurerAdapter and the needed methods are 
 * overriden
 */
@Configuration
@EnableWebMvc
@ComponentScan({"andreasneokleous.com.securepathservice.controller"})
@Import({AppProperties.class})
public class WebConfig extends WebMvcConfigurerAdapter {

    public WebConfig() {
    }
    
    /**
     * View resolver to setup the Views of the application (JSP files)
     */    
    @Bean
    public ContentNegotiatingViewResolver contentViewResolver() throws Exception {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        ContentNegotiatingViewResolver contentViewResolver = new ContentNegotiatingViewResolver();
        contentViewResolver.setViewResolvers(Arrays.<ViewResolver>asList(viewResolver));
        return contentViewResolver;
    }
    
    /**
     * This method is used to forward the requested URLs to the appropriate views
     * 
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:home");
    }

    /**
     * Added to allow convert JSON to HTML
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());

        super.configureMessageConverters(converters);
    }
    
    /**
     * This method sets the location of the resources
     */
       @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/config/**").addResourceLocations("classpath:/config/");
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }
 
        
   @Bean
    public DriverManagerDataSource getDataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        driverManagerDataSource.setUrl("jdbc:hive2://localhost:10000/securepathdb");
        driverManagerDataSource.setUsername("Spring_Web_App");
        driverManagerDataSource.setPassword("");
        return driverManagerDataSource;
    }

    @Bean
    public CrimeDAO getCrimeDAO(){
        return new CrimeDAOImpl(getDataSource());
    }
}
    