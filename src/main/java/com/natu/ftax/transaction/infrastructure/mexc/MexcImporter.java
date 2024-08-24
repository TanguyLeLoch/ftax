package com.natu.ftax.transaction.infrastructure.mexc;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.transaction.application.PlatformImporter;
import com.natu.ftax.transaction.application.TransactionRepository;
import com.natu.ftax.transaction.domain.EditTransactionCommand;
import com.natu.ftax.transaction.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component("MexcImporter")
public class MexcImporter implements PlatformImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            MexcImporter.class);

    private final TransactionRepository transactionRepository;
    private final IdGenerator idGenerator;

    public MexcImporter(TransactionRepository transactionRepository,
            IdGenerator idGenerator) {
        this.transactionRepository = transactionRepository;
        this.idGenerator = idGenerator;
    }


    @Override
    public void importTransaction(MultipartFile file) {
        List<MexcData> mexcDatas = getMexcData(file);
        List<Transaction> transactions = new ArrayList<>();
        for (MexcData mexcData : mexcDatas) {
            String id = idGenerator.generate();
            Transaction transaction = Transaction.create(id);
            EditTransactionCommand command = mexcData.toCommand(id);
            command.execute(transaction);
            try {
                transaction.submit();
            } catch (FunctionalException e) {
                LOGGER.info("Transaction cannot be submitted: {}",
                        e.getMessage());
            }
            transactions.add(transaction);
        }
        transactionRepository.saveAll(transactions);

        System.out.println(mexcDatas);
    }

    private static List<MexcData> getMexcData(MultipartFile file) {
        List<MexcData> mexcDatas = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), MexcData.class,
                    new ReadListener<MexcData>() {


                        @Override
                        public void invoke(MexcData mexcData,
                                AnalysisContext analysisContext) {
                            mexcDatas.add(mexcData);
                        }

                        @Override
                        public void doAfterAllAnalysed(
                                AnalysisContext analysisContext) {

                            LOGGER.info("Importing {} transactions",
                                    mexcDatas.size());

                        }
                    }
            ).sheet().doRead();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mexcDatas;
    }
}
