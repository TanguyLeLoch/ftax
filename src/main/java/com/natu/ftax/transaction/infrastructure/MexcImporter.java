package com.natu.ftax.transaction.infrastructure;

import com.natu.ftax.transaction.application.PlatformImporter;
import com.natu.ftax.transaction.application.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MexcImporter implements PlatformImporter {

    private final TransactionRepository transactionRepository;

    public MexcImporter(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    @Override
    public void importTransaction(MultipartFile file) {


    }
}
