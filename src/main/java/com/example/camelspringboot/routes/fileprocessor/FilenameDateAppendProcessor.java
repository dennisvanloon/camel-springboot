package com.example.camelspringboot.routes.fileprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.camel.Exchange.FILE_NAME;

@Component
public class FilenameDateAppendProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        var originalFileName = exchange.getIn().getHeader(FILE_NAME, String.class);

        var dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        var changedFileName = dateFormat.format(new Date()) + originalFileName;
        exchange.getIn().setHeader(FILE_NAME, changedFileName);
    }

}