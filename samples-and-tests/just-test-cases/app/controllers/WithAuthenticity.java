package controllers;

import yalp.*;
import yalp.mvc.*;

import java.util.*;

import models.*;

public class WithAuthenticity extends Controller {

    public static void index() {
        render();
    }
    
    public static void needAuthenticity() {
        checkAuthenticity();
        renderText("OK");
    }
    
}

