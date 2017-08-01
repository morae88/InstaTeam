package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Collaborator;
import com.teamtreehouse.instateam.service.CollaboratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class CollaboratorController {

    @Autowired
    private CollaboratorService collaboratorService;

    @RequestMapping("/collaborators")
    @SuppressWarnings("unchecked")
    public String listCollaborators(Model model){
        List<Collaborator> collaborators = collaboratorService.findAll();
        if(!model.containsAttribute("collaborator")){
            model.addAttribute("collaborator", new Collaborator());
        }

        model.addAttribute("collaborators", collaborators);

        return "collaborator/index";
    }
}
