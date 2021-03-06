<%@ page language="java" import="java.util.*,java.lang.Thread.*" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%--
Author:- Ramakanta(rks2286(at)gmail(dot)com)
Date Created: Mar 14 2010
PurPose: Online Java Thread Dump
Minimum Java version Required: 1.5.x
--%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
        <title>Thread Dump</title>
    </head>
    <body>
        <table width="100%" border="1" cellspacing="0" cellpadding="3" bordercolor="#000000">
            <tr>
                <td bgcolor="#E7E7EF" bordercolor="#000000" align="center" nowrap>
                    <font face="Verdana" size="+1">Thread Dumps&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>
                </td>
            </tr>
            <tr>
                <td bgcolor="#E7E7EF" bordercolor="#000000">
                    <%
                        out.print("---------------------------START-----------------------------------------<br>");
                        out.print("Generating Thread-Dump At:" + (new java.util.Date()).toString() + "<BR>");
                        out.println("---------------------------------------------------------------------<br>");
                        Map map = Thread.getAllStackTraces();
                        Iterator itr = map.keySet().iterator();
                        while (itr.hasNext()) {
                            Thread t = (Thread) itr.next();
                            StackTraceElement[] elem = (StackTraceElement[]) map.get(t);
                            out.print("\"" + t.getName() + "\"");
                            out.print(" Priority=" + t.getPriority());
                            out.print(" Thread Id=" + t.getId());
                            State s = t.getState();
                            String state = null;
                            String color = "000000";
                            String GREEN = "00FF00";
                            String RED = "FF0000";
                            String ORANGE = "FCA742";
                            switch (s) {
                                case NEW:
                                    state = "NEW";
                                    color = GREEN;
                                    break;
                                case BLOCKED:
                                    state = "BLOCKED";
                                    color = RED;
                                    break;
                                case RUNNABLE:
                                    state = "RUNNABLE";
                                    color = GREEN;
                                    break;
                                case TERMINATED:
                                    state = "TERMINATED";
                                    break;
                                case TIMED_WAITING:
                                    state = "TIME WAITING";
                                    color = ORANGE;
                                    break;
                                case WAITING:
                                    state = "WAITING";
                                    color = RED;
                                    break;
                            }
                            out.print("<font color=\"" + color + "\"> In State :</font>");
                            out.println(" " + state + "<BR>");
                            for (int i = 0; i < elem.length; i++) {
                                out.println("  at ");
                                out.print(elem[i].toString());
                                out.println("<BR>");
                            }
                            out.println("--------------------------------------------------------------------------<br>");
                        }
                        out.print("----------------------------FINISH--------------------------------------<br>");
                        out.print("Generated Thread-Dump At:" + (new java.util.Date()).toString() + "<BR>");
                        out.println("---------------------------------------------------------------------<br>");
                    %>
                </td>
            </tr>
        </table>
    </body>
</html>
