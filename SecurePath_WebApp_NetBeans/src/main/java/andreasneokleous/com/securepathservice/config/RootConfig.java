package andreasneokleous.com.securepathservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Andreas Neokleous
 * This class is used to register the Spring application in the AnnotationConfigWebApplicationContext (MainInitializer class)
 */
@Configuration
// Scan all components, but dont scan (i.e. filter out) the classes that are annotated as @Controllers. This is scanned separatelly in WebConfig
@ComponentScan(value = {"andreasneokleous.com.securepathservice"}, excludeFilters = {
    @ComponentScan.Filter(Controller.class)})
@Import({AppProperties.class})
public class RootConfig {

}
