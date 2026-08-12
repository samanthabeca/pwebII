package br.edu.ifto.pwebII;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Configuration para indicar ao Spring que essa é uma classe de configuração.
 * @author samanthabeca
 */
@Configuration
public class ConfiguracaoSpringMvc implements WebMvcConfigurer {


    /**
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addRedirectViewController("/","/funcionario/list");
        registry.addViewController("/").setViewName("forward:/index"); //aponta para a página
    }
}