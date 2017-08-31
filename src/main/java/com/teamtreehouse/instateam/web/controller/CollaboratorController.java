package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Collaborator;
import com.teamtreehouse.instateam.model.Project;
import com.teamtreehouse.instateam.model.Role;
import com.teamtreehouse.instateam.service.CollaboratorService;
import com.teamtreehouse.instateam.service.ProjectService;
import com.teamtreehouse.instateam.service.RoleService;
import com.teamtreehouse.instateam.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.OneToMany;
import javax.validation.Valid;
import java.util.List;

@Controller
public class CollaboratorController {

    @Autowired
    private CollaboratorService collaboratorService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectService projectService;

    @RequestMapping("/collaborators")
    @SuppressWarnings("unchecked")
    public String listCollaborators(Model model) {
        List<Collaborator> collaborators = collaboratorService.findAll();
        List<Role> roles = roleService.findAll();
        if (!model.containsAttribute("collaborator")) {
            model.addAttribute("collaborator", new Collaborator());
        }

        model.addAttribute("collaborators", collaborators);
        model.addAttribute("roles", roles);

        return "collaborator/index";
    }

    @RequestMapping(value = "/collaborators/add", method = RequestMethod.POST)
    public String addCollaborator(@Valid Collaborator collaborator, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.collaborator", result);
            redirectAttributes.addFlashAttribute("collaborator", collaborator);

            return "redirect:/collaborators";
        }
        collaboratorService.save(collaborator);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Collaborator successfully added!", FlashMessage.Status.SUCCESS));

        // TODO: Redirect browser to /roles
        return "redirect:/collaborators";
    }

    @RequestMapping("/collaborators/{collaboratorId}/edit")
    public String formEditCollaborator(@PathVariable Long collaboratorId, Model model) {
        // if the model already contains a "collaborator", the form will be pre-populated (from redirection)
        if (!model.containsAttribute("collaborator")) {
            model.addAttribute("collaborator", collaboratorService.findById(collaboratorId));
        }

        model.addAttribute("roles", roleService.findAll());
        return "collaborator/edit";
    }

    @RequestMapping(value = "collaborators/{collaboratorId}", method = RequestMethod.POST)
    public String updateCollaborator(@Valid Collaborator collaborator, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.collaborator", result);
            redirectAttributes.addFlashAttribute("collaborator", collaborator);

            return String.format("redirect:/collaborators/%s", collaborator.getId());
        }
        collaboratorService.save(collaborator);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Collaborator successfully updated!", FlashMessage.Status.SUCCESS));

        return "redirect:/collaborators";
    }

    @RequestMapping(value = "collaborators/{collaboratorId}/delete", method = RequestMethod.POST)
    public String deleteCollaborator(@PathVariable Long collaboratorId, RedirectAttributes redirectAttributes) {
        Collaborator collaborator = collaboratorService.findById(collaboratorId);
        List<Project> projects = projectService.findAll();

        for (Project project: projects) {
            if(project.getCollaborators().contains(collaborator)){
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Cannot delete. Collaborator is assigned to a project.", FlashMessage.Status.FAILURE));
                return String.format("redirect:/collaborators/%s/edit", collaboratorId);
            }
        }

        collaboratorService.delete(collaborator);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Collaborator successfully deleted!", FlashMessage.Status.SUCCESS));

        return "redirect:/collaborators";
    }
}
