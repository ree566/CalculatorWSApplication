/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.helper.MailSend;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author Wei.Cheng
 */
public class TestClass2 {

    static int i = 1, j = i;

    public static void main(String arg0[]) {
        MailSend m = MailSend.getInstance();
        try {
            m.sendMail("Wei.Cheng", "test", "testMail");
        } catch (MessagingException ex) {
            System.out.println(ex);
        }

    }

}
