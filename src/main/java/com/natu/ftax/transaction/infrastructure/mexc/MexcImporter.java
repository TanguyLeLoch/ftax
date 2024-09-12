package com.natu.ftax.transaction.infrastructure.mexc;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.transaction.application.PlatformImporter;
import com.natu.ftax.transaction.simplified.TransactionSimplified;
import com.natu.ftax.transaction.simplified.TransactionSimplifiedRepositoryJpa;
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

    private final IdGenerator idGenerator;
    private final TransactionSimplifiedRepositoryJpa transactionSimplifiedRepositoryJpa;

    public MexcImporter(IdGenerator idGenerator, TransactionSimplifiedRepositoryJpa transactionSimplifiedRepositoryJpa) {
        this.idGenerator = idGenerator;
        this.transactionSimplifiedRepositoryJpa = transactionSimplifiedRepositoryJpa;
    }


    @Override
    public void importTransaction(MultipartFile file) {
        List<MexcData> mexcDatas = getMexcData(file);
        List<TransactionSimplified> transactions = new ArrayList<>();
        for (MexcData mexcData : mexcDatas) {
            String id = idGenerator.generate();
            TransactionSimplified transaction = new TransactionSimplified();
            transaction.setId(id);

        }
        transactionSimplifiedRepositoryJpa.saveAll(transactions);

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
