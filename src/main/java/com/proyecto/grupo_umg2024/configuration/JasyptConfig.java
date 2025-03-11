package com.proyecto.grupo_umg2024.configuration;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {
    @Bean(name = "encryptorBean")
    public StringEncryptor stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("passwordUMGB"); 
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        return encryptor;
    }
}
