package controllers;

import helpers.CheatSheetHelper;

import helpers.LangMenuHelper;
import helpers.LangMenuHelper.*;
import yalp.Logger;
import yalp.Yalp;
import yalp.libs.IO;
import yalp.mvc.Controller;
import yalp.mvc.Http;
import yalp.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YalpDocumentation extends Controller {
    
    public static void index() throws Exception {
        Http.Header header = request.headers.get("accept-language");
        String docLang = header!=null? header.value().split(",")[0] : "";
        docLang = docLang.length()>2? docLang.substring(0,2) : docLang;
        page("home", null, docLang);
    }

    @SuppressWarnings("unchecked")
    public static void page(String id, String module, String docLang) throws Exception {
        String docLangDir = (docLang != null && (!"en".equalsIgnoreCase(docLang) && !docLang.matches("en-.*") ) ) ? "_" + docLang + "/" : "/";

        File page = new File(Yalp.frameworkPath, "documentation/manual" + docLangDir + id + ".textile");
        if(!page.exists()){
            page = new File(Yalp.frameworkPath, "documentation/manual/" + id + ".textile");
        }

        if (module != null) {
            page = new File(Yalp.modules.get(module).getRealFile(), "documentation/manual/" + id + ".textile");
        }

        if (!page.exists()) {
            notFound("Manual page for " + id + " not found");
        }
        String textile = IO.readContentAsString(page);
        String html = toHTML(textile);
        String title = getTitle(textile);
        
        List<String> modules = new ArrayList();
        List<String> apis = new ArrayList();
        if (id.equals("home") && module == null) {
            for (String key : Yalp.modules.keySet()) {
                VirtualFile mr = Yalp.modules.get(key);
                VirtualFile home = mr.child("documentation/manual/" + "home.textile");
                if (home.exists()) {
                    modules.add(key);
                }
                if (mr.child("documentation/api/index.html").exists()) {
                    apis.add(key);
                }
            }
        }
        List<LangMenu> langMenuList = LangMenuHelper.getMenuList();
        render(id, html, title, modules, apis, module, docLang, langMenuList);
    }

    @SuppressWarnings("unchecked")
    public static void cheatSheet(String category, String docLang) {
        File[] sheetFiles = CheatSheetHelper.getSheets(category, docLang);
        if (sheetFiles != null) {
            List<String> sheets = new ArrayList<String>();

            for (File file : sheetFiles) {
                sheets.add(toHTML(IO.readContentAsString(file)));
            }

            String title = CheatSheetHelper.getCategoryTitle(category);
            Map<String, String> otherCategories = CheatSheetHelper.listCategoriesAndTitles(docLang);

            render(title, otherCategories, sheets, docLang);
        }
        notFound("Cheat sheet directory not found");
    }

    public static void image(String name, String module, String lang) {
        File image = new File(Yalp.frameworkPath, "documentation/images/" + name + ".png");
        if (module != null) {
            image = new File(Yalp.modules.get(module).getRealFile(), "documentation/images/" + name + ".png");
        }
        if (!image.exists()) {
            notFound();
        }
        renderBinary(image);
    }

    public static void file(String name, String module, String lang) {
        File file = new File(Yalp.frameworkPath, "documentation/files/" + name);
        if (module != null) {
            file = new File(Yalp.modules.get(module).getRealFile(), "documentation/files/" + name);
        }
        if (!file.exists()) {
            notFound();
        }
        renderBinary(file);
    }    
    
    static String toHTML(String textile) {
        String html = new jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser(new jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage()).parseToHtml(textile);
        html = html.substring(html.indexOf("<body>") + 6, html.lastIndexOf("</body>"));
        return html;
    }
    
    static String getTitle(String textile) {
        if (textile.length() == 0) {
            return "";
        }
        return textile.split("\n")[0].substring(3).trim();
    }
    
}