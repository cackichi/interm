package org.example.cfg;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperCfg {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
