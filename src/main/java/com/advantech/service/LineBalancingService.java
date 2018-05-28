/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service;

import com.advantech.model.Bab;
import com.advantech.model.Line;
import com.advantech.model.LineBalancing;
import com.advantech.dao.LineBalancingDAO;
import com.advantech.dao.LineDAO;
import com.advantech.dao.UserDAO;
import com.advantech.helper.MailManager;
import com.advantech.helper.PropertiesReader;
import com.advantech.model.LineType;
import com.advantech.model.User;
import com.advantech.model.view.BabAvg;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional("transactionManager")
public class LineBalancingService {

    private static final Logger log = LoggerFactory.getLogger(LineBalancingDAO.class);

    @Autowired
    private PropertiesReader p;

    private Integer balanceRoundDigit;
    private Double balnDiff;

    @Autowired
    private LineBalancingDAO lineBalanceDAO;

    @Autowired
    private LineDAO lineDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MailManager mailManager;

    @PostConstruct
    private void init() {
        log.info(LineBalancingService.class.getName() + " init inner paramaters.");
        balanceRoundDigit = p.getBabLineBalanceRoundDigit();
        balnDiff = p.getBabLineBalanceAlarmDiff();
    }

    public List<LineBalancing> findAll() {
        return lineBalanceDAO.findAll();
    }

    public LineBalancing findByPrimaryKey(Object obj_id) {
        return lineBalanceDAO.findByPrimaryKey(obj_id);
    }

    public LineBalancing getMaxBalance(Bab bab, LineType lineType) {
        return lineBalanceDAO.getMaxBalance(bab, lineType);
    }

    public int insert(Bab bab) {
        List<BabAvg> balances = bab.getBabAvgs();

        LineType lineType = lineDAO.findLineType(bab.getLine().getId());

        LineBalancing maxBaln = this.getMaxBalance(bab, lineType); //先取得max才insert，不然會抓到自己
        double baln = this.caculateLineBalance(balances);

        LineBalancing record = new LineBalancing(
                bab.getPeople(),
                lineType.getName(),
                bab.getPo(),
                bab.getModelName(),
                Integer.toString(bab.getLine().getId())
        );

        record.setBalance(baln);

        try {
            for (int i = 0; i < balances.size(); i++) {
                Double avg = balances.get(i).getAverage();
                BeanUtils.setProperty(record, ("avg" + (i + 1)), avg);
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

        lineBalanceDAO.insert(record);

        //Don't throw exception when mail send fail
        try {
            this.checkLineBalanceAndSendMail(bab, maxBaln, baln);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 1;
    }

    public int update(LineBalancing pojo) {
        return lineBalanceDAO.update(pojo);
    }

    public int delete(LineBalancing pojo) {
        return lineBalanceDAO.delete(pojo);
    }

    public double caculateLineBalance(List<BabAvg> balances) throws JSONException {
        double balancing = 0.0;
        if (balances != null && !balances.isEmpty()) {
            double max = balances.stream().mapToDouble(i -> i.getAverage()).max().getAsDouble();
            double sum = balances.stream().mapToDouble(i -> i.getAverage()).sum();
            int babPeople = balances.size();
            balancing = (max != 0 ? caculateLineBalance(max, sum, babPeople) : 0.0);
        }
        return balancing;
    }

    public double caculateLineBalance(double max, double sum, int people) {
        return new BigDecimal(sum / (max * people))
                .setScale(balanceRoundDigit, BigDecimal.ROUND_HALF_DOWN)
                .doubleValue();
    }

    public void checkLineBalanceAndSendMail(Bab bab, LineBalancing linebaln, double balance) throws JSONException, MessagingException {
        //確定資料庫已經插入之後才送信
        int maxbln = parseDoubleToInteger(linebaln == null ? 0 : linebaln.getBalance());
        int currentbln = parseDoubleToInteger(balance);
        int diff = parseDoubleToInteger(balnDiff);
        if ((maxbln - currentbln) > diff) {
            sendMail(bab, maxbln, currentbln, diff);
            log.info("begin send mail because diff = "
                    + diff
                    + " and the current linebalance is "
                    + currentbln
                    + " ,the maxium in this line is "
                    + maxbln
            );
        }
    }

    private int parseDoubleToInteger(double d) {
        return (int) Math.round(d * 100);
    }

    public void sendMail(Bab bab, int maxbln, int currentbln, int diff) throws MessagingException {

        Line line = lineDAO.findByPrimaryKey(bab.getLine().getId());
        
        List<User> mailTo = userDAO.findLineOwner(line.getId());
        List<User> mailCc = userDAO.findByUserNotification("under_balance_alarm");

        String subject = "[藍燈系統]異常訊息(" + line.getName().trim() + ")";
        mailManager.sendMail(mailTo, mailCc, subject,
                new StringBuilder()
                        .append("<p>時間 <strong>")
                        .append(new Date())
                        .append("</strong> 統計到的線平衡率</p>")
                        .append("<p>與資料庫儲存的最佳平衡比對，下降差距到達了")
                        .append("百分之 ")
                        .append(diff)
                        .append(" </p><p>")
                        .append(maxbln)
                        .append("% ----> <font style='color:red'>")
                        .append(currentbln)
                        .append("</font>%</p>")
                        .append("<p>工單號碼: ")
                        .append(bab.getPo())
                        .append("</p><p>生產機種: ")
                        .append(bab.getModelName())
                        .append("</p><p>生產人數: ")
                        .append(bab.getPeople())
                        .append("</p><p>詳細歷史資料請上 <a href='")
                        .append("http://172.20.131.208/Line_Balancing/Login.aspx")
                        .append("'>線平衡電子化系統</a> 中的歷史紀錄做查詢</p>")
                        .toString());
    }

}
