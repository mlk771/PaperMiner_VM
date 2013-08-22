/*
 * Copyright (c) 2013 The University of Queensland. This software is being developed 
 * for the UQ School of History, Philosophy, Religion and Classics (HPRC).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should hav	e received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package au.org.paperminer.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.org.paperminer.common.*;


/**
 * Servlet sends requested HTML page fragments when invoked directly, or
 * decodes error message codes when invoked as last step in a filter chain.
 * If no error code set by filter, and no response generated by filter,
 * just says "ok".
 */
@WebServlet("/PaperMiner")
public class PaperMinerServlet extends AbstractServlet 
{
    private static final long serialVersionUID = 3647305192690391219L;

    private String m_pmHomePath;
    private Hashtable<String, String> m_errors;


    /**
     * Default constructor. 
     */
    public PaperMinerServlet() 
    {
        super();
    }

    public void init () throws ServletException
    {
        super.init();
        m_pmHomePath = System.getProperty(PM_HOME);
        if (! loadErrorMessages()) {
            throw new ServletException("Error message load failed");
        }
    }
    

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        m_logger.info("PaperMinerServlet doGet");
        
        if (response.getContentType() == null) {
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();

            String page = (String) request.getParameter(REQ_PAGE);
            String epage = (String) request.getAttribute(ERROR_PAGE);
            m_logger.debug("page=" + page + "; epage=" + epage);
            
            if ((page == null) && (epage == null)) {
                pw.write("ok");
                m_logger.debug("Reply ok");
            }
            else if (page != null) {
                m_logger.debug("served " + page);
                pw.write(getPageContent(page));
            }
            else if (epage != null) {
                m_logger.debug("error response " + epage);
                if (m_errors.containsKey(epage)) {
                    pw.write(m_errors.get(epage));
                }
                else {
                    m_logger.error("Unknown error message key: " + epage);
                    pw.append("<p>Paper Miner has experieced an unknown error." +
                              "<p>Please inform the Paper Miner Administrator.");
                }
            }
            pw.close();
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        m_logger.info("doPost");
    }

    private boolean loadErrorMessages ()
    {
        boolean res = false;
        Hashtable<String, String> tmp = new Hashtable<String, String>();
        String path = m_pmHomePath + DATA_PATH;

        StringBuilder sb = null;
        BufferedReader reader = null;
        try {
            Pattern pat = Pattern.compile("^(e\\d{3,4}):$");
            reader = new BufferedReader(new FileReader(new File(path, ERROR_MSG_FILE)));
            sb = new StringBuilder();
            String key = null;
            String line = null;

            for (;;) {
                line = reader.readLine();
                if (line == null) {
                    tmp.put(key,  sb.toString());
                    break;
                }

                if ((line.length() > 0) && ! line.startsWith("#")) {
                    Matcher mat = pat.matcher(line);
                    if (! mat.matches()) {
                        sb.append(line).append("\n");
                    }
                    else {
                        if (key != null) {
                            tmp.put(key,  sb.toString());
                        }
                        key = mat.group(1);
                        //m_logger.debug(key);
                        sb.delete(0, sb.length());
                    }
                }
            }
            m_errors = tmp;
            res = true;
        } 
        catch (FileNotFoundException ex) {
            m_logger.error("Error message file not found at " + path + '/' + ERROR_MSG_FILE);
        }
        catch (IOException ex) {
            m_logger.error("Error reading Errors File", ex);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException ex) {
                m_logger.info("Error closing error message file", ex);
            }
        }
        return res;
    }

    private String getPageContent (String page)
    {
        StringBuilder sb = null;
        BufferedReader reader = null;
        String pageFile = page + ".txt";
        String path = m_pmHomePath + DATA_PATH;
        
        try {
            m_logger.debug("Fetching " + path + '/' + pageFile);
            reader = new BufferedReader(new FileReader(new File(path, pageFile)));
            sb = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
        } 
        catch (FileNotFoundException ex) {
            m_logger.error("File not found " + path + "/" + pageFile);
            sb.append("");
        }
        catch (IOException ex) {
            m_logger.error("Error reading File " + path + "/" + pageFile, ex);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException ex) {
                m_logger.info("Error closing File " + path + "/" + pageFile, ex);
            }
        }
        return sb.toString();
    }

}

// EOF

