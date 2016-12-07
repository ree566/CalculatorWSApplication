/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 卡Cell各線別保持"唯一"用
 */
package com.advantech.servlet;

import com.advantech.entity.CellLine;
import com.advantech.helper.ParamChecker;
import com.advantech.service.BasicService;
import com.advantech.service.CellLineService;
import com.advantech.service.LineService;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Wei.Cheng
 */
@WebServlet(name = "CellLineServlet", urlPatterns = {"/CellLineServlet"})
public class CellLineServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CellLineServlet.class);

    private CellLineService cellLineService = null;

    private final String LOGIN = "LOGIN";
    private final String LOGOUT = "LOGOUT";

    ParamChecker pChecker = null;

    @Override
    public void init()
            throws ServletException {
        cellLineService = BasicService.getCellLineService();
        pChecker = new ParamChecker();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/plain");

        PrintWriter out = res.getWriter();
        String lineId = req.getParameter("lineId");
        String action = req.getParameter("action");

        if (pChecker.checkInputVal(lineId) && !lineId.equals("-1")) {

            int line = Integer.parseInt(lineId);

            String msg;
            switch (action) {
                case LOGIN:
                    CellLine cellLine = cellLineService.findOne(line);
                    if (cellLine.isOpened()) {
                        msg = "This line is already in used";
                    } else {
                        msg = cellLineService.login(line) ? "success" : "fail";
                    }
                    break;
                case LOGOUT:
                    msg = cellLineService.logout(line) ? "success" : "fail";
                    break;
                default:
                    msg = "未知的動作。";
                    break;
            }
            out.print(msg);

        } else {
            log.error("no data filter the check");
            out.print("no data filter the check");
        }
    }
}