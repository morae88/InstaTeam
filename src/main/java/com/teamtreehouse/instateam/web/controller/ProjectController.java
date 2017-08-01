package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Project;
import com.teamtreehouse.instateam.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @RequestMapping("/")
    @SuppressWarnings("unchecked")
    public String listProjects(Model model) {
        List<Project> projects = projectService.findAll();

        if(!model.containsAttribute("project")){
            model.addAttribute("project", new Project());
        }

        model.addAttribute("projects", projects);

        return "project/index";
    }
}
