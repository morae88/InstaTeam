package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Collaborator;
import com.teamtreehouse.instateam.model.Project;
import com.teamtreehouse.instateam.model.Role;
import com.teamtreehouse.instateam.service.CollaboratorService;
import com.teamtreehouse.instateam.service.ProjectService;
import com.teamtreehouse.instateam.service.RoleService;
import com.teamtreehouse.instateam.web.FlashMessage;
import com.teamtreehouse.instateam.web.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.RollbackException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CollaboratorService collaboratorService;

    @RequestMapping("/")
    @SuppressWarnings("unchecked")
    public String listProjects(Model model) {
        List<Project> projects = projectService.findAll();

        if(!model.containsAttribute("project")){
            model.addAttribute("project", new Project());
        }

        model.addAttribute("projects", projects);
        model.addAttribute("statuses", Status.values());

        return "project/index";
    }

    @RequestMapping("/projects/add")
    public String addNewProject(Model model) {
        List<Role> roles = roleService.findAll();
        if(!model.containsAttribute("project")){
            model.addAttribute("project", new Project());
        }

        model.addAttribute("statuses", Status.values());
        model.addAttribute("roles",roles);
        model.addAttribute("action", "/projects/add");

        return "project/edit";
    }


    @RequestMapping(value = "/projects/add", method = RequestMethod.POST)
    public String addProject(@Valid Project project, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.project", result);
            redirectAttributes.addFlashAttribute("project", project);

            return "redirect:/projects/add";
        }
        projectService.save(project);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project successfully added!", FlashMessage.Status.SUCCESS));

        return "redirect:/";
    }

    @RequestMapping("/{projectId}")
    public String project(@PathVariable Long projectId, Model model) {
        Project project = projectService.findById(projectId);
        List<Role> roles = project.getRolesNeeded();
        List<Collaborator> collaborators = project.getCollaborators();
        List<Role> noCollaborators = new ArrayList<>();

        noCollaborators.addAll(roles);
        for (Collaborator c : collaborators) {
            noCollaborators.remove(c.getRole());
        }


        model.addAttribute("project", project);
        model.addAttribute("roles", roles);
        model.addAttribute("collaborators", collaborators);
        model.addAttribute("unassigned", noCollaborators);


        return "project/detail";
    }

    @RequestMapping("/{projectId}/edit")
    public String editProject(@PathVariable Long projectId, Model model) {
        List<Role> roles = roleService.findAll();
        if(!model.containsAttribute("project")){
            model.addAttribute("project", projectService.findById(projectId));
        }

        model.addAttribute("statuses", Status.values());
        model.addAttribute("roles",roles);
        model.addAttribute("action", String.format("/%s/edit", projectId));

        return "project/edit";
    }


    @RequestMapping(value = "/{projectId}/edit", method = RequestMethod.POST)
    public String editProject(@Valid Project project, @PathVariable Long projectId, BindingResult result, RedirectAttributes redirectAttributes) {
        List<Collaborator> collaborators = projectService.findById(projectId).getCollaborators();
        List<Role> assignedRoles = project.getRolesNeeded();
        List<Collaborator> toRemove = new ArrayList<>();

        for (Collaborator col: collaborators) {
            if(!assignedRoles.contains(col.getRole())){
                toRemove.add(col);
            }
        }

        collaborators.removeAll(toRemove);

        project.setCollaborators(collaborators);
        projectService.save(project);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.project", result);
            redirectAttributes.addFlashAttribute("project", project);

            return "redirect:/{projectId}/edit";
        }


        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project successfully edited!", FlashMessage.Status.SUCCESS));

        // TODO: Redirect browser to /roles
        return "redirect:/";
    }

    @RequestMapping("/{projectId}/collaborators")
    public String projectCollaborators(@PathVariable Long projectId, Model model) {
        if(!model.containsAttribute("project")) {
            model.addAttribute("project",projectService.findById(projectId));

        }
        List<Role> roles = projectService.findById(projectId).getRolesNeeded();
        List<Collaborator> collaborators = projectService.findById(projectId).getCollaborators();
        List<Collaborator> availableCollaborators = collaboratorService.findAll();




        model.addAttribute("roles", roles);
        model.addAttribute("availableCollaborators",availableCollaborators);
        model.addAttribute("collaborators", collaborators);

        return "project/collaborators";
    }

    @RequestMapping(value = "/{projectId}/editCollaborators", method = RequestMethod.POST)
    public String editProjectCollaborators(@PathVariable Long projectId, Project project, BindingResult result, RedirectAttributes redirectAttributes) {
        Project saved = projectService.findById(projectId);

        List<Collaborator> collaborators = new ArrayList<>();

        for (Collaborator collaborator: project.getCollaborators()) {
            if(collaborator.getId() != 0){
                collaborators.add(collaboratorService.findById(collaborator.getId()));
            }
        }
        saved.setCollaborators(collaborators);
        projectService.save(saved);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.project", result);
            redirectAttributes.addFlashAttribute("project", project);

            return String.format("redirect:/%s/collaborators",project.getId());
        }



        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project collaborators successfully updated!", FlashMessage.Status.SUCCESS));

        // TODO: Redirect browser to project collaborators
        return "redirect:/{projectId}";
    }

    @RequestMapping(value = "/{projectId}/delete", method = RequestMethod.POST)
    public String deleteProject(@PathVariable("projectId") Long projectId, RedirectAttributes redirectAttributes) {
        Project project = projectService.findById(projectId);
        project.setCollaborators(new ArrayList<>());
        project.setRolesNeeded(new ArrayList<>());
        projectService.delete(project);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project successfully deleted!", FlashMessage.Status.SUCCESS));

        return "redirect:/";
    }
}
