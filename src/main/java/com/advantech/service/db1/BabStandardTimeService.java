/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.helper.PropertiesReader;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabStandardTimeHistory;
import com.advantech.model.db1.Worktime;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author MFG.ESOP
 */
@Component
public class BabStandardTimeService {

    private BigDecimal ASSY_WORKTIME_ALLOWANCE_STANDARD;
    private BigDecimal PACKING_WORKTIME_ALLOWANCE_STANDARD;

    @Autowired
    private PropertiesReader p;

    @Autowired
    private WorktimeService worktimeService;

    @Autowired
    private BabStandardTimeHistoryService babStandardTimeHistoryService;

    @Autowired
    private BabService babService;

    @PostConstruct
    public void init() {
        this.ASSY_WORKTIME_ALLOWANCE_STANDARD = p.getAssyWorktimeAllowanceStandard();
        this.PACKING_WORKTIME_ALLOWANCE_STANDARD = p.getPackingWorktimeAllowanceStandard();
    }

    public BigDecimal[] findMaxAndMinAllowanceByBabFromWorktime(int babId) {
        Bab b = babService.findWithLineInfo(babId);
        Worktime w = worktimeService.findByModelName(b.getModelName());
        return this.findMaxAndMinAllowanceByBabFromWorktime(b, w);
    }
    
    public BigDecimal[] findMaxAndMinAllowanceByBabFromWorktime(Bab b, Worktime w) {
        
        
        BigDecimal[] result = new BigDecimal[2];
        if (b == null) {
            return result;
        }
        String lineTypeName = b.getLine().getLineType().getName();
        BigDecimal standardTime = BigDecimal.ZERO;

        if ("ASSY".equals(lineTypeName)) {
            standardTime = w.getAssy();
        }

        if ("Packing".equals(lineTypeName)) {
            standardTime = w.getPacking();
        }

        return findMaxAndMinAllowance(standardTime, b);
    } 

    public BigDecimal[] findMaxAndMinAllowanceByBabFromHistory(int babId) {
        Bab b = babService.findWithLineInfo(babId);
        BabStandardTimeHistory standardTimeHistory = babStandardTimeHistoryService.findByBab(babId);
        BigDecimal[] result = new BigDecimal[2];
        if (b == null) {
            return result;
        }
        return findMaxAndMinAllowance(standardTimeHistory.getStandardTime(), b);
    }

    private BigDecimal[] findMaxAndMinAllowance(BigDecimal worktime, Bab b) {
        String lineTypeName = b.getLine().getLineType().getName();
        BigDecimal[] result = new BigDecimal[2];
        BigDecimal allowance = BigDecimal.ZERO;

        /*
            min = (worktime - allowanceMin) * 60(to second) / people
            max = (worktime - allowanceMax) * 60(to second) / people
         */
        if ("ASSY".equals(lineTypeName)) {
            allowance = worktime.multiply(this.ASSY_WORKTIME_ALLOWANCE_STANDARD);

        } else if ("Packing".equals(lineTypeName)) {
            allowance = worktime.multiply(this.PACKING_WORKTIME_ALLOWANCE_STANDARD);
        }

        result[0] = worktime.subtract(allowance);
        result[1] = worktime.add(allowance);

        formateResult(result, b.getPeople());
        return result;
    }

    private void formateResult(BigDecimal[] b, int people) {
        for (int i = 0; i < b.length; i++) {
            b[i] = b[i].multiply(new BigDecimal(60)).divide(new BigDecimal(people));
        }
    }

}
