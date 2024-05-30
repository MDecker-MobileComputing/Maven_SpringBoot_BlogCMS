package de.eldecker.dhbw.spring.blog.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping( "/app" )
public class ThymeleafController {

    @GetMapping( "/artikel/{artikelID}" )
    public String artikelAnzeigen( Model model,
                                   @PathVariable("artikelID") String artikelID ) {
        
        return "artikel-anzeige";
    }
    
}
