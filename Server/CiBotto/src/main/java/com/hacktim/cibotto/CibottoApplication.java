package com.hacktim.cibotto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CibottoApplication {

    private static final Logger logger = LoggerFactory.getLogger(CibottoApplication.class);

    public static void main(String[] args) {
        logger.debug("starting ...");
        SpringApplication.run(CibottoApplication.class, args);
    }


}
